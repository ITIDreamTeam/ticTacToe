/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network;

/**
 *
 * @author yasse
 */
public enum MessageType {
    REGISTER_REQUEST,
    REGISTER_RESPONSE,
    LOGIN_REQUEST,
    LOGIN_RESPONSE,
    ONLINE_PLAYERS_REQUEST,
    ONLINE_PLAYERS_RESPONSE,
    ONLINE_PLAYERS_CHANGED,
    ERROR
}
