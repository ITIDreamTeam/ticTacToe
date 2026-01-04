/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.game_board;

/**
 *
 * @author yasse
 */
import java.util.Random;

public class GameEngine {

    public enum Player {
        X, O, NONE
    }
    private Player[][] board;
    private Player currentPlayer;
    private boolean gameOver;
    private Random random;
    private int[] winningCoords = null;

    public GameEngine() {
        board = new Player[3][3];
        random = new Random();
        resetGame(Player.X);
    }

    public void resetGame(Player startPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = Player.NONE;
            }
        }
        currentPlayer = startPlayer;
        gameOver = false;
        winningCoords = null;
    }

    public boolean makeMove(int row, int col) {
        if (gameOver || board[row][col] != Player.NONE) {
            return false;
        }
        board[row][col] = currentPlayer;
        return true;
    }

    public void switchTurn() {
        currentPlayer = (currentPlayer == Player.X) ? Player.O : Player.X;
    }

    public Player getCurrentPlayer() { //TODO: Nedds to be able to handle 2 players player on the same machine locally
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean state) {
        this.gameOver = state;
    }

    public int[] getWinningCoords() {
        return winningCoords;
    }

    public Player getWinner() {
        for (int i = 0; i < 3; i++) {
            if (checkLine(board[i][0], board[i][1], board[i][2])) {
                int start = i * 3;
                winningCoords = new int[]{start, start + 2};
                return board[i][0];
            }
        }

        for (int i = 0; i < 3; i++) {
            if (checkLine(board[0][i], board[1][i], board[2][i])) {
                winningCoords = new int[]{i, i + 6};
                return board[0][i];
            }
        }

        if (checkLine(board[0][0], board[1][1], board[2][2])) {
            winningCoords = new int[]{0, 8};
            return board[0][0];
        }
        if (checkLine(board[0][2], board[1][1], board[2][0])) {
            winningCoords = new int[]{2, 6};
            return board[0][2];
        }

        return Player.NONE;
    }

    private boolean checkLine(Player a, Player b, Player c) {
        return a != Player.NONE && a == b && b == c;
    }

    public boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == Player.NONE) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[] getComputerMove() {
        while (true) {
            int r = random.nextInt(3);
            int c = random.nextInt(3);
            if (board[r][c] == Player.NONE) {
                return new int[]{r, c};
            }
            if (isBoardFull()) {
                return null;
            }
        }
    }
}
