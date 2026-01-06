/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import com.google.gson.Gson;
import com.mycompany.tictactoeserver.network.dtos.ErrorPayload;
import com.mycompany.tictactoeserver.network.request.RegisterRequest;
import com.mycompany.tictactoeserver.network.response.ResultPayload;

/**
 *
 * @author yasse
 */
public final class MessageRouter {
   private final Gson gson;
    private final ClientRegistry registry;
    private final AuthService auth;

    public MessageRouter(Gson gson, ClientRegistry registry, AuthService auth) {
        this.gson = gson;
        this.registry = registry;
        this.auth = auth;
    }

public void handle(ClientSession session, NetworkMessage msg) {
    switch (msg.getType()) {
        case REGISTER:
            onRegister(session, msg);
            break;
            
        case LOGIN:
            onLogin(session, msg);
            break;
            
        case GET_ONLINE_PLAYERS:
            onGetOnlinePlayers(session);
            break;
            
        case SEND_REQUEST:
            onInvite(session, msg);
            break;
            
        case GAME_MOVE:
            onGameMove(session, msg);
            break;
            
        case DISCONNECT:
            onDisconnect(session);
            break;
            
        default:
            sendError(session, "UNKNOWN_TYPE", "Unknown message type: " + msg.getType());
            break;
    }
}

    public void onDisconnect(ClientSession session) {
        registry.remove(session);
        broadcastOnlinePlayers();
        session.close();
    }

    private void onRegister(ClientSession session, NetworkMessage msg) {
        RegisterRequest cmd = gson.fromJson(msg.getPayload(), RegisterRequest.class);
        ResultPayload result = auth.register(cmd);

        session.send(new NetworkMessage(
                MessageType.REGISTER_RESULT,
                "Server",
                null,
                gson.toJsonTree(result)
        ));

        if (!result.isSuccess()) {
            sendError(session, result.getCode(), result.getMessage());
        }
    }

    private void onLogin(ClientSession session, NetworkMessage msg) {
        RegisterRequest cmd = gson.fromJson(msg.getPayload(), RegisterRequest.class);
        String username = normalize(cmd.getUsername());

        ResultPayload authResult = auth.login(cmd);
        if (!authResult.isSuccess()) {
            session.send(new NetworkMessage(MessageType.LOGIN_RESULT, "Server", null, gson.toJsonTree(authResult)));
            return;
        }
        if (!registry.addOnline(username, session)) {
            ResultPayload r = new ResultPayload(false, "USER_ALREADY_ONLINE", "This user is already online.");
            session.send(new NetworkMessage(MessageType.LOGIN_RESULT, "Server", null, gson.toJsonTree(r)));
            return;
        }

        session.setUsername(username);

        session.send(new NetworkMessage(
                MessageType.LOGIN_RESULT,
                "Server",
                null,
                gson.toJsonTree(new ResultPayload(true, "OK", "Login successful."))
        ));

        broadcastOnlinePlayers();
    }

    private void onGetOnlinePlayers(ClientSession session) {
        OnlinePlayersUpdate update = new OnlinePlayersUpdate(registry.onlineUsernames());
        session.send(new NetworkMessage(
                MessageType.ONLINE_PLAYERS_UPDATE,
                "Server",
                session.getUsername(),
                gson.toJsonTree(update)
        ));
    }

    private void onInvite(ClientSession session, NetworkMessage msg) {
        if (session.getUsername() == null) {
            sendError(session, "NOT_LOGGED_IN", "Login first.");
            return;
        }
        String target = normalize(msg.getReceiver());
        if (target.isEmpty()) {
            sendError(session, "INVALID_INPUT", "Receiver username required.");
            return;
        }

        ClientSession targetSession = registry.get(target);
        if (targetSession == null) {
            sendError(session, "USER_OFFLINE", "User is not online: " + target);
            return;
        }
        targetSession.send(new NetworkMessage(
                MessageType.SEND_REQUEST,
                session.getUsername(),
                target,
                msg.getPayload() 
        ));
    }

    private void onGameMove(ClientSession session, NetworkMessage msg) {
        if (session.getUsername() == null) {
            sendError(session, "NOT_LOGGED_IN", "Login first.");
            return;
        }

        String opponent = normalize(msg.getReceiver());
        ClientSession opp = registry.get(opponent);
        if (opp == null) {
            sendError(session, "USER_OFFLINE", "Opponent is not online.");
            return;
        }
        opp.send(new NetworkMessage(
                MessageType.GAME_MOVE,
                session.getUsername(),
                opponent,
                msg.getPayload()
        ));
    }

    private void broadcastOnlinePlayers() {
        OnlinePlayersUpdate update = new OnlinePlayersUpdate(registry.onlineUsernames());
        NetworkMessage event = new NetworkMessage(
                MessageType.ONLINE_PLAYERS_UPDATE,
                "Server",
                null,
                gson.toJsonTree(update)
        );

        for (ClientSession s : registry.allSessions()) {
            s.send(event);
        }
    }

    private void sendError(ClientSession session, String code, String message) {
        session.send(new NetworkMessage(
                MessageType.ERROR,
                "Server",
                session.getUsername(),
                gson.toJsonTree(new ErrorPayload(code, message))
        ));
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }
}
