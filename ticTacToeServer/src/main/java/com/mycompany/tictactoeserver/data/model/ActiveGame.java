package com.mycompany.tictactoeserver.data.model;

import com.mycompany.tictactoeserver.network.ClientSession;

public class ActiveGame {

    private final ClientSession playerX;
    private final ClientSession playerO;
    private final GameEngine gameEngine;
    private final boolean isRecorded;

    public ActiveGame(ClientSession player1, ClientSession player2, boolean isRecorded) {
        // For simplicity, player1 is always X
        this.playerX = player1;
        this.playerO = player2;
        this.gameEngine = new GameEngine();
        this.isRecorded = isRecorded;
    }

    public boolean isRecorded() {
        return isRecorded;
    }

    public ClientSession getPlayerX() {
        return playerX;
    }

    public ClientSession getPlayerO() {
        return playerO;
    }

    public synchronized boolean makeMove(int row, int col, String username) {
        GameEngine.Player currentPlayer = gameEngine.getCurrentPlayer();
        ClientSession currentSession = (currentPlayer == GameEngine.Player.X) ? playerX : playerO;

        if (!currentSession.getUsername().equals(username)) {
            return false; // Not this player's turn
        }

        if (gameEngine.makeMove(row, col)) {
            gameEngine.switchTurn();
            return true;
        }
        return false;
    }

    public ClientSession getOpponent(String username) {
        if (playerX.getUsername().equals(username)) {
            return playerO;
        } else {
            return playerX;
        }
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }
}
