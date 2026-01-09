/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource.dao;

import com.mycompany.tictactoeserver.data.model.Player;
import com.mycompany.tictactoeserver.network.dtos.PlayerStatsDto;

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

//    public List<Player> getAllPlayers() {
//        List<Player> players = new ArrayList<>();
//        String sql = "SELECT * FROM PLAYER";
//
//        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                players.add(Helper.PlayerMapper(rs));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return players;
//    }
    public List<PlayerStatsDto> getAllPlayers() {
    List<PlayerStatsDto> players = new ArrayList<>();
    
    String sql = "SELECT p.*, " +
                 "COALESCE(SUM(CASE WHEN g.GAME_STATE = p.ID THEN 1 ELSE 0 END), 0) as WINS, " +
                 "COALESCE(SUM(CASE WHEN g.GAME_STATE != p.ID AND g.GAME_STATE != 0 THEN 1 ELSE 0 END), 0) as LOSSES, " +
                 "COALESCE(SUM(CASE WHEN g.GAME_STATE = 0 THEN 1 ELSE 0 END), 0) as DRAWS, " +
                 "COUNT(g.GAME_DATE) as TOTAL_GAMES " +
                 "FROM PLAYER p " +
                 "LEFT JOIN GAME g ON (p.ID = g.PLAYER_ONE_ID OR p.ID = g.PLAYER_TWO_ID) " +
                 "GROUP BY p.ID, p.NAME, p.EMAIL, p.PASSWORD, p.PLAYER_STATE, p.SCORE " +
                 "ORDER BY p.SCORE DESC";


    try (Connection con = DBConnection.getConnection(); 
         PreparedStatement ps = con.prepareStatement(sql); 
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            players.add(Helper.PlayerStatsDtoMapper(rs));
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
    
    public boolean editPlayerState(String playername, int state) {
        String sql = "UPDATE PLAYER SET PLAYER_STATE = ? WHERE NAME = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, state);
            ps.setString(2, playername);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public boolean addGame(String playerOneName, String playerTwoName, String winnerName) {
        String sql = "INSERT INTO GAME (PLAYER_ONE_ID, PLAYER_TWO_ID, GAME_DATE, GAME_STATE) " +
                     "VALUES (?, ?, CURRENT_TIMESTAMP, ?)";

        String getPlayerIdSql = "SELECT ID FROM PLAYER WHERE NAME = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement getPlayerStmt1 = con.prepareStatement(getPlayerIdSql);
             PreparedStatement getPlayerStmt2 = con.prepareStatement(getPlayerIdSql);
             PreparedStatement getWinnerid=con.prepareStatement(getPlayerIdSql);
             PreparedStatement insertStmt = con.prepareStatement(sql)) {

            // Get player one ID
            getPlayerStmt1.setString(1, playerOneName);
            ResultSet rs1 = getPlayerStmt1.executeQuery();
            if (!rs1.next()) {
                System.out.println("Player one not found: " + playerOneName);
                return false;
            }
            int playerOneId = rs1.getInt("ID");

            getPlayerStmt2.setString(1, playerTwoName);
            ResultSet rs2 = getPlayerStmt2.executeQuery();
            if (!rs2.next()) {
                System.out.println("Player two not found: " + playerTwoName);
                return false;
            }
            int playerTwoId = rs2.getInt("ID");
            
            getWinnerid.setString(1, playerTwoName);
            ResultSet rs3 = getWinnerid.executeQuery();
            if (!rs3.next()) {
                System.out.println("Player two not found: " + playerTwoName);
                return false;
            }
            int winnerId = rs3.getInt("ID");

            if (winnerId != 0 && winnerId != playerOneId && winnerId != playerTwoId) {
                System.out.println("Invalid winnerId: " + winnerId + 
                                 ". Must be 0 (draw), " + playerOneId + " (" + playerOneName + 
                                 "), or " + playerTwoId + " (" + playerTwoName + ")");
                return false;
            }

            // Set parameters for insert
            insertStmt.setInt(1, playerOneId);
            insertStmt.setInt(2, playerTwoId);
            insertStmt.setInt(3, winnerId);

            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Handle duplicate game constraint (same players at same timestamp)
            if (e.getErrorCode() == 23505) { // Duplicate key error code may vary by DB
                System.out.println("Game already exists between these players at this time.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
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
                    "COUNT(g.GAME_DATE) as TOTAL_GAMES, " + 
                    "COALESCE(SUM(CASE " +
                    "    WHEN g.GAME_STATE = ? THEN 1 " + 
                    "    ELSE 0 " +
                    "END), 0) AS WINS, " +
                    "COALESCE(SUM(CASE " +
                    "    WHEN g.GAME_STATE = p.ID THEN 1 " +
                    "    ELSE 0 " +
                    "END), 0) AS LOSSES " +
                    "FROM PLAYER p " +
                    "LEFT JOIN GAME g ON ( " +
                    "    ((g.PLAYER_ONE_ID = ? AND g.PLAYER_TWO_ID = p.ID) OR " +
                    "     (g.PLAYER_TWO_ID = ? AND g.PLAYER_ONE_ID = p.ID)) " +
                    ") " +
                    "WHERE p.PLAYER_STATE = 1 " +  
                    "AND p.ID <> ? " +
                    "GROUP BY p.ID, p.NAME, p.EMAIL, p.SCORE, p.PLAYER_STATE " +
                    "ORDER BY p.SCORE DESC, WINS DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps1 = con.prepareStatement(sqlGetPlayerId);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps1.setString(1, playerName); 
            ResultSet rs1 = ps1.executeQuery();

            if (!rs1.next()) {
                return result;
            }

            int playerId = rs1.getInt("ID");

            ps.setInt(1, playerId);   // For wins calculation
            ps.setInt(2, playerId);   // First parameter in JOIN
            ps.setInt(3, playerId);   // Second parameter in JOIN
            ps.setInt(4, playerId);   // First WHERE condition

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
    
    public int getPlayersCountBasedOnState(int state) {
        String sql = "SELECT COUNT(ID) AS count FROM PLAYER WHERE PLAYER_STATE = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, state);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { 
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPlayersState(String playerName) {
        String sql = "SELECT PLAYER_STATE FROM PLAYER WHERE NAME = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("PLAYER_STATE");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updatePlayerScore(String playerName, int scoreDelta) {
        String sql = "UPDATE PLAYER SET SCORE = SCORE + ? WHERE NAME = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, scoreDelta);
            ps.setString(2, playerName);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
