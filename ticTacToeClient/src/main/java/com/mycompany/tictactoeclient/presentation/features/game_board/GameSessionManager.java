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
    
    public void setGameSession(String opponentUsername, boolean isRecordingGame, boolean isHost) {
        this.opponentUsername = opponentUsername;
        this.isRecordingGame = isRecordingGame;
        this.isHost = isHost;
        this.userName = UserSession.getInstance().getUsername();
        this.gameMode = gameMode.vsComputer;
    }
        public void setGameSession(String opponentUsername,String userName, boolean isRecordingGame, boolean isHost) {
        this.opponentUsername = opponentUsername;
        this.isRecordingGame = isRecordingGame;
        this.isHost = isHost;
        this.userName = userName;
         this.gameMode = gameMode.twoPlayer;
    }
    public void setGameSession(String opponentUsername,String userName, boolean isRecordingGame, boolean isHost,GameMode gameMode) {
        this.opponentUsername = opponentUsername;
        this.isRecordingGame = isRecordingGame;
        this.isHost = isHost;
        this.userName = userName;
        this.gameMode = gameMode;
    }
    
    public String getOpponentUsername() {
        return opponentUsername;
    }
    public String getUserName(){
    return userName;
    }
    
    public boolean isRecordingGame() {
        return isRecordingGame;
    }
    
    public boolean isHost() {
        return isHost;
    }
    
    public void clearSession() {
        this.opponentUsername = null;
        this.isRecordingGame = false;
        this.isHost = false;
    }
}
