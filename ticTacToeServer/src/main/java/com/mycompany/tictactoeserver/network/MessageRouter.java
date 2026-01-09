/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import com.google.gson.Gson;
import static com.mycompany.tictactoeserver.network.MessageType.ACCEPT_REQUEST;
import com.mycompany.tictactoeserver.network.dtos.PlayerStatsDto;
import com.mycompany.tictactoeserver.network.dtos.ErrorPayload;
import com.mycompany.tictactoeserver.network.request.InviteRequest;
import com.mycompany.tictactoeserver.network.request.RegisterRequest;
import com.mycompany.tictactoeserver.network.response.InviteResponse;
import com.mycompany.tictactoeserver.network.response.ResultPayload;
import java.util.List;

/**
 *
 * @author yasse
 */
public final class MessageRouter {

    private final Gson gson;
    private final ClientRegistry registry;
    private final GameService gameService;

    public MessageRouter(Gson gson, ClientRegistry registry, GameService auth) {
        this.gson = gson;
        this.registry = registry;
        this.gameService = auth;
    }

    public void handle(ClientSession session, NetworkMessage msg) {
        try {
            switch (msg.getType()) {
                case REGISTER:
                    handleRegister(session, msg);
                    break;

                case LOGIN:
                    handleLogin(session, msg);
                    break;

                case GET_ONLINE_PLAYERS:
                    handleGetOnlinePlayers(session);
                    break;

                case SEND_REQUEST:
                    handleGameInvite(session, msg);
                    break;

                case GAME_MOVE:
                    handleGameMove(session, msg);
                    break;

                case DISCONNECT:
                    onDisconnect(session);
                    break;
                case ACCEPT_REQUEST:
                    handleAcceptRequest(session, msg);
                case DECLINE_REQUEST:
                    handleDeclineRequest(session, msg);

                default:
                    sendError(session, "UNKNOWN_TYPE", "Unknown message type: " + msg.getType());
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
            sendError(session, "SERVER_ERROR", "Internal server error");
        }
    }

    public void onDisconnect(ClientSession session) {
        String username = session.getUsername();
        if (username != null) {
            gameService.updatePlayerState(username, 0);
            registry.remove(session);
            System.out.println("User disconnected: " + username);
            broadcastOnlinePlayers();
            gameService.updateStats();
        }
        session.close();
    }

    private void handleRegister(ClientSession session, NetworkMessage msg) {
        RegisterRequest request = gson.fromJson(msg.getPayload(), RegisterRequest.class);

        ResultPayload result = gameService.register(request);

        if (result.isSuccess()) {
            String username = request.getUsername();
            if (registry.isOnline(username)) {
                ClientSession existingSession = registry.get(username);
                if (existingSession != null) {
                    sendError(existingSession, "DUPLICATE_LOGIN", "Logged in from another location");
                    registry.remove(existingSession);
                    existingSession.close();
                }
            }
            session.setUsername(username);
            registry.addOnline(username, session);
            System.out.println("User registered and logged in: " + username);
            session.send(new NetworkMessage(
                    MessageType.REGISTER_RESULT,
                    "Server",
                    username,
                    gson.toJsonTree(result)
            ));
            broadcastOnlinePlayers();

        } else {
            session.send(new NetworkMessage(
                    MessageType.REGISTER_RESULT,
                    "Server",
                    request.getUsername(),
                    gson.toJsonTree(result)
            ));
        }
    }

    private void handleLogin(ClientSession session, NetworkMessage msg) {
        RegisterRequest request = gson.fromJson(msg.getPayload(), RegisterRequest.class);
        String username = request.getUsername();

        ResultPayload authResult = gameService.login(request);
        if (!authResult.isSuccess()) {
            session.send(new NetworkMessage(
                    MessageType.LOGIN_RESULT,
                    "Server",
                    username,
                    gson.toJsonTree(authResult)
            ));
            return;
        }

        if (registry.isOnline(username)) {
            ResultPayload error = new ResultPayload(false, "USER_ALREADY_ONLINE",
                    "This user is already logged in from another location.");
            session.send(new NetworkMessage(
                    MessageType.LOGIN_RESULT,
                    "Server",
                    username,
                    gson.toJsonTree(error)
            ));
            return;
        }

        session.setUsername(username);
        registry.addOnline(username, session);

        session.send(new NetworkMessage(
                MessageType.LOGIN_RESULT,
                "Server",
                username,
                gson.toJsonTree(new ResultPayload(true, "OK", "Login successful"))
        ));

        System.out.println("User logged in: " + username);
        broadcastOnlinePlayers();
    }

    private void handleGetOnlinePlayers(ClientSession session) {
        if (!isAuthenticated(session)) {
            return;
        }

//        OnlinePlayersUpdate update = new OnlinePlayersUpdate(registry.onlineUsernames());
//        session.send(new NetworkMessage(
//            MessageType.ONLINE_PLAYERS_UPDATE,
//            "Server",
//            session.getUsername(),
//            gson.toJsonTree(update)
//        ));
        broadcastOnlinePlayers();

        System.out.println("Handled GET_ONLINE_PLAYERS for: " + session.getUsername());

        System.out.print("get online players");
        String userName = session.getUsername();
        List<PlayerStatsDto> players = gameService.getOnlineAndInGamePlayers(userName);
        OnlinePlayersUpdate update = new OnlinePlayersUpdate(players);
        session.send(new NetworkMessage(
                MessageType.ONLINE_PLAYERS_UPDATE,
                "Server",
                userName,
                gson.toJsonTree(update)
        ));
    }

    private void handleGameInvite(ClientSession session, NetworkMessage msg) {
        if (!isAuthenticated(session)) {
            return;
        }
        InviteRequest inviteRequest = gson.fromJson(msg.getPayload(), InviteRequest.class);
        String targetUsername = inviteRequest.getTargetUsername();
        if (targetUsername == null || targetUsername.trim().isEmpty()) {
            sendError(session, "INVALID_INPUT", "Target username is required");
            return;
        }

        ClientSession targetSession = registry.get(targetUsername);
        if (targetSession == null || !targetSession.isConnected()) {
            sendError(session, "USER_OFFLINE", "User '" + targetUsername + "' is not online");
            return;
        }
        targetSession.send(new NetworkMessage(
                MessageType.SEND_REQUEST,
                session.getUsername(),
                targetUsername,
                gson.toJsonTree(inviteRequest)
        ));
        gameService.changePlayerState(targetUsername, 3);
        gameService.changePlayerState(inviteRequest.getSenderUsername(), 3);
        System.out.println("Game invite from " + session.getUsername() + " to " + targetUsername);
        broadcastOnlinePlayers();
    }

    private void handleGameMove(ClientSession session, NetworkMessage msg) {
        if (!isAuthenticated(session)) {
            return;
        }

        String opponentUsername = msg.getReceiver();
        if (opponentUsername == null || opponentUsername.trim().isEmpty()) {
            sendError(session, "INVALID_INPUT", "Opponent username is required");
            return;
        }

        ClientSession opponentSession = registry.get(opponentUsername);
        if (opponentSession == null || !opponentSession.isConnected()) {
            sendError(session, "USER_OFFLINE", "Opponent is not online");
            return;
        }

        // Forward the move to opponent
        opponentSession.send(new NetworkMessage(
                MessageType.GAME_MOVE,
                session.getUsername(),
                opponentUsername,
                msg.getPayload()
        ));
    }

    private void broadcastOnlinePlayers() {
        for (ClientSession session : registry.allSessions()) {
            if (session.isConnected() && session.getUsername() != null) {
                List<PlayerStatsDto> players = gameService.getOnlineAndInGamePlayers(session.getUsername());
                OnlinePlayersUpdate update = new OnlinePlayersUpdate(players);

                session.send(new NetworkMessage(
                        MessageType.ONLINE_PLAYERS_UPDATE,
                        "Server",
                        session.getUsername(),
                        gson.toJsonTree(update)
                ));
            }
        }
    }

    private boolean isAuthenticated(ClientSession session) {
        if (session.getUsername() == null) {
            sendError(session, "NOT_AUTHENTICATED", "You must login first");
            return false;
        }
        return true;
    }

    private void sendError(ClientSession session, String code, String message) {
        session.send(new NetworkMessage(
                MessageType.ERROR,
                "Server",
                session.getUsername(),
                gson.toJsonTree(new ErrorPayload(code, message))
        ));
    }

    private void handleAcceptRequest(ClientSession session, NetworkMessage msg) {
        if (!isAuthenticated(session)) {
            return;
        }

        InviteResponse response = gson.fromJson(msg.getPayload(), InviteResponse.class);
        String senderUsername = response.getSenderUsername();

        if (senderUsername == null || senderUsername.trim().isEmpty()) {
            sendError(session, "INVALID_INPUT", "Sender username is required");
            return;
        }

        ClientSession senderSession = registry.get(senderUsername);
        if (senderSession == null || !senderSession.isConnected()) {
            sendError(session, "USER_OFFLINE", "User '" + senderUsername + "' is not online");
            return;
        }
        gameService.updatePlayerState(session.getUsername(), 2);
        gameService.updatePlayerState(senderUsername, 2);

        senderSession.send(new NetworkMessage(
                MessageType.ACCEPT_REQUEST,
                session.getUsername(),
                senderUsername,
                msg.getPayload()
        ));
        System.out.println(session.getUsername() + " accepted invite from " + senderUsername);
        broadcastOnlinePlayers();
    }

    private void handleDeclineRequest(ClientSession session, NetworkMessage msg) {
        if (!isAuthenticated(session)) {
            return;
        }

        InviteResponse response = gson.fromJson(msg.getPayload(), InviteResponse.class);
        String senderUsername = response.getSenderUsername();

        if (senderUsername == null || senderUsername.trim().isEmpty()) {
            sendError(session, "INVALID_INPUT", "Sender username is required");
            return;
        }

        ClientSession senderSession = registry.get(senderUsername);
        if (senderSession == null || !senderSession.isConnected()) {
            return;
        }

        senderSession.send(new NetworkMessage(
                MessageType.DECLINE_REQUEST,
                session.getUsername(),
                senderUsername,
                msg.getPayload()
        ));
        gameService.updatePlayerState(session.getUsername(), 1);
        gameService.updatePlayerState(senderUsername, 1);
        System.out.println(session.getUsername());
    }
}
