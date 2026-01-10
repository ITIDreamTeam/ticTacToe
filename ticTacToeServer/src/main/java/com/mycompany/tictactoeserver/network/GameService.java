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
        System.out.print(userName + email);
        if (playerDao.isUsernameExist(userName)) {
            return new ResultPayload(false, "DUPLICATE_USERNAME", "Username already exists.");
        }
        Player player = new Player(userName, email, password);
        try {
            playerDao.register(player);
            return new ResultPayload(true, "OK", "Registered successfully.");
        } catch (SQLException ex) {
            return new ResultPayload(false, "INVALID_INPUT", "Un expected behavior");
        }
    }

    public ResultPayload login(RegisterRequest request) {
        String userName = clean(request.getUsername());
        String password = request.getPassword() == null ? "" : request.getPassword();
        System.out.print("this is the login player :"+ request.getPassword() );
        if (userName.isEmpty() || password.length() < 4) {
            return new ResultPayload(false, "INVALID_INPUT", "Username required, password min 4 chars.");
        }

        if (!playerDao.isUsernameExist(userName)) {
            return new ResultPayload(false, "INVALID_INPUT", "Username not found");
        }

        try {
            Player player = playerDao.login(userName, password);
            System.out.print("this is the login player :"+ player );
            if(player == null){
                return new ResultPayload(false, "INVALID_INPUT", "Invalid password");
            }else{
            playerDao.updatePlayerState(userName, 1);
            updateStats();
            return new ResultPayload(true, "OK", "Registered successfully.");
            }
            
        } catch (SQLException ex) {
            return new ResultPayload(false, "INVALID_INPUT", "Un expected behavior");
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

        System.out.println("Updating state for " + username + " to: " + state);
        return playerDao.updatePlayerState(username, state);
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
        if (username == null || username.isEmpty()) return false;
        boolean success = playerDao.updatePlayerScore(username, points);

        if (success) {
            updateStats();
            System.out.println("Score Update: " + username + " gained " + points + " points.");
        }

        return success;}
    
    public synchronized void findMatch(ClientSession playerSession) {
        matchmakingQueue.add(playerSession);

        if (matchmakingQueue.size() >= 2) {
            ClientSession player1 = matchmakingQueue.poll();
            ClientSession player2 = matchmakingQueue.poll();
            ActiveGame game = new ActiveGame(player1, player2, false);
            activeGames.put(player1.getUsername(), game);
            activeGames.put(player2.getUsername(), game);
            player1.send(new NetworkMessage(MessageType.GAME_START, "server", player1.getUsername(), gson.toJsonTree(new GameStartDto(player2.getUsername(), true, false))));
            player2.send(new NetworkMessage(MessageType.GAME_START, "server", player2.getUsername(), gson.toJsonTree(new GameStartDto(player1.getUsername(), false, false))));
        }
    }

    public void handleGameMove(ClientSession session, GameMoveDto move) {
        ActiveGame game = activeGames.get(session.getUsername());
        if (game == null) {
            return;
        }

        if (game.makeMove(move.getRow(), move.getColumn(), session.getUsername())) {
            ClientSession opponent = game.getOpponent(session.getUsername());
            opponent.send(new NetworkMessage(MessageType.UPDATE_BOARD, session.getUsername(), opponent.getUsername(), gson.toJsonTree(move)));
            
            GameEngine gameEngine = game.getGameEngine();
            GameEngine.Player winner = gameEngine.getWinner();
            if (winner != GameEngine.Player.NONE) {
                session.send(new NetworkMessage(MessageType.GAME_OVER, "server", session.getUsername(), gson.toJsonTree("You Win!")));
                opponent.send(new NetworkMessage(MessageType.GAME_OVER, "server", opponent.getUsername(), gson.toJsonTree("You Lose!")));
                cleanupGame(game, winner);
            } else if (gameEngine.isBoardFull()) {
                session.send(new NetworkMessage(MessageType.GAME_OVER, "server", session.getUsername(), gson.toJsonTree("It's a Draw!")));
                opponent.send(new NetworkMessage(MessageType.GAME_OVER, "server", opponent.getUsername(), gson.toJsonTree("It's a Draw!")));
                cleanupGame(game, GameEngine.Player.NONE);
            }

        } else {
            session.send(new NetworkMessage(MessageType.ERROR, "server", session.getUsername(), gson.toJsonTree(new ErrorPayload("INVALID_MOVE", "Invalid move"))));
        }
    }

    private void cleanupGame(ActiveGame game, GameEngine.Player winner) {
        String winnerName = null;
        if (winner == GameEngine.Player.X) {
            winnerName = game.getPlayerX().getUsername();
            playerDao.updatePlayerScore(game.getPlayerX().getUsername(), 100);
            playerDao.updatePlayerScore(game.getPlayerO().getUsername(), -50);
        } else if (winner == GameEngine.Player.O) {
            winnerName = game.getPlayerO().getUsername();
            playerDao.updatePlayerScore(game.getPlayerO().getUsername(), 100);
            playerDao.updatePlayerScore(game.getPlayerX().getUsername(), -50);
        } else { // Draw
            playerDao.updatePlayerScore(game.getPlayerX().getUsername(), 50);
            playerDao.updatePlayerScore(game.getPlayerO().getUsername(), 50);
        }

        gameDao.addGame(game.getPlayerX().getUsername(), game.getPlayerO().getUsername(), winnerName);

        playerDao.updatePlayerState(game.getPlayerX().getUsername(), 1); // 1 for Online
        playerDao.updatePlayerState(game.getPlayerO().getUsername(), 1); // 1 for Online

        activeGames.remove(game.getPlayerX().getUsername());
        activeGames.remove(game.getPlayerO().getUsername());
        
        updateStats();
    }
    
    public void handleSurrender(ClientSession session) {
    ActiveGame game = activeGames.get(session.getUsername());
    if (game != null) {
        ClientSession opponent = game.getOpponent(session.getUsername());
        if (opponent != null && opponent.isConnected()) {
            opponent.send(new NetworkMessage(
                MessageType.OPPONENT_LEFT, 
                "server", 
                opponent.getUsername(), 
                gson.toJsonTree("Your opponent surrendered. You Win!")
            ));
        }
        GameEngine.Player winnerSymbol = (game.getPlayerX() == opponent) 
                ? GameEngine.Player.X 
                : GameEngine.Player.O;

        cleanupGame(game, winnerSymbol);
        System.out.println(session.getUsername() + " surrendered.");
        updateStats();
    }
}
    public void handlePlayerDisconnect(ClientSession session) {
        ActiveGame game = activeGames.get(session.getUsername());
        if (game != null) {
            ClientSession opponent = game.getOpponent(session.getUsername());
            opponent.send(new NetworkMessage(MessageType.OPPONENT_LEFT, session.getUsername(), opponent.getUsername(), gson.toJsonTree("Your opponent has disconnected. You win!")));
            
            GameEngine.Player winner = game.getPlayerX() == opponent ? GameEngine.Player.X : GameEngine.Player.O;
            cleanupGame(game, winner);
        }
    }
    public void startPrivateGame(ClientSession player1, ClientSession player2, boolean isRecorded) {
    ActiveGame game = new ActiveGame(player1, player2, isRecorded);
    activeGames.put(player1.getUsername(), game);
    activeGames.put(player2.getUsername(), game);
    player1.send(new NetworkMessage(MessageType.GAME_START, "server", player1.getUsername(), gson.toJsonTree(new GameStartDto(player2.getUsername(), true, isRecorded))));
    player2.send(new NetworkMessage(MessageType.GAME_START, "server", player2.getUsername(), gson.toJsonTree(new GameStartDto(player1.getUsername(), false, isRecorded))));
    System.out.println("Private game started: " + player1.getUsername() + " vs " + player2.getUsername());
}
}
