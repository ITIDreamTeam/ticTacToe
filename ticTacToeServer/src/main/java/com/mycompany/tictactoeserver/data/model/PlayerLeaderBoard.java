/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.model;

/**
 *
 * @author Basmala
 */
public class PlayerLeaderBoard {

    private String id;
    private String username;
    private PlayerStatus status;
    private int wins;
    private int losses;
    private int score;
    public enum PlayerStatus {
        ONLINE, OFFLINE, IN_GAME
    }

    public PlayerLeaderBoard() {
    }

    public PlayerLeaderBoard(String username, String email, String password) {
        this.username = username;
        this.id = generateId();
        this.status = PlayerStatus.OFFLINE;
    }

    private String generateId() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
