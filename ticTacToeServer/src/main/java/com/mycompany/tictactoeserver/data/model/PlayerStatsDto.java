/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.model;

/**
 *
 * @author Basmala
 */
public class PlayerStatsDto {
    private Player player;
    private int wins;
    private int losses;

    public PlayerStatsDto(Player player, int wins, int losses) {
        this.player = player;
        this.wins = wins;
        this.losses = losses;
    }
    public void setPlayer(Player player){
        this.player=player;
    }
    
    public void setWins(int wins){
        this.wins=wins;
    }
    
    public void setLosses(int losses){
        this.losses=losses;
    }
    
    public Player getPlayer(){
        return player;
    }
    
    public int getWins(){
        return wins;
    }
    
    public int getLosses(){
        return losses;
    }
}
