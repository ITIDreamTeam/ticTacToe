/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import com.google.gson.Gson;
import com.mycompany.tictactoeserver.data.dataSource.dao.GameDAO;
import com.mycompany.tictactoeserver.data.dataSource.dao.PlayerDaoImpl;
import com.mycompany.tictactoeserver.data.model.ActiveGame;
import com.mycompany.tictactoeserver.data.model.GameEngine;
import com.mycompany.tictactoeserver.data.model.Player;
import com.mycompany.tictactoeserver.network.dtos.ErrorPayload;
import com.mycompany.tictactoeserver.network.dtos.GameMoveDto;
import com.mycompany.tictactoeserver.network.dtos.GameStartDto;
import com.mycompany.tictactoeserver.network.dtos.PlayerStatsDto;
import com.mycompany.tictactoeserver.network.request.ChangePasswordRequest;
import com.mycompany.tictactoeserver.network.request.RegisterRequest;
import com.mycompany.tictactoeserver.network.response.ResultPayload;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author yasse
 */
public final class GameService {

    private final PlayerDaoImpl playerDao;
    private final GameDAO gameDao;
    private Runnable onStatsChanged;
    private final Queue<ClientSession> matchmakingQueue = new LinkedList<>();
    private final Map<String, ActiveGame> activeGames = new HashMap<>();
    private final Gson gson = new Gson();

    public GameService(PlayerDaoImpl playerDao, GameDAO gameDao) {
        this.playerDao = playerDao;
        this.gameDao = gameDao;
    }

    public ResultPayload register(RegisterRequest request) {
        String userName = clean(request.getUsername());
        String email = request.getEmail();
        String password = request.getPassword() == null ? "" : request.getPassword();

        if (userName.isEmpty() || password.length() < 4) {
            return new ResultPayload(false, "INVALID_INPUT", "Username required, password min 4 chars.");
        }
        
        System.out.println("Registering: " + userName + " " + email);
        
        if (playerDao.isUsernameExist(userName)) {
            return new ResultPayload(false, "DUPLICATE_USERNAME", "Username already exists.");
        }
        
        Player player = new Player(userName, email, password);
        player.setScore(300);
        
        try {
            playerDao.register(player);
            ResultPayload response = new ResultPayload(true, "OK", "Registered successfully.");
            response.setJsonPayload(gson.toJson(player));
            return response;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new ResultPayload(false, "DB_ERROR", "Database error occurred");
        }
    }

