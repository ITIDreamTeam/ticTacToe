package com.mycompany.tictactoeserver.data.dataSource.dao;

import com.mycompany.tictactoeserver.data.model.Player;
import com.mycompany.tictactoeserver.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nadin
 */
public interface PlayerDAO {

    List<Player> getAllPlayers();

    Player login(String email, String password);

    boolean register(Player player);

    boolean editPlayer(Player player);

    List<Player> getLeaderboardPlayers(int playerId);
}

class PlayerDAOImpl implements PlayerDAO {

    @Override
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

    @Override
    public Player login(String email, String password) {
        String sql = "SELECT * FROM PLAYER WHERE EMAIL = ? AND PASSWORD = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Helper.PlayerMapper(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean register(Player player
    ) {
        String sql = "INSERT INTO PLAYER (NAME, EMAIL, PASSWORD, PLAYER_STATE, SCORE) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, player.getName());
            ps.setString(2, player.getEmail());
            ps.setString(3, player.getPassword());
            ps.setInt(4, player.getPlayerState().getValue());
            ps.setInt(5, player.getScore());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
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

    @Override
    public List<Player> getLeaderboardPlayers(int playerId
    ) {
        List<Player> leaderboard = new ArrayList<>();
        String sql = "SELECT * FROM PLAYER"
                + "WHERE ID <> ?"
                + "ORDER BY SCORE DESC";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                leaderboard.add(Helper.PlayerMapper(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return leaderboard;
    }
}
