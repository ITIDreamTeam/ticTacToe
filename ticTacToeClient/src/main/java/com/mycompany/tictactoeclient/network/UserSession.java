/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network;

/**
 *
 * @author yasse
 */
public final class UserSession {
     private static final UserSession INSTANCE = new UserSession();
    
    private volatile String username;
    private volatile String email;
    private volatile boolean isOnline;
    
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
    
    public boolean isOnline() {
        return isOnline && NetworkClient.getInstance().isConnected();
    }
    
    public void login(String username, String email) {
        this.username = username;
        this.email = email;
        this.isOnline = true;
    }
    
    public void logout() {
        NetworkClient client = NetworkClient.getInstance();
        if (client.isConnected() && username != null) {
            try {
                NetworkMessage logoutMsg = new NetworkMessage(
                    MessageType.DISCONNECT,
                    this.username,
                    "Server",
                    null
                );
                client.send(logoutMsg);
            } catch (Exception ignored) {}
            client.disconnect();
        }

        this.username = null;
        this.email = null;
        this.isOnline = false;
        client.clearListeners();
    }
    
    public void setOffline() {
        this.isOnline = false;
    }
}
