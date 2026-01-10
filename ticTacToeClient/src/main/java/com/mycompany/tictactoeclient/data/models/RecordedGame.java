/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.data.models;

import com.mycompany.tictactoeclient.data.dataSource.RecordedGamesJson;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mina Wagdy
 */
public class RecordedGame {

    public String playerXName;
    public String playerOName;
    public LocalDateTime gameDate;
    public List<MoveRecord> moves = new ArrayList<>();
    public String userId;

    public RecordedGame(String playerXName, String playerOName, String userId) {
        this.playerXName = playerXName;
        this.playerOName = playerOName;
        this.gameDate = LocalDateTime.now();
        this.userId = userId;
    }

    public void addMove(MoveRecord move) {
        moves.add(move);
    }
    
    public void saveRecord() {
        RecordedGamesJson.saveGame(this);
    }

    public List<MoveRecord> getMoves() {
        return moves;
    }

    public String getPlayerXName() {
        return playerXName;
    }

    public String getPlayerOName() {
        return playerOName;
    }

    public String getPlayerInfo() {
        return playerXName + " vs " + playerOName;
    }

    public LocalDateTime getGameDate() {
        return gameDate;
    }
    
    public String getUserId() {
        return userId;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        return gameDate.format(formatter);
    }

}
