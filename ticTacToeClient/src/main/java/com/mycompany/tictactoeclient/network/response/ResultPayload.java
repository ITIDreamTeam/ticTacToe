/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network.response;

/**
 *
 * @author yasse
 */
public final class ResultPayload {
    private boolean success;
    private String code;  
    private String message;

    public ResultPayload() {}

    public ResultPayload(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}
