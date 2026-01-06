/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import com.google.gson.JsonElement;

/**
 *
 * @author yasse
 */
public final class NetworkMessage {
    private MessageType type;
    private String username;
    private String receiver;

    private JsonElement payload;

    public NetworkMessage() {}

    public NetworkMessage(MessageType type, String username, String receiver, JsonElement payload) {
        this.type = type;
        this.username = username;
        this.receiver = receiver;
        this.payload = payload;
    }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public JsonElement getPayload() { return payload; }
    public void setPayload(JsonElement payload) { this.payload = payload; }
}


