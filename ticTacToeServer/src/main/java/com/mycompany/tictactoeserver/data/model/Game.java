/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.model;

import java.time.LocalDateTime;

/**
 *
 * @author Nadin
 */
public class Game {

    private Player playerOne;
    private Player playerTwo;
    private LocalDateTime gameDate;
    private GameState gameState;
    private boolean isRecorded;

    public enum GameState {
        ONGOING(0),
        PLAYER_ONE_WON(1),
        PLAYER_TWO_WON(2),
        DRAW(3);

        private final int value;

        GameState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static GameState fromValue(int value) {
            for (GameState state : values()) {
                if (state.value == value) {
                    return state;
                }
            }
            return ONGOING;
        }
    }

    public Game() {
    }

    public Game(Player playerOne, Player playerTwo,
            LocalDateTime gameDate, GameState gameState, boolean isRecorded) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.gameDate = gameDate;
        this.gameState = gameState;
        this.isRecorded = isRecorded;
    }

    public Player getPlayerOne(){return playerOne;}
    public Player getPlayerTwo(){return playerTwo;}
    public LocalDateTime getGameDate(){return gameDate;}
    public GameState getGameState(){return gameState;}
    public boolean isRecorded(){return isRecorded;}
}
