/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

/**
 *
 * @author yasse
 */
public enum MessageType {
    // Auth
    REGISTER,
    REGISTER_RESULT,
    LOGIN,
    LOGIN_RESULT,
    DISCONNECT,
    
    // Players
    GET_ONLINE_PLAYERS,
    ONLINE_PLAYERS_UPDATE,
    
    // Game Invites
    SEND_REQUEST,
    ACCEPT_REQUEST,
    DECLINE_REQUEST,
    
    // Game
    GAME_MOVE,
    GAME_STATE_UPDATE,
    GAME_END,
    
    // General
    ERROR
}
