/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.game_board;

/**
 *
 * @author yasse
 */
import com.mycompany.tictactoeclient.data.models.MoveRecord;
import com.mycompany.tictactoeclient.data.models.PlayerType;
import com.mycompany.tictactoeclient.data.models.RecordedGame;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {

    public enum Player {
        X, O, NONE
    };
    private Player[][] board;
    private Player currentPlayer;
    private boolean gameOver;
    private Random random;
    private int[] winningCoords = null;
    private MinimaxAlgorithm minimax;
    private boolean isRecorded;
    private String playerXName;
    private String playerOName;
    MoveRecord moveRecord;
    RecordedGame recordedGame;

    public static enum gameDifficulty {
        Easy, Medium, Hard
    };
    public gameDifficulty difficulty = gameDifficulty.Easy;

    public GameEngine() {
        board = new Player[3][3];
        random = new Random();

        minimax = new MinimaxAlgorithm();
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
        PlayerType type
                = currentPlayer == Player.X
                        ? PlayerType.X
                        : PlayerType.O;
        if (isRecorded && recordedGame != null) {
            MoveRecord moveRecord = new MoveRecord(row, col, type);
            System.out.println("Recorded move: " + row + col + type);
            recordedGame.addMove(moveRecord);
        }
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
        if (state) {
            stopRecording();
        }
    }

    public void startRecording(String playerXName, String playerOName, String userId) {
        this.isRecorded = true;
        this.playerXName = playerXName;
        this.playerOName = playerOName;
        recordedGame = new RecordedGame(playerXName, playerOName, userId);
        System.out.println("start hash: " + hashCode());
    }

    public void stopRecording() {
        this.isRecorded = false;
        if (recordedGame == null) {
            return;
        }
        System.out.println("list before save");
        System.out.println("stop hash: " + hashCode());
        for (MoveRecord e : recordedGame.getMoves()) {
            System.out.println("col: " + e.getCol() + " row: " + e.getRow() + " player symoble: " + e.getPlayer());
        }
        if (recordedGame != null && !recordedGame.getMoves().isEmpty()) {
            recordedGame.saveRecord();
            recordedGame = null;
        }
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

//    public int[] getComputerMove() {
//        while (true) {
//            int r = random.nextInt(3);
//            int c = random.nextInt(3);
//            if (board[r][c] == Player.NONE) return new int[]{r, c};
//            if (isBoardFull()) return null;
//        }
//    }
    public int[] getComputerMove() {
        switch (difficulty) {
            case Easy:
                return minimax.getEasyMove(board, currentPlayer);
            case Medium:
                return minimax.getMediumMove(board, currentPlayer);
            case Hard:
                return minimax.getHardMove(board, currentPlayer);
            default:
                return minimax.getMediumMove(board, currentPlayer);
        }
    }

    private class MinimaxAlgorithm {

        private Random random = new Random();

        public int[] getEasyMove(Player[][] board, Player currentPlayer) {
            Player opponent = (currentPlayer == Player.X) ? Player.O : Player.X;

            if (random.nextDouble() < 0.7) {
                return getRandomMove(board);
            }

            int[] immediateMove = findImmediateMove(board, currentPlayer, opponent);
            if (immediateMove != null) {
                return immediateMove;
            }

            return getRandomMove(board);
        }

        public int[] getMediumMove(Player[][] board, Player currentPlayer) {
            Player opponent = (currentPlayer == Player.X) ? Player.O : Player.X;

            int[] immediateMove = findImmediateMove(board, currentPlayer, opponent);
            if (immediateMove != null) {
                return immediateMove;
            }

            if (random.nextDouble() < 0.8) {
                return getMinimaxMove(board, currentPlayer, 3);
            } else {
                return getGoodButNotPerfectMove(board, currentPlayer);
            }
        }

        public int[] getHardMove(Player[][] board, Player currentPlayer) {
            return getPerfectMinimaxMove(board, currentPlayer);
        }

        private int[] getRandomMove(Player[][] board) {
            List<int[]> emptyCells = getEmptyCells(board);
            if (emptyCells.isEmpty()) {
                return null;
            }
            return emptyCells.get(random.nextInt(emptyCells.size()));
        }

        private int[] getPerfectMinimaxMove(Player[][] board, Player currentPlayer) {
            List<int[]> emptyCells = getEmptyCells(board);
            Player opponent = (currentPlayer == Player.X) ? Player.O : Player.X;

            if (emptyCells.size() >= 8 || emptyCells.size() == 1) {
                return emptyCells.get(random.nextInt(emptyCells.size()));
            }

            int[] immediateMove = findImmediateMove(board, currentPlayer, opponent);
            if (immediateMove != null) {
                return immediateMove;
            }

            int bestScore = Integer.MIN_VALUE;
            List<int[]> bestMoves = new ArrayList<>();

            for (int[] move : emptyCells) {
                int row = move[0];
                int col = move[1];

                board[row][col] = currentPlayer;
                int score = minimax(board, 0, false, currentPlayer, opponent);
                board[row][col] = Player.NONE;

                if (score > bestScore) {
                    bestScore = score;
                    bestMoves.clear();
                    bestMoves.add(move);
                } else if (score == bestScore) {
                    bestMoves.add(move);
                }
            }

            return bestMoves.get(random.nextInt(bestMoves.size()));
        }

        private int[] getMinimaxMove(Player[][] board, Player currentPlayer, int maxDepth) {
            List<int[]> emptyCells = getEmptyCells(board);
            Player opponent = (currentPlayer == Player.X) ? Player.O : Player.X;

            int[] immediateMove = findImmediateMove(board, currentPlayer, opponent);
            if (immediateMove != null) {
                return immediateMove;
            }

            int bestScore = Integer.MIN_VALUE;
            List<int[]> bestMoves = new ArrayList<>();

            for (int[] move : emptyCells) {
                int row = move[0];
                int col = move[1];

                board[row][col] = currentPlayer;
                int score = limitedMinimax(board, 0, false, currentPlayer, opponent, maxDepth);
                board[row][col] = Player.NONE;

                if (score > bestScore) {
                    bestScore = score;
                    bestMoves.clear();
                    bestMoves.add(move);
                } else if (score == bestScore) {
                    bestMoves.add(move);
                }
            }

            return bestMoves.get(random.nextInt(bestMoves.size()));
        }

        private int[] getGoodButNotPerfectMove(Player[][] board, Player currentPlayer) {
            List<int[]> emptyCells = getEmptyCells(board);
            Player opponent = (currentPlayer == Player.X) ? Player.O : Player.X;

            if (board[1][1] == Player.NONE && random.nextBoolean()) {
                return new int[]{1, 1};
            }

            List<int[]> corners = new ArrayList<>();
            if (board[0][0] == Player.NONE) {
                corners.add(new int[]{0, 0});
            }
            if (board[0][2] == Player.NONE) {
                corners.add(new int[]{0, 2});
            }
            if (board[2][0] == Player.NONE) {
                corners.add(new int[]{2, 0});
            }
            if (board[2][2] == Player.NONE) {
                corners.add(new int[]{2, 2});
            }

            if (!corners.isEmpty() && random.nextBoolean()) {
                return corners.get(random.nextInt(corners.size()));
            }

            return getMinimaxMove(board, currentPlayer, 2);
        }

        private int limitedMinimax(Player[][] board, int depth, boolean isMaximizing, Player player, Player opponent, int maxDepth) {
            if (depth >= maxDepth) {
                return evaluateBoard(board, player, opponent);
            }

            if (checkWin(board, player)) {
                return 50 - depth;
            }
            if (checkWin(board, opponent)) {
                return -50 + depth;
            }
            if (isBoardFull(board)) {
                return 0;
            }

            if (isMaximizing) {
                int bestScore = Integer.MIN_VALUE;
                for (int[] move : getEmptyCells(board)) {
                    int row = move[0];
                    int col = move[1];

                    board[row][col] = player;
                    int score = limitedMinimax(board, depth + 1, false, player, opponent, maxDepth);
                    board[row][col] = Player.NONE;

                    bestScore = Math.max(score, bestScore);
                }
                return bestScore;
            } else {
                int bestScore = Integer.MAX_VALUE;
                for (int[] move : getEmptyCells(board)) {
                    int row = move[0];
                    int col = move[1];

                    board[row][col] = opponent;
                    int score = limitedMinimax(board, depth + 1, true, player, opponent, maxDepth);
                    board[row][col] = Player.NONE;

                    bestScore = Math.min(score, bestScore);
                }
                return bestScore;
            }
        }

        private int evaluateBoard(Player[][] board, Player player, Player opponent) {
            int score = 0;

            if (board[1][1] == player) {
                score += 3;
            } else if (board[1][1] == opponent) {
                score -= 3;
            }

            int[][] corners = {{0, 0}, {0, 2}, {2, 0}, {2, 2}};
            for (int[] corner : corners) {
                if (board[corner[0]][corner[1]] == player) {
                    score += 2;
                } else if (board[corner[0]][corner[1]] == opponent) {
                    score -= 2;
                }
            }

            return score;
        }

        private int minimax(Player[][] board, int depth, boolean isMaximizing,
                Player player, Player opponent) {
            if (checkWin(board, player)) {
                return 50 - depth;
            }
            if (checkWin(board, opponent)) {
                return -50 + depth;
            }
            if (isBoardFull(board)) {
                return 0;
            }

            if (isMaximizing) {
                int bestScore = Integer.MIN_VALUE;
                for (int[] move : getEmptyCells(board)) {
                    int row = move[0];
                    int col = move[1];

                    board[row][col] = player;
                    int score = minimax(board, depth + 1, false, player, opponent);
                    board[row][col] = Player.NONE;

                    bestScore = Math.max(score, bestScore);
                }
                return bestScore;
            } else {
                int bestScore = Integer.MAX_VALUE;
                for (int[] move : getEmptyCells(board)) {
                    int row = move[0];
                    int col = move[1];

                    board[row][col] = opponent;
                    int score = minimax(board, depth + 1, true, player, opponent);
                    board[row][col] = Player.NONE;

                    bestScore = Math.min(score, bestScore);
                }
                return bestScore;
            }
        }

        private int[] findImmediateMove(Player[][] board, Player player, Player opponent) {
            for (int[] move : getEmptyCells(board)) {
                int row = move[0];
                int col = move[1];

                board[row][col] = player;
                if (checkWin(board, player)) {
                    board[row][col] = Player.NONE;
                    return move;
                }
                board[row][col] = Player.NONE;
            }

            for (int[] move : getEmptyCells(board)) {
                int row = move[0];
                int col = move[1];

                board[row][col] = opponent;
                if (checkWin(board, opponent)) {
                    board[row][col] = Player.NONE;
                    return move;
                }
                board[row][col] = Player.NONE;
            }

            return null;
        }

        private boolean checkWin(Player[][] board, Player player) {
            for (int i = 0; i < 3; i++) {
                if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                    return true;
                }
            }

            for (int j = 0; j < 3; j++) {
                if (board[0][j] == player && board[1][j] == player && board[2][j] == player) {
                    return true;
                }
            }

            if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
                return true;
            }
            if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
                return true;
            }

            return false;
        }

        private boolean isBoardFull(Player[][] board) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == Player.NONE) {
                        return false;
                    }
                }
            }
            return true;
        }

        private List<int[]> getEmptyCells(Player[][] board) {
            List<int[]> emptyCells = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == Player.NONE) {
                        emptyCells.add(new int[]{i, j});
                    }
                }
            }
            return emptyCells;
        }
    }
}
