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

    private String playerInfo;
    private String date;
    private String time;

    public RecordedGame(String playerInfo, String date, String time) {
        this.playerInfo = playerInfo;
        this.date = date;
        this.time = time;
    }

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
