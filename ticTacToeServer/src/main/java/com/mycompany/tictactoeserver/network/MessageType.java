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
    LOGIN,
    REGISTER,
    GET_ONLINE_PLAYERS,

    SEND_REQUEST,     
    REQUEST_RESPONSE,   
    GAME_MOVE,
    GAME_END,

    DISCONNECT,

    // server -> client events
    ERROR,
    REGISTER_RESULT,
    LOGIN_RESULT,
    ONLINE_PLAYERS_UPDATE
}
