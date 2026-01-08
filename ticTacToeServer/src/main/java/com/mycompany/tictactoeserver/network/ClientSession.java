/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author yasse
 */
public final class ClientSession implements Runnable {
    private final Socket socket;
    private final Gson gson;
    private final MessageRouter router;
    
    private BufferedReader in;
    private BufferedWriter out;
    
    private volatile boolean running = true;
    private volatile String username;

    public ClientSession(Socket socket, Gson gson, MessageRouter router) {
        this.socket = socket;
        this.gson = gson;
        this.router = router;
    }

    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line;
            while (running && (line = in.readLine()) != null) {
                try {
                    NetworkMessage msg = gson.fromJson(line, NetworkMessage.class);
                    System.out.println("Received from " + username + ": " + msg.getType());
                    router.handle(this, msg);
                } catch (Exception e) {
                    System.err.println("Error processing message: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Client session error: " + e.getMessage());
        } finally {
            router.onDisconnect(this);
        }
    }

    public void send(NetworkMessage msg) {
        try {
            synchronized (this) {
                if (out != null && running) {
                    out.write(gson.toJson(msg));
                    out.write("\n");
                    out.flush();
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            close();
        }
    }

    public void close() {
        running = false;
        try { 
            if (socket != null && !socket.isClosed()) {
                socket.close(); 
            }
        } catch (Exception e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }
    
    public boolean isConnected() {
        return running && socket != null && socket.isConnected() && !socket.isClosed();
    }
}