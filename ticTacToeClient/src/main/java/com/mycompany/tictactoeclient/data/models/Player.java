/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.data.models;

/**
 *
 * @author yasse
 */
public class Player {
    private String name;
    private int score;
    private PlayerStatus status;
    private String avatarUrl;
     private int wins;
    private int losses;
    
    public enum PlayerStatus {
        ONLINE, OFFLINE, IN_GAME
    }
    public Player() {}
    
    public Player(String name, int score, int wins, int losses, PlayerStatus status) {
        this.name = name;
        this.score = score;
        this.wins = wins;
        this.losses = losses;
        this.status = status;
        this.avatarUrl = "..\\..\\..\\avatar.png";
    }
    
    public Player(String name, PlayerStatus status) {
        this.name = name;
        this.status = status;
        this.score = 0;
        this.wins = 0;
        this.losses = 0;
        this.avatarUrl = "..\\..\\..\\avatar.png";
    }
    
    public Player(String name, int score, PlayerStatus status) {
        this.name = name;
        this.score = score;
        this.status = status;
        this.avatarUrl = "..\\..\\..\\avatar.png";
    }

   public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }
    
    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }
    
    public PlayerStatus getStatus() { return status; }
    public void setStatus(PlayerStatus status) { this.status = status; }
    
    public String getAvatarUrl() { return avatarUrl; }
}
