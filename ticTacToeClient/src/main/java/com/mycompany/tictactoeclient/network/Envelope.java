/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network;

/**
 *
 * @author yasse
 */
public  class Envelope {
    private MessageType type;
    private String requestId; 
    private Object payload;

    public Envelope() { }

    public Envelope(MessageType type, String requestId, Object payload) {
        this.type = type;
        this.requestId = requestId;
        this.payload = payload;
    }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}

