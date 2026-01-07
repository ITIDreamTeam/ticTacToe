/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource.connection;

/**
 *
 * @author Basmala
 */

import java.util.Date;

public class Game {
    private String gameId;
    private String playerXId;
    private String playerOId;
    private String currentTurn;
    private String[] board;
    private GameStatus status;
    private String winner;
    private Date startTime;
    private Date endTime;
    
    public enum GameStatus {
        WAITING, IN_PROGRESS, FINISHED, CANCELLED
    }
    
    public Game() {
        this.gameId = java.util.UUID.randomUUID().toString();
        this.board = new String[9];
        this.status = GameStatus.WAITING;
        this.startTime = new Date();
    }
    
    public Game(String playerXId, String playerOId) {
        this();
        this.playerXId = playerXId;
        this.playerOId = playerOId;
        this.currentTurn = playerXId;
    }
    
    public boolean makeMove(int position, String playerId) {
        if (!currentTurn.equals(playerId) || 
            board[position] != null || 
            !status.equals(GameStatus.IN_PROGRESS)) {
            return false;
        }
        
        board[position] = playerId.equals(playerXId) ? "X" : "O";
        
        if (checkWin()) {
            status = GameStatus.FINISHED;
            winner = playerId;
            endTime = new Date();
        } else if (isBoardFull()) {
            status = GameStatus.FINISHED;
            winner = "DRAW";
            endTime = new Date();
        } else {
            currentTurn = currentTurn.equals(playerXId) ? playerOId : playerXId;
        }
        
        return true;
    }
    
    private boolean checkWin() {
        String[][] winPatterns = {
            {board[0], board[1], board[2]},
            {board[3], board[4], board[5]},
            {board[6], board[7], board[8]},
            {board[0], board[3], board[6]},
            {board[1], board[4], board[7]},
            {board[2], board[5], board[8]},
            {board[0], board[4], board[8]},
            {board[2], board[4], board[6]}
        };
        
        for (String[] pattern : winPatterns) {
            if (pattern[0] != null && 
                pattern[0].equals(pattern[1]) && 
                pattern[1].equals(pattern[2])) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isBoardFull() {
        for (String cell : board) {
            if (cell == null) return false;
        }
        return true;
    }
    
    // Getters and setters
    public String getGameId() { return gameId; }
    public String getPlayerXId() { return playerXId; }
    public String getPlayerOId() { return playerOId; }
    public String getCurrentTurn() { return currentTurn; }
    public String[] getBoard() { return board; }
    public GameStatus getStatus() { return status; }
    public String getWinner() { return winner; }
    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }
    public void setStatus(GameStatus status) { this.status = status; }
}