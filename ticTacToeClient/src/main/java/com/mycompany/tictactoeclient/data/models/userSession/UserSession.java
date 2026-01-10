/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.data.models.userSession;

import com.mycompany.tictactoeclient.DataAccessLayer;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;

/**
 *
 * @author Basmala
 */
public class UserSession {
     private static final UserSession INSTANCE = new UserSession();
    
    private volatile String username ;
    private volatile String email;
    private volatile int score;
    private volatile boolean isOnline;
    
    private final DataAccessLayer dataAccessLayer = new DataAccessLayer();
    
    private UserSession() {}
    
    public static UserSession getInstance() { 
        return INSTANCE; 
    }
    
    public boolean isLoggedIn() { 
        return username != null; 
    }
    
    public String getUsername() { 
        return username; 
    }
    
    public String getEmail() {
        return email;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setScore(int score) {
        this.score=score;
    }
    
    public boolean isOnline() {
        return isOnline && NetworkClient.getInstance().isConnected();
    }
    
    public void login(String username, String email) {
        this.username = username;
        this.email = email;
        this.isOnline = true;
    }
    
    public void logout() {
        dataAccessLayer.deleteUserRecordings(this.username);
        this.username = null;
        this.email = null;
        this.isOnline = false;
        NetworkClient client = NetworkClient.getInstance();
        if (client.isConnected()) {
            try {
                NetworkMessage logoutMsg = new NetworkMessage(
                    MessageType.DISCONNECT,
                    username,
                    "Server",
                    null
                );
                client.send(logoutMsg);
            } catch (Exception ignored) {}
            
            client.disconnect();
        }
        client.clearListeners();
    }
    
    public void setOffline() {
        this.isOnline = false;
    }
}