    public ResultPayload login(RegisterRequest request) {
        String userName = clean(request.getUsername());
        String password = request.getPassword() == null ? "" : request.getPassword();
        
        System.out.println("Login attempt: " + userName);
        
        if (userName.isEmpty() || password.length() < 4) {
            return new ResultPayload(false, "INVALID_INPUT", "Username required, password min 4 chars.");
        }

        if (!playerDao.isUsernameExist(userName)) {
            return new ResultPayload(false, "INVALID_INPUT", "Username not found");
        }

        try {
            Player player = playerDao.login(userName, password);
            if (player == null) {
                return new ResultPayload(false, "INVALID_INPUT", "Invalid password");
            } else {
                playerDao.updatePlayerState(userName, 1);
                updateStats();
                ResultPayload response = new ResultPayload(true, "OK", "Login successful.");
                response.setJsonPayload(gson.toJson(player));
                return response;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return new ResultPayload(false, "DB_ERROR", "Database error occurred");
        }
    }

    public List<PlayerStatsDto> getOnlineAndInGamePlayers(String userName) {
        return playerDao.getLeaderBoardPlayers(userName);
    }

    private String clean(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    public boolean updatePlayerState(String username, int state) {
        if (username == null || username.isEmpty()) {
            return false;
        }

        System.out.println("Updating state for " + username + " to: " + state + 
                " (0=OFFLINE, 1=ONLINE, 2=IN_GAME, 3=WAITING)");
        
        boolean result = playerDao.updatePlayerState(username, state);
        
        if (result) {
            updateStats();
        }
        
        return result;
    }

    public void setOnStatsChanged(Runnable callback) {
        this.onStatsChanged = callback;
    }

    public void updateStats() {
        if (onStatsChanged != null) {
            onStatsChanged.run();
        }
    }

    public boolean increasePlayerScore(String username, int points) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        
        boolean success = playerDao.updatePlayerScore(username, points);

        if (success) {
            updateStats();
            System.out.println("Score Update: " + username + " gained " + points + " points.");
        }

        return success;
    }

    public synchronized void findMatch(ClientSession playerSession) {
        matchmakingQueue.add(playerSession);
        updatePlayerState(playerSession.getUsername(), 3);

        if (matchmakingQueue.size() >= 2) {
            ClientSession player1 = matchmakingQueue.poll();
            ClientSession player2 = matchmakingQueue.poll();
            
            ActiveGame game = new ActiveGame(player1, player2, false);
            activeGames.put(player1.getUsername(), game);
            activeGames.put(player2.getUsername(), game);
            
            updatePlayerState(player1.getUsername(), 2);
            updatePlayerState(player2.getUsername(), 2);
            
            player1.send(new NetworkMessage(
                    MessageType.GAME_START, 
                    "server", 
                    player1.getUsername(), 
                    gson.toJsonTree(new GameStartDto(player2.getUsername(), true, false))
            ));
            
            player2.send(new NetworkMessage(
                    MessageType.GAME_START, 
                    "server", 
                    player2.getUsername(), 
                    gson.toJsonTree(new GameStartDto(player1.getUsername(), false, false))
            ));
            
            System.out.println("Match started: " + player1.getUsername() + " vs " + player2.getUsername());
        }
    }

    public void handleGameMove(ClientSession session, GameMoveDto move) {
        ActiveGame game = activeGames.get(session.getUsername());
        if (game == null) {
            System.err.println("No active game found for: " + session.getUsername());
            return;
        }

        if (game.makeMove(move.getRow(), move.getColumn(), session.getUsername())) {
            ClientSession opponent = game.getOpponent(session.getUsername());
            
            if (opponent != null && opponent.isConnected()) {
                opponent.send(new NetworkMessage(
                        MessageType.UPDATE_BOARD, 
                        session.getUsername(), 
                        opponent.getUsername(), 
                        gson.toJsonTree(move)
                ));
            }

            GameEngine gameEngine = game.getGameEngine();
            GameEngine.Player winner = gameEngine.getWinner();
            
            if (winner != GameEngine.Player.NONE) {
                session.send(new NetworkMessage(
                        MessageType.GAME_OVER, 
                        "server", 
                        session.getUsername(), 
                        gson.toJsonTree("You Win!")
                ));
                
                if (opponent != null && opponent.isConnected()) {
                    opponent.send(new NetworkMessage(
                            MessageType.GAME_OVER, 
                            "server", 
                            opponent.getUsername(), 
                            gson.toJsonTree("You Lose!")
                    ));
                }
                
                cleanupGame(game, winner);
                
            } else if (gameEngine.isBoardFull()) {
                session.send(new NetworkMessage(
                        MessageType.GAME_OVER, 
                        "server", 
                        session.getUsername(), 
                        gson.toJsonTree("It's a Draw!")
                ));
                
                if (opponent != null && opponent.isConnected()) {
                    opponent.send(new NetworkMessage(
                            MessageType.GAME_OVER, 
                            "server", 
                            opponent.getUsername(), 
                            gson.toJsonTree("It's a Draw!")
                    ));
                }
                
                cleanupGame(game, GameEngine.Player.NONE);
            }

        } else {
            session.send(new NetworkMessage(
                    MessageType.ERROR, 
                    "server", 
                    session.getUsername(), 
                    gson.toJsonTree(new ErrorPayload("INVALID_MOVE", "Invalid move"))
            ));
        }
    }

    private void cleanupGame(ActiveGame game, GameEngine.Player winner) {
        String player1 = game.getPlayerX().getUsername();
        String player2 = game.getPlayerO().getUsername();
        String winnerName = null;
        
        if (winner == GameEngine.Player.X) {
            winnerName = player1;
            playerDao.updatePlayerScore(player1, 100);
            playerDao.updatePlayerScore(player2, -50);
            System.out.println("Game ended: " + player1 + " wins against " + player2);
            
        } else if (winner == GameEngine.Player.O) {
            winnerName = player2;
            playerDao.updatePlayerScore(player2, 100);
            playerDao.updatePlayerScore(player1, -50);
            System.out.println("Game ended: " + player2 + " wins against " + player1);
            
        } else {
            playerDao.updatePlayerScore(player1, 50);
            playerDao.updatePlayerScore(player2, 50);
            System.out.println("Game ended: Draw between " + player1 + " and " + player2);
        }

        gameDao.addGame(player1, player2, winnerName);

        playerDao.updatePlayerState(player1, 1);
        playerDao.updatePlayerState(player2, 1);

        activeGames.remove(player1);
        activeGames.remove(player2);

        updateStats();
    }

    public void handleSurrender(ClientSession session) {
        ActiveGame game = activeGames.get(session.getUsername());
        
        if (game != null) {
            String surrenderer = session.getUsername();
            ClientSession opponent = game.getOpponent(surrenderer);
            
            if (opponent != null && opponent.isConnected()) {
                opponent.send(new NetworkMessage(
                        MessageType.OPPONENT_LEFT,
                        "server",
                        opponent.getUsername(),
                        gson.toJsonTree("Your opponent surrendered. You Win!")
                ));
            }
            
            GameEngine.Player winnerSymbol = (game.getPlayerX().getUsername().equals(surrenderer))
                    ? GameEngine.Player.O
                    : GameEngine.Player.X;

            cleanupGame(game, winnerSymbol);
            
            System.out.println(surrenderer + " surrendered to " + 
                    (opponent != null ? opponent.getUsername() : "disconnected opponent"));
        }
    }

    public void handlePlayerDisconnect(ClientSession session) {
        String username = session.getUsername();
        ActiveGame game = activeGames.get(username);
        
        if (game != null) {
            ClientSession opponent = game.getOpponent(username);
            
            if (opponent != null && opponent.isConnected()) {
                opponent.send(new NetworkMessage(
                        MessageType.OPPONENT_LEFT, 
                        username, 
                        opponent.getUsername(), 
                        gson.toJsonTree("Your opponent has disconnected. You win!")
                ));
            }

            GameEngine.Player winner = (game.getPlayerX().getUsername().equals(username)) 
                    ? GameEngine.Player.O 
                    : GameEngine.Player.X;
                    
            cleanupGame(game, winner);
            
            System.out.println(username + " disconnected during game");
        }
    }

    public void startPrivateGame(ClientSession player1, ClientSession player2, boolean isRecorded) {
        ActiveGame game = new ActiveGame(player1, player2, isRecorded);
        activeGames.put(player1.getUsername(), game);
        activeGames.put(player2.getUsername(), game);
        
        updatePlayerState(player1.getUsername(), 2);
        updatePlayerState(player2.getUsername(), 2);
        
        player1.send(new NetworkMessage(
                MessageType.GAME_START, 
                "server", 
                player1.getUsername(), 
                gson.toJsonTree(new GameStartDto(player2.getUsername(), true, isRecorded))
        ));
        
        player2.send(new NetworkMessage(
                MessageType.GAME_START, 
                "server", 
                player2.getUsername(), 
                gson.toJsonTree(new GameStartDto(player1.getUsername(), false, isRecorded))
        ));
        
        System.out.println("Private game started: " + player1.getUsername() + " vs " + player2.getUsername() + 
                " (Recorded: " + isRecorded + ")");
    }

    public ResultPayload changePassword(ChangePasswordRequest request) {
        String username = request.getUsername();
        String newPass = request.getNewPassword();

        if (username == null || newPass == null || newPass.length() < 4) {
            return new ResultPayload(false, "INVALID_INPUT", "Password must be at least 4 characters.");
        }

        boolean updated = playerDao.updatePassword(username, newPass);

        if (updated) {
            return new ResultPayload(true, "OK", "Password changed successfully.");
        } else {
            return new ResultPayload(false, "DB_ERROR", "Failed to update password.");
        }
    }
}
