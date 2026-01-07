/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import com.mycompany.tictactoeserver.data.dataSource.dao.PlayerDaoImpl;
import com.mycompany.tictactoeserver.data.model.Player;
import com.mycompany.tictactoeserver.network.request.RegisterRequest;
import com.mycompany.tictactoeserver.network.response.ResultPayload;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author yasse
 */
public final class GameService {
    private final PlayerDaoImpl playerDao;


    public GameService(PlayerDaoImpl playerDao) {
        this.playerDao = playerDao;
    }

     public ResultPayload register(RegisterRequest request) {
        String userName = clean(request.getUsername());
        String email = request.getEmail();
        String password = request.getPassword() == null ? "" : request.getPassword();

        if (userName.isEmpty() || password.length() < 4) {
            return new ResultPayload(false, "INVALID_INPUT", "Username required, password min 4 chars.");
        }
        System.out.print(userName+email);
        if (playerDao.isUsernameExist(userName)) {
            return new ResultPayload(false, "DUPLICATE_USERNAME", "Username already exists.");
        }
        Player player = new Player(userName,email,password);
        try {
            playerDao.register(player);
            return new ResultPayload(true, "OK", "Registered successfully.");
        } catch (SQLException ex) {
           return new ResultPayload(false, "INVALID_INPUT", "Un expected behavior");
        } 
    }

    public ResultPayload login(RegisterRequest request) {
        String userName = clean(request.getUsername());
        String password= request.getPassword() == null ? "" : request.getPassword();

  if (userName.isEmpty() || password.length() < 4) {
            return new ResultPayload(false, "INVALID_INPUT", "Username required, password min 4 chars.");
        }
        
        if (!playerDao.isUsernameExist(userName)) {
            return new ResultPayload(false, "INVALID_INPUT", "Username not found");
        }
        
        try {
            playerDao.login(userName, password);
            return new ResultPayload(true, "OK", "Registered successfully.");
        } catch (SQLException ex) {
           return new ResultPayload(false, "INVALID_INPUT", "Un expected behavior");
        } 
    }
    public List<Player> getOnlineAndInGamePlayers(String userName) {
        return playerDao.getLeaderboardPlayers(userName);
    }
    private String clean(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }
}
