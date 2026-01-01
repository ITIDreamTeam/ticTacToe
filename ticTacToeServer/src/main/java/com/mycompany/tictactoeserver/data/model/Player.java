/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.model;

/**
 *
 * @author Nadin
 */
public class Player {
    private String name;
    private int score;
    private PlayerStatus status;
    private String avatarUrl;
    private int numOfWins;
    private int numOfLosses;
    private int numOfDraws;

    public enum PlayerStatus {
        ONLINE, OFFLINE, IN_GAME
    }

    public Player(String name, int score, PlayerStatus status) {
        this.name = name;
        this.score = score;
        this.status = status;
        this.avatarUrl = "..\\..\\..\\avatar.png";
        this.numOfWins = 0;
        this.numOfLosses = 0;
        this.numOfDraws = 0;
    }

    public String getName() { return name; }
    public int getScore() { return score; }
    public PlayerStatus getStatus() { return status; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getNumOfWins() {return numOfWins;}
    public int getNumOfLosses() {return numOfLosses;}
    public int getNumOfDraws() {return numOfDraws;}
}
