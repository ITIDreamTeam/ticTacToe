/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network;

import com.google.gson.Gson;
import com.mycompany.tictactoeserver.network.request.RegisterRequest;

/**
 *
 * @author yasse
 */
public final class AuthApi {
    private final Gson gson = new Gson();

    private final NetworkService network;

    public AuthApi(NetworkService network) {
        this.network = network;
    }

    public void register(String username,String email, String password) throws Exception {
        RegisterRequest cmd = new RegisterRequest(username,email, password);
        network.send(new NetworkMessage(MessageType.REGISTER, null, null, gson.toJsonTree(cmd)));
    }

    public void login(String username, String password) throws Exception {
        RegisterRequest cmd = new RegisterRequest(username, password);
        network.send(new NetworkMessage(MessageType.LOGIN, null, null, gson.toJsonTree(cmd)));
    }

    public void disconnect() throws Exception {
        network.send(new NetworkMessage(MessageType.DISCONNECT, null, null, null));
        network.disconnectSilently();
    }
}
