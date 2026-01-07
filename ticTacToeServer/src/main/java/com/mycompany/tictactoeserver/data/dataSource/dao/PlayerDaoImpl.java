/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource.dao;

import com.mycompany.tictactoeserver.data.model.Player;
import com.mycompany.tictactoeserver.data.model.PlayerStatsDto;
import com.mycompany.tictactoeserver.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yasse
 */
public class PlayerDaoImpl {

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM PLAYER";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                players.add(Helper.PlayerMapper(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }

    public Player login(String email, String password) throws SQLException {
        String sql = "SELECT * FROM PLAYER WHERE EMAIL = ? AND PASSWORD = ?";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Helper.PlayerMapper(rs);
            }


        return null;
    }

public boolean register(Player player) throws SQLException {
    String sql = "INSERT INTO PLAYER (NAME, EMAIL, PASSWORD, PLAYER_STATE, SCORE) "
            + "VALUES (?, ?, ?, ?, ?)";

    Connection con = DBConnection.getConnection();
    PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, player.getName());
        ps.setString(2, player.getEmail());
        ps.setString(3, player.getPassword());
        ps.setInt(4, 1);
        ps.setInt(5, 300);
        
        int rowsAffected = ps.executeUpdate();
        
        System.out.print(player.getName() + " Registered: " + (rowsAffected > 0));
        
        return rowsAffected > 0;
}

    public boolean editPlayer(Player player
    ) {
        String sql = "UPDATE PLAYER "
                + "SET NAME = ?, EMAIL = ?, PASSWORD = ?, PLAYER_STATE = ?, SCORE = ? "
                + "WHERE ID = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, player.getName());
            ps.setString(2, player.getEmail());
            ps.setString(3, player.getPassword());
            ps.setInt(4, player.getPlayerState().getValue());
            ps.setInt(5, player.getScore());
            ps.setInt(6, player.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

//    public List<Player> getLeaderboardPlayers(int playerId
//    ) {
//        List<Player> leaderboard = new ArrayList<>();
//        String sql = "SELECT * FROM PLAYER"
//                + "WHERE ID <> ?"
//                + "ORDER BY SCORE DESC";
//
//        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
//
//            ps.setInt(1, playerId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                leaderboard.add(Helper.PlayerMapper(rs));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return leaderboard;
//    }
    public List<PlayerStatsDto> getLeaderBoardPlayers(String playerName) {
         List<PlayerStatsDto> result = new ArrayList<>();

        String sqlGetPlayerId = "SELECT ID FROM PLAYER WHERE NAME = ?";

        String sql = "SELECT p.ID, p.NAME, p.EMAIL, p.SCORE, p.PLAYER_STATE, " +
                     "COUNT(*) as TOTAL_GAMES, " +
                     "SUM(CASE " +
                     "    WHEN g.GAME_STATE = ? THEN 1 " +
                     "    ELSE 0 " +
                     "END) AS WINS, " +
                     "SUM(CASE " +
                     "    WHEN g.GAME_STATE = p.ID THEN 1 " +
                     "    ELSE 0 " +
                     "END) AS LOSSES, " +
                     "FROM GAME g " +
                     "JOIN PLAYER p ON ( " +
                     "    (g.PLAYER_ONE_ID = ? AND p.ID = g.PLAYER_TWO_ID) OR " +
                     "    (g.PLAYER_TWO_ID = ? AND p.ID = g.PLAYER_ONE_ID) " +
                     ") " +
                     "WHERE (g.PLAYER_ONE_ID = ? OR g.PLAYER_TWO_ID = ?) " +
                     "AND p.PLAYER_STATE = 1 " +  
                     "GROUP BY p.ID, p.NAME, p.EMAIL, p.SCORE, p.PLAYER_STATE " +
                     "ORDER BY WINS DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps1 = con.prepareStatement(sqlGetPlayerId);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps1.setString(1, playerName); 
            ResultSet rs1 = ps1.executeQuery();

            if (!rs1.next()) {
                return result;
            }

            int playerId = rs1.getInt("ID");

            // Set parameters for the main query
            ps.setInt(1, playerId);   // For wins calculation
            ps.setInt(2, playerId);   // First parameter in JOIN
            ps.setInt(3, playerId);   // Second parameter in JOIN
            ps.setInt(4, playerId);   // First WHERE condition
            ps.setInt(5, playerId);   // Second WHERE condition

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PlayerStatsDto player = Helper.PlayerStatsDtoMapper(rs);
                result.add(player);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    public boolean isUsernameExist(String name) {
        String sql = "SELECT 1 FROM PLAYER WHERE NAME = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
