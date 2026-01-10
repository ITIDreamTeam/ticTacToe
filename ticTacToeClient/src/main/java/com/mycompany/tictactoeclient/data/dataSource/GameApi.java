/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.data.dataSource;

import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.UserSession;
import com.mycompany.tictactoeclient.network.dtos.GameMoveDto;
import com.mycompany.tictactoeclient.network.request.InviteRequest;
import com.mycompany.tictactoeclient.network.response.InviteResponse;

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
                null
        );
        client.send(msg);
    }

    public void sendGameInvite(String targetUsername, boolean recordGame) throws Exception {
        InviteRequest request = new InviteRequest(
                UserSession.getInstance().getUsername(),
                targetUsername,
                recordGame
        );

        NetworkMessage msg = new NetworkMessage(
                MessageType.SEND_REQUEST,
                UserSession.getInstance().getUsername(),
                targetUsername,
                client.getGson().toJsonTree(request)
        );
        client.send(msg);
    }

    public void acceptInvite(String senderUsername, boolean recordGame) throws Exception {
        InviteResponse response = new InviteResponse(
                senderUsername,
                UserSession.getInstance().getUsername(),
                true,
                recordGame
        );

        NetworkMessage msg = new NetworkMessage(
                MessageType.ACCEPT_REQUEST,
                UserSession.getInstance().getUsername(),
                senderUsername,
                client.getGson().toJsonTree(response)
        );
        client.send(msg);
    }

    public void declineInvite(String senderUsername) throws Exception {
        InviteResponse response = new InviteResponse(
                senderUsername,
                UserSession.getInstance().getUsername(),
                false,
                false
        );

        NetworkMessage msg = new NetworkMessage(
                MessageType.DECLINE_REQUEST,
                UserSession.getInstance().getUsername(),
                senderUsername,
                client.getGson().toJsonTree(response)
        );
        client.send(msg);
    }

    public void sendGameMove(String opponentUsername, int row, int col) throws Exception {
        GameMoveDto request = new GameMoveDto(row, col);
        NetworkMessage msg = new NetworkMessage(
                MessageType.GAME_MOVE,
                UserSession.getInstance().getUsername(),
                opponentUsername,
                client.getGson().toJsonTree(request)
        );
        client.send(msg);
    }
    
    public void sendSurrender() throws Exception {
    NetworkMessage msg = new NetworkMessage(
            MessageType.SURRENDER,
            UserSession.getInstance().getUsername(),
            "server",
            null
    );
    client.send(msg);
}
    public void sendFindMatchRequest() throws Exception {

        client.send(new NetworkMessage(MessageType.FIND_MATCH, UserSession.getInstance().getUsername(), null, null));

    }

    public void sendGameMove(GameMoveDto move) throws Exception {
       
            NetworkMessage msg = new NetworkMessage(MessageType.GAME_MOVE, UserSession.getInstance().getUsername(), null, client.getGson().toJsonTree(move));
            client.send(msg);

    }

}
