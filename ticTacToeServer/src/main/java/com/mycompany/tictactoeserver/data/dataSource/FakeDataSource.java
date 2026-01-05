/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource;

import com.mycompany.tictactoeserver.data.model.Player;
import com.mycompany.tictactoeserver.data.model.Player.PlayerState;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yasse
 */
public class FakeDataSource {

    public static List<Player> getAllPlayers() {

        List<Player> players = new ArrayList<>();

        players.add(new Player(1, "Ahmed", "ahmed@mail.com", "1234", PlayerState.ONLINE, 1500));
        players.add(new Player(2, "Mohamed", "mohamed@mail.com", "1234", PlayerState.IN_GAME, 1200));
        players.add(new Player(3, "Ali", "ali@mail.com", "1234", PlayerState.IN_GAME, 800));
        players.add(new Player(4, "Omar", "omar@mail.com", "1234", PlayerState.OFFLINE, 1900));
        players.add(new Player(5, "Sara", "sara@mail.com", "1234", PlayerState.ONLINE, 2100));
        players.add(new Player(6, "Khaled", "khaled@mail.com", "1234", PlayerState.ONLINE, 950));
        players.add(new Player(7, "Mona", "mona@mail.com", "1234", PlayerState.IN_GAME, 1750));
        players.add(new Player(8, "Youssef", "youssef@mail.com", "1234", PlayerState.ONLINE, 1300));
        players.add(new Player(9, "Ibrahim", "ibrahim@mail.com", "1234", PlayerState.ONLINE, 1100));
        players.add(new Player(10, "Hoda", "hoda@mail.com", "1234", PlayerState.IN_GAME, 600));

        return players;
    }
}