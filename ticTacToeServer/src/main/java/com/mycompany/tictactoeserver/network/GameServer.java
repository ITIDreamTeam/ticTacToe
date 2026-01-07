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


/**
 *
 * @author yasse
 */
public final class GameServer {
    private static GameServer instance;
    
    private final int port = 5005;
    private final Gson gson = new Gson();
    private final PlayerDaoImpl playerDao = new PlayerDaoImpl();
    
    private ServerSocket serverSocket;
    private volatile boolean running;
    
    private final ClientRegistry registry = new ClientRegistry();
    private final AuthService auth = new AuthService(playerDao);
    private final MessageRouter router = new MessageRouter(gson, registry, auth);
    
    private GameServer() {}
    
    public static synchronized GameServer getInstance() {
        if (instance == null) {
            instance = new GameServer();
        }
        return instance;
    }

    public void start() throws Exception {
        if (running) {
            System.out.println("Server is already running");
            return;
        }
        
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Server listening on port " + port);

            while (running) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());
                
                ClientSession session = new ClientSession(socket, gson, router);
                new Thread(session, "client-" + socket.getPort()).start();
            }
        } catch (IOException ex) {
            if (running) {
                System.err.println("Server error: " + ex.getMessage());
                throw ex;
            } else {
                System.out.println("Server stopped successfully");
            }
        }
    }

    public void stop() {
        if (!running) return;
        running = false;    
        registry.disconnectAll();     
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        System.out.println("Server stopped");
    }

    public boolean isRunning() {
        return running;
    }
    
    public ClientRegistry getRegistry() {
        return registry;
    }
}
