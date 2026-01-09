package com.mycompany.tictactoeserver.data.dataSource.dao;

import com.mycompany.tictactoeserver.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameDAO {

    public boolean addGame(String playerOneName, String playerTwoName, String winnerName) {
        String sql = "INSERT INTO GAME (PLAYER_ONE_ID, PLAYER_TWO_ID, GAME_DATE, GAME_STATE) "
                + "VALUES (?, ?, CURRENT_TIMESTAMP, ?)";

        try (Connection con = DBConnection.getConnection(); PreparedStatement insertStmt = con.prepareStatement(sql)) {

            int playerOneId = getPlayerId(playerOneName);
            int playerTwoId = getPlayerId(playerTwoName);
            Integer winnerId = null;
            if (winnerName != null) {
                winnerId = getPlayerId(winnerName);
            }

            if (playerOneId == -1 || playerTwoId == -1 || (winnerName != null && winnerId == -1)) {
                return false;
            }

            insertStmt.setInt(1, playerOneId);
            insertStmt.setInt(2, playerTwoId);
            if (winnerId != null) {
                insertStmt.setInt(3, winnerId);
            } else {
                insertStmt.setNull(3, java.sql.Types.INTEGER);
            }

            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getPlayerId(String name) {
        String sql = "SELECT ID FROM PLAYER WHERE NAME = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
