/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import com.google.gson.Gson;
import com.mycompany.tictactoeserver.data.dataSource.dao.PlayerDaoImpl;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 *
 * @author yasse
 */
public final class GameServer {
    private final ExecutorService clientPool = Executors.newCachedThreadPool();
    int port = 5005;
     Gson gson = new Gson();
     PlayerDaoImpl playerDao = new PlayerDaoImpl();
    private volatile boolean running;
    private ServerSocket serverSocket;

       public GameServer() {

    }
        ClientRegistry registry = new ClientRegistry();
        AuthService auth = new AuthService(playerDao);
        MessageRouter router = new MessageRouter(gson, registry, auth);

    public void start() throws Exception {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);
            while (running) {
                Socket socket = serverSocket.accept();
                                ClientSession session = new ClientSession(
                        socket,
                        gson,
                        router::handle,
                        router::onDisconnect
                );
                new Thread(session, "client-session").start();
            }
        }catch(IOException ex ){
            ex.printStackTrace();
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) { }
        clientPool.shutdownNow();
    }

    public boolean isRunning() {
        return running;
    }
}
