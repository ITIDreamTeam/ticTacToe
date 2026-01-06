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

    private UserSession() {}

    public static UserSession getInstance() { return INSTANCE; }

    public boolean isLoggedIn() { return username != null; }

    public String getUsername() { return username; }

    public void login(String username) { this.username = username; }

    public void logout() { this.username = null; }
}
