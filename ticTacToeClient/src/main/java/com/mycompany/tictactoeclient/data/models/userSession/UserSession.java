/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.data.models.userSession;

/**
 *
 * @author Basmala
 */
public class UserSession {
    private static UserSession instance;
    
    private String username= "Basmala";
    private boolean isLoggedIn;
    
    private UserSession() {
        this.isLoggedIn = true;
        this.username = null;
    }
    
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    public void login(String username) {
        this.username = username;
        this.isLoggedIn = true;
    }
    
    public void logout() {
        this.username = null;
        this.isLoggedIn = false;
    }
    
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
