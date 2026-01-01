/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.data.models;

/**
 *
 * @author Mina Wagdy
 */
public class RecordedGame {

    private String playerInfo; // e.g., "WithPlayer1"
    private String date;       // e.g., "20-12-2025"
    private String time;       // e.g., "02:22AM"

    public RecordedGame(String playerInfo, String date, String time) {
        this.playerInfo = playerInfo;
        this.date = date;
        this.time = time;
    }

    // Getters
    public String getPlayerInfo() {
        return playerInfo;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
