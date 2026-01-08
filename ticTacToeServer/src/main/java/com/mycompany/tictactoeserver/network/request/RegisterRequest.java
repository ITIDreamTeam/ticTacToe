/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network.request;

/**
 *
 * @author yasse
 */
public final class RegisterRequest {
    private String username;
    private String password;
    private String email;

    public RegisterRequest() { }
    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail(){return email;}
}
