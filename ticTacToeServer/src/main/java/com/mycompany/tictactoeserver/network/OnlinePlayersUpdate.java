/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import java.util.List;

/**
 *
 * @author yasse
 */
public final class OnlinePlayersUpdate {
    private List<String> usernames;

    public OnlinePlayersUpdate() {}

    public OnlinePlayersUpdate(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<String> getUsernames() { return usernames; }
}
