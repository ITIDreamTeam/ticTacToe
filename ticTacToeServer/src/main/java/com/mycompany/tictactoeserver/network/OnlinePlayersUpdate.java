/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import com.mycompany.tictactoeserver.data.model.Player;
import java.util.List;

/**
 *
 * @author yasse
 */
public final class OnlinePlayersUpdate {
    private List<Player> players;

    public OnlinePlayersUpdate() {}

    public OnlinePlayersUpdate(List<Player> players) {
        this.players = players;
    }

    public List<Player> getUsernames() { return players; }
}
