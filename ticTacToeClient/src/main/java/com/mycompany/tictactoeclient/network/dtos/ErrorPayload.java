/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network.dtos;

/**
 *
 * @author yasse
 */
public final class ErrorPayload {
    private String code;
    private String message;

    public ErrorPayload() {}

    public ErrorPayload(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
