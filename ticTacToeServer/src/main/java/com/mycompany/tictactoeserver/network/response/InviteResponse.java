/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network.response;

/**
 *
 * @author yasse
 */
public class InviteResponse {
    private String senderUsername;
    private String targetUsername;
    private boolean accepted;
    private boolean recordGame;
    
    public InviteResponse() {}
    
    public InviteResponse(String senderUsername, String targetUsername, boolean accepted, boolean recordGame) {
        this.senderUsername = senderUsername;
        this.targetUsername = targetUsername;
        this.accepted = accepted;
        this.recordGame = recordGame;
    }
    
    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
    
    public String getTargetUsername() { return targetUsername; }
    public void setTargetUsername(String targetUsername) { this.targetUsername = targetUsername; }
    
    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
    
    public boolean isRecordGame() { return recordGame; }
    public void setRecordGame(boolean recordGame) { this.recordGame = recordGame; }
}
