/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network.dtos;

import java.util.List;

/**
 *
 * @author yasse
 */
public class OnlinePlayersUpdate {
    private List<String> usernames;
    
    public OnlinePlayersUpdate() {}
    
    public OnlinePlayersUpdate(List<String> usernames) {
        this.usernames = usernames;
    }
    
    public List<String> getUsernames() {
        return usernames;
    }
    
    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }
}
