/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network.dtos;

import com.mycompany.tictactoeclient.data.models.Player;
import java.util.List;

/**
 *
 * @author yasse
 */
public class OnlinePlayersUpdate {
   
    private List<Player> players;
    
    public OnlinePlayersUpdate() {}
    
    public OnlinePlayersUpdate(List<Player> players) {
        this.players = players;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
