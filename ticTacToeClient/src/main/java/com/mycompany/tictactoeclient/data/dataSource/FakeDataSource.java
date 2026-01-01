/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.data.dataSource;

import com.mycompany.tictactoeclient.data.models.Player;
import com.mycompany.tictactoeclient.data.models.Player.PlayerStatus;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yasse
 */
public class FakeDataSource {

    public static List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Ahmed", 1500, PlayerStatus.ONLINE));
        players.add(new Player("Mohamed", 1200, PlayerStatus.IN_GAME));
        players.add(new Player("Ali", 800, PlayerStatus.IN_GAME));
        players.add(new Player("Sara", 2100, PlayerStatus.ONLINE));
        players.add(new Player("Khaled", 950, PlayerStatus.ONLINE));
        players.add(new Player("Mona", 1750, PlayerStatus.IN_GAME));
        players.add(new Player("Youssef", 1300, PlayerStatus.ONLINE));
        players.add(new Player("Ibrahim", 1100, PlayerStatus.ONLINE));
        players.add(new Player("Hoda", 600, PlayerStatus.IN_GAME));
        players.add(new Player("Omar", 1900, PlayerStatus.IN_GAME));
        return players;
    }
}
