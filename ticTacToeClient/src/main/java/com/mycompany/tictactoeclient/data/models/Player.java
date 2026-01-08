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
 private int id;
    private String name;
    private String email;
    private String password;
    private int score;
    private PlayerState playerState;
     private String avatarUrl;
    private int wins;
    private int losses;
    
    public static enum PlayerState {
        OFFLINE(0),
        ONLINE(1),
        IN_GAME(2);

        private final int value;

        PlayerState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static PlayerState fromValue(int value) {
            for (PlayerState state : values()) {
                if (state.value == value) {
                    return state;
                }
            }
            return OFFLINE;
        }
    }
    
    public Player() {}
    
    public Player(String name, int score, int wins, int losses, PlayerState status) {
        this.name = name;
        this.score = score;
        this.wins = wins;
        this.losses = losses;
        this.playerState = status;
        this.avatarUrl = "..\\..\\..\\avatar.png";
    }
    
    public Player(String name, PlayerState status) {
        this.name = name;
        this.playerState = status;
        this.score = 0;
        this.wins = 0;
        this.losses = 0;
        this.avatarUrl = "..\\..\\..\\avatar.png";
    }
    
    public Player(String name, int score, PlayerState status) {
        this.name = name;
        this.score = score;
        this.playerState = status;
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
    
    public PlayerState getStatus() { return playerState; }
    public void setStatus(PlayerState status) { this.playerState = status; }
    public String getAvatarUrl() { return avatarUrl; }
    
     public static PlayerState fromStateCode(int stateCode) {
        switch (stateCode) {
            case 1: return PlayerState.ONLINE;
            case 0: return PlayerState.OFFLINE;
            case 2: return PlayerState.IN_GAME;
            default: return PlayerState.OFFLINE;
        }
    }
}
