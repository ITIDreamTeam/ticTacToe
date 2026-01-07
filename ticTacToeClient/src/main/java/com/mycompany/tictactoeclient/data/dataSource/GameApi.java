/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.data.dataSource;

import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.dtos.GameMoveDto;

/**
 *
 * @author yasse
 */
public class GameApi {
     private final NetworkClient client;
    
    public GameApi(NetworkClient client) {
        this.client = client;
    }
    
    public void requestOnlinePlayers() throws Exception {
        NetworkMessage msg = new NetworkMessage(
            MessageType.GET_ONLINE_PLAYERS,
                UserSession.getInstance().getUsername(),
                "server",
            client.getGson().toJsonTree(null)
        );
        client.send(msg);
    }
    
    public void sendGameInvite(String targetUsername) throws Exception {
        NetworkMessage msg = new NetworkMessage(
            MessageType.SEND_REQUEST,
            UserSession.getInstance().getUsername(),
            targetUsername,
            null
        );
        client.send(msg);
    }
    
    public void acceptInvite(String senderUsername) throws Exception {
        NetworkMessage msg = new NetworkMessage(
            MessageType.ACCEPT_REQUEST,
            UserSession.getInstance().getUsername(),
            senderUsername,
            null
        );
        client.send(msg);
    }
    
    public void declineInvite(String senderUsername) throws Exception {
        NetworkMessage msg = new NetworkMessage(
            MessageType.DECLINE_REQUEST,
            UserSession.getInstance().getUsername(),
            senderUsername,
            null
        );
        client.send(msg);
    }
    
    public void sendGameMove(String opponentUsername, int row, int col ) throws Exception {
        GameMoveDto request = new GameMoveDto(row, col);
        NetworkMessage msg = new NetworkMessage(
            MessageType.GAME_MOVE,
            UserSession.getInstance().getUsername(),
            opponentUsername,
            client.getGson().toJsonTree(request)
        );
        client.send(msg);
    }
}
