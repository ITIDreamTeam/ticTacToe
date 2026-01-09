/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network.request;

/**
 *
 * @author Mina Wagdy
 */
public class InviteRequest {

    private String senderUsername;
    private String targetUsername;
    private boolean recordGame;

    public InviteRequest() {
    }

    public InviteRequest(String senderUsername, String targetUsername, boolean recordGame) {
        this.senderUsername = senderUsername;
        this.targetUsername = targetUsername;
        this.recordGame = recordGame;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public boolean isRecordGame() {
        return recordGame;
    }

    public void setRecordGame(boolean recordGame) {
        this.recordGame = recordGame;
    }
}
