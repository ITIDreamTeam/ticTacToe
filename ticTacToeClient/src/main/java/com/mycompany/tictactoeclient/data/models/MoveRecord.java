/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.data.models;

import com.mycompany.tictactoeclient.presentation.features.game_board.GameEngine;

/**
 *
 * @author Nadin
 */
public class MoveRecord {

    private final int row;
    private final int col;
    private final PlayerType player;

    public MoveRecord(int row, int col, PlayerType player) {
        this.row = row;
        this.col = col;
        this.player = player;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public PlayerType getPlayer() {
        return player;
    }
}
