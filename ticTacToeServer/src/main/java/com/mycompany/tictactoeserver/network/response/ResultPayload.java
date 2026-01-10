/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network.response;

import com.google.gson.JsonElement;

/**
 *
 * @author yasse
 */
public final class ResultPayload {

    private boolean success;
    private String code;
    private Object message;
    private JsonElement jsonPayload;

    public ResultPayload() {
    }

    public ResultPayload(boolean success, String code, Object message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
        public ResultPayload(boolean success, String code, Object message,JsonElement jsonPayload) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.jsonPayload = jsonPayload;
    }

    // 2. Add Getter and Setter
    public JsonElement getJsonPayload() {
        return jsonPayload;
    }

    public void setJsonPayload(JsonElement jsonPayload) {
        this.jsonPayload = jsonPayload;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public Object getMessage() {
        return message;
    }
}
