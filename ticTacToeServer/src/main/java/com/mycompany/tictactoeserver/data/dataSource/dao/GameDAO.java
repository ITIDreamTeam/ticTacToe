/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource.dao;

import com.mycompany.tictactoeserver.data.model.Game;
import com.mycompany.tictactoeserver.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Nadin
 */
public interface GameDAO {

    boolean addGameResult(Game game);
}

class GameDAOImpl implements GameDAO {

    @Override
    public boolean addGameResult(Game game) {

        String sql = "INSERT INTO GAME(PLAYER_ONE_ID, PLAYER_TWO_ID, GAME_DATE, GAME_STATE)"
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, game.getPlayerOne().getId());
            ps.setInt(2, game.getPlayerTwo().getId());
            ps.setTimestamp(3, Timestamp.valueOf(game.getGameDate()));
            ps.setInt(4, game.getGameState().getValue());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
