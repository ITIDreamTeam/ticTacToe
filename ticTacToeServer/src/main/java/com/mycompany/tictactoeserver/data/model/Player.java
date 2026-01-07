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

    public Player() {
    }

    public Player(int id, String name, String email, String password, PlayerState playerState, int score) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.playerState = playerState;
        this.score = score;
    }
    public Player(int id, String name, String email, PlayerState playerState, int score) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.playerState = playerState;
        this.score = score;
    }

        public Player( String name, String email, String password, PlayerState playerState, int score) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.playerState = playerState;
        this.score = score;
    }
      public Player( String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getScore() {
        return score;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }
}
