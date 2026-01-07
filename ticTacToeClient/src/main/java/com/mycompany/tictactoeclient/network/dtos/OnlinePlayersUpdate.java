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
   
    private List<PlayerStatsDto> players;
    
    public OnlinePlayersUpdate() {}
    
    public OnlinePlayersUpdate(List<PlayerStatsDto> players) {
        this.players = players;
    }
    
    public List<PlayerStatsDto> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<PlayerStatsDto> players) {
        this.players = players;
    }
}
