/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource.dao;

import com.mycompany.tictactoeserver.data.model.Player;
import com.mycompany.tictactoeserver.data.model.Player.PlayerState;
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

    public List<Player> getLeaderboardPlayers(String userName
    ) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT ID, NAME, EMAIL, SCORE, PLAYER_STATE " +
                     "FROM PLAYER " +
                     "WHERE PLAYER_STATE IN (1, 3) " + // 1=ONLINE, 3=IN_GAME
                     "ORDER BY SCORE DESC";

        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Player player = new Player(
                    rs.getInt("ID"),
                    rs.getString("NAME"),
                    rs.getString("EMAIL"),
                     rs.getString("PASSWORD"),
                    PlayerState.fromValue( rs.getInt("SCORE")),
                    rs.getInt("PLAYER_STATE")
                );
                players.add(player);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
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
