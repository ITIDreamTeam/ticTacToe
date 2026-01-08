/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import com.mycompany.tictactoeserver.network.dtos.PlayerStatsDto;
import java.util.List;

/**
 *
 * @author yasse
 */
public final class OnlinePlayersUpdate {
    private List<PlayerStatsDto> players;

    public OnlinePlayersUpdate() {}

    public OnlinePlayersUpdate(List<PlayerStatsDto> players) {
        this.players = players;
    }

    public List<PlayerStatsDto> getUsernames() { return players; }
}
