/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource.dao;

import com.mycompany.tictactoeserver.data.model.Player;
import com.mycompany.tictactoeserver.data.model.Player.PlayerState;
import com.mycompany.tictactoeserver.data.model.PlayerStatsDto;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Nadin
 */
public class Helper {
    private Helper() {}

    public static Player PlayerMapper(ResultSet rs) throws SQLException {
        return new Player(
                rs.getInt("ID"),
                rs.getString("NAME"),
                rs.getString("EMAIL"),
                PlayerState.fromValue(rs.getInt("PLAYER_STATE")),
                rs.getInt("SCORE")
        );
    }
    
    public static PlayerStatsDto PlayerStatsDtoMapper(ResultSet rs) throws SQLException {
        return new PlayerStatsDto(
                new Player(
                rs.getInt("ID"),
                rs.getString("NAME"),
                rs.getString("EMAIL"),
                PlayerState.fromValue(rs.getInt("PLAYER_STATE")),
                rs.getInt("SCORE")),
                rs.getInt("WINS"),
                rs.getInt("LOSSES")
        );
    }
}
