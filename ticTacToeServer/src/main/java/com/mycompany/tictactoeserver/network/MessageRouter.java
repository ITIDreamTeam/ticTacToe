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
import com.mycompany.tictactoeserver.network.dtos.GameMoveDto;
import com.mycompany.tictactoeserver.network.request.ChangePasswordRequest;
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

    public MessageRouter(Gson gson, ClientRegistry registry, GameService gameService) {
        this.gson = gson;
        this.registry = registry;
        this.gameService = gameService;
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

                case FIND_MATCH:
                    handleFindMatch(session);
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
                    break;

                case DECLINE_REQUEST:
                    handleDeclineRequest(session, msg);
                    break;

                case SURRENDER:
                    handleSurrender(session);
                    break;

                case CHANGE_PASSWORD:
                    handleChangePassword(session, msg);
                    break;

                default:
                    sendError(session, "UNKNOWN_TYPE", "Unknown message type: " + msg.getType());
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
            e.printStackTrace();
            sendError(session, "SERVER_ERROR", "Internal server error");
        }
    }

    public void onDisconnect(ClientSession session) {
        String username = session.getUsername();
        if (username != null) {
            System.out.println("User disconnecting: " + username);
            
            gameService.handlePlayerDisconnect(session);
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
                gson.toJsonTree(authResult)
        ));

        System.out.println("User logged in: " + username);
        broadcastOnlinePlayers();
    }

    private void handleGetOnlinePlayers(ClientSession session) {
        if (!isAuthenticated(session)) {
            return;
        }

        String userName = session.getUsername();
        List<PlayerStatsDto> players = gameService.getOnlineAndInGamePlayers(userName);
        OnlinePlayersUpdate update = new OnlinePlayersUpdate(players);
        
        session.send(new NetworkMessage(
                MessageType.ONLINE_PLAYERS_UPDATE,
                "Server",
                userName,
                gson.toJsonTree(update)
        ));
        
        System.out.println("Sent online players to: " + userName);
    }

    private void handleFindMatch(ClientSession session) {
        if (!isAuthenticated(session)) {
            return;
        }
        gameService.findMatch(session);
        System.out.println("User " + session.getUsername() + " is looking for a match.");
    }

    private void handleGameInvite(ClientSession session, NetworkMessage msg) {
        if (!isAuthenticated(session)) {
            return;
        }
        
        InviteRequest inviteRequest = gson.fromJson(msg.getPayload(), InviteRequest.class);
        String targetUsername = inviteRequest.getTargetUsername();
        String senderUsername = session.getUsername();
        
        if (targetUsername == null || targetUsername.trim().isEmpty()) {
            sendError(session, "INVALID_INPUT", "Target username is required");
            return;
        }

        ClientSession targetSession = registry.get(targetUsername);
        if (targetSession == null || !targetSession.isConnected()) {
            sendError(session, "USER_OFFLINE", "User '" + targetUsername + "' is not online");
            gameService.updatePlayerState(senderUsername, 1);
            broadcastOnlinePlayers();
            return;
        }

        gameService.updatePlayerState(targetUsername, 3);
        gameService.updatePlayerState(senderUsername, 3);

        targetSession.send(new NetworkMessage(
                MessageType.SEND_REQUEST,
                senderUsername,
                targetUsername,
                gson.toJsonTree(inviteRequest)
        ));

        System.out.println("Game invite from " + senderUsername + " to " + targetUsername);
        broadcastOnlinePlayers();
    }

    private void handleGameMove(ClientSession session, NetworkMessage msg) {
        if (!isAuthenticated(session)) {
            return;
        }

        GameMoveDto move = gson.fromJson(msg.getPayload(), GameMoveDto.class);
        gameService.handleGameMove(session, move);
    }

    private void handleAcceptRequest(ClientSession session, NetworkMessage msg) {
        if (!isAuthenticated(session)) {
            return;
        }

        InviteResponse response = gson.fromJson(msg.getPayload(), InviteResponse.class);
        String senderUsername = response.getSenderUsername();
        String receiverUsername = session.getUsername();

        if (senderUsername == null || senderUsername.trim().isEmpty()) {
            sendError(session, "INVALID_INPUT", "Sender username is required");
            gameService.updatePlayerState(receiverUsername, 1);
            broadcastOnlinePlayers();
            return;
        }

        ClientSession senderSession = registry.get(senderUsername);
        if (senderSession == null || !senderSession.isConnected()) {
            sendError(session, "USER_OFFLINE", "User '" + senderUsername + "' is not online");
            gameService.updatePlayerState(receiverUsername, 1);
            broadcastOnlinePlayers();
            return;
        }

        gameService.updatePlayerState(receiverUsername, 2);
        gameService.updatePlayerState(senderUsername, 2);

        senderSession.send(new NetworkMessage(
                MessageType.ACCEPT_REQUEST,
                receiverUsername,
                senderUsername,
                msg.getPayload()
        ));

        gameService.startPrivateGame(senderSession, session, response.isRecordGame());

        System.out.println(receiverUsername + " accepted invite from " + senderUsername);
        broadcastOnlinePlayers();
    }

    private void handleDeclineRequest(ClientSession session, NetworkMessage msg) {
        if (!isAuthenticated(session)) {
            return;
        }

        InviteResponse response = gson.fromJson(msg.getPayload(), InviteResponse.class);
        String senderUsername = response.getSenderUsername();
        String receiverUsername = session.getUsername();

        if (senderUsername == null || senderUsername.trim().isEmpty()) {
            sendError(session, "INVALID_INPUT", "Sender username is required");
            gameService.updatePlayerState(receiverUsername, 1);
            broadcastOnlinePlayers();
            return;
        }

        ClientSession senderSession = registry.get(senderUsername);
        
        gameService.updatePlayerState(receiverUsername, 1);
        gameService.updatePlayerState(senderUsername, 1);

        if (senderSession != null && senderSession.isConnected()) {
            senderSession.send(new NetworkMessage(
                    MessageType.DECLINE_REQUEST,
                    receiverUsername,
                    senderUsername,
                    msg.getPayload()
            ));
        }

        System.out.println(receiverUsername + " declined/cancelled invite from " + senderUsername);
        broadcastOnlinePlayers();
    }

    private void handleSurrender(ClientSession session) {
        if (!isAuthenticated(session)) {
            return;
        }

        String username = session.getUsername();
        System.out.println(username + " surrendered");

        gameService.handleSurrender(session);
        
        gameService.updatePlayerState(username, 1);
        
        broadcastOnlinePlayers();
        gameService.updateStats();
    }

    private void handleChangePassword(ClientSession session, NetworkMessage msg) {
        ChangePasswordRequest request = gson.fromJson(msg.getPayload(), ChangePasswordRequest.class);

        if (!session.getUsername().equals(request.getUsername())) {
            sendError(session, "UNAUTHORIZED", "You cannot change another user's password.");
            return;
        }

        ResultPayload result = gameService.changePassword(request);

        session.send(new NetworkMessage(
                MessageType.CHANGE_PASSWORD_RESULT,
                "Server",
                session.getUsername(),
                gson.toJsonTree(result)
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
}
