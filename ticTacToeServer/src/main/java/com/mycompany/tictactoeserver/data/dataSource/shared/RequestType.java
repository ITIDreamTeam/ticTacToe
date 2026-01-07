/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource.shared;

/**
 *
 * @author Basmala
 */

public class RequestType {
    public static final String LOGIN = "login";
    public static final String REGISTER = "register";
    public static final String LOGOUT = "logout";
    
    public static final String LEADERBOARD = "Leader-board";
    public static final String ASK_FOR_PLAY = "Ask-for-play";
    public static final String GAME_MOVE = "game-move";
    public static final String GAME_INVITE_RESPONSE = "invite-response";
    public static final String CANCEL_INVITE = "cancel-invite";
    public static final String PLAYER_STATUS = "player-status";
    
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String GAME_STATE = "game-state";
    public static final String INVITATION = "invitation";
}