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
    private boolean isHost; 
    private GameMode gameMode;     
    
    private GameSessionManager() {}
    
    public static GameSessionManager getInstance() {
        return INSTANCE;
    }
    
    public void setOnlineSession(String opponentUsername, boolean isHost) {
        this.opponentUsername = opponentUsername;
        this.isHost = isHost;
        this.userName = UserSession.getInstance().getUsername();
        this.gameMode = GameMode.withFriend; 
    }

    public void setComputerSession() {
        this.opponentUsername = "Computer";
        this.userName = UserSession.getInstance().isLoggedIn() ? UserSession.getInstance().getUsername() : "Player";
        this.isHost = true; 
        this.gameMode = GameMode.vsComputer;
    }

    public void setLocalPvpSession(String player1, String player2) {
        this.userName = player1;   
        this.opponentUsername = player2; 
        this.isHost = true;
        this.gameMode = GameMode.twoPlayer;
    }
    
    
    public String getOpponentName() { return opponentUsername; }
    public String getUserName() { return userName; }
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
        this.isHost = false;
        this.gameMode = null;
    }
}