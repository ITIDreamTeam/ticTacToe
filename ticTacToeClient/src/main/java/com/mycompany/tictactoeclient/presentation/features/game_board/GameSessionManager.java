/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.game_board;

import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.presentation.features.game_board.Game_boardController.GameMode;

/**
 *
 * @author yasse
 */
public class GameSessionManager {
    private static final GameSessionManager INSTANCE = new GameSessionManager();
    
    private String opponentUsername;
    private String userName;
    private boolean isRecordingGame;
    private boolean isHost; 
    private GameMode gameMode;     
    
    private GameSessionManager() {}
    
    public static GameSessionManager getInstance() {
        return INSTANCE;
    }
    
    public void setOnlineSession(String opponentUsername, boolean isRecordingGame, boolean isHost) {
        this.opponentUsername = opponentUsername;
        this.isRecordingGame = isRecordingGame;
        this.isHost = isHost;
        this.userName = UserSession.getInstance().getUsername();
        this.gameMode = GameMode.withFriend; 
    }

    public void setComputerSession(boolean isRecordingGame) {
        this.opponentUsername = "Computer";
        this.userName = UserSession.getInstance().isLoggedIn() ? UserSession.getInstance().getUsername() : "Player";
        this.isRecordingGame = isRecordingGame;
        this.isHost = true; 
        this.gameMode = GameMode.vsComputer;
    }

    public void setLocalPvpSession(String player1, String player2, boolean isRecordingGame) {
        this.userName = player1;   
        this.opponentUsername = player2; 
        this.isRecordingGame = isRecordingGame;
        this.isHost = true;
        this.gameMode = GameMode.twoPlayer;
    }
    
    
    public String getOpponentName() { return opponentUsername; }
    public String getUserName() { return userName; }
    public boolean isRecordingGame() { return isRecordingGame; }
    public GameMode getGameMode() { return gameMode; }
    
    public boolean isMyTurnFirst() {
        return isHost; 
    }
    
    public boolean isOnlineGame() {
        return this.gameMode == GameMode.withFriend;
    }
    
    public void clearSession() {
        this.opponentUsername = null;
        this.userName = null;
        this.isRecordingGame = false;
        this.isHost = false;
        this.gameMode = null;
    }
}