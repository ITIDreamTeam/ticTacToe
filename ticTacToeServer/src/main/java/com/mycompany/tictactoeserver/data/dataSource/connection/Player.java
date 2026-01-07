/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource.connection;

/**
 *
 * @author Basmala
 */
import java.net.Socket;

public class Player {

    private String id;
    private String username;
    private String email;
    private int score;
    private String status; // ONLINE, OFFLINE, PLAYING, WAITING
    private Socket socket;
    private String currentGameId;

    public Player(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.score = 0;
        this.status = "ONLINE";
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getScore() {
        return score;
    }

    public String getStatus() {
        return status;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setCurrentGameId(String gameId) {
        this.currentGameId = gameId;
    }

    public void incrementScore() {
        this.score++;
    }
}
