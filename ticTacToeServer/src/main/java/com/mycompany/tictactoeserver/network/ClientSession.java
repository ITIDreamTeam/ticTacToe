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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author yasse
 */
public final class ClientSession implements Runnable {
    private final Socket socket;
    private final Gson gson;

    private BufferedReader in;
    private BufferedWriter out;

    private volatile boolean running = true;
    private volatile String username; 

    private final BiConsumer<ClientSession, NetworkMessage> onMessage;
    private final Consumer<ClientSession> onDisconnect;

    public ClientSession(Socket socket, Gson gson,
                         BiConsumer<ClientSession, NetworkMessage> onMessage,
                         Consumer<ClientSession> onDisconnect) {
        this.socket = socket;
        this.gson = gson;
        this.onMessage = onMessage;
        this.onDisconnect = onDisconnect;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public void run() {
        try (Socket s = socket) {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

            String line;
            while (running && (line = in.readLine()) != null) {
                NetworkMessage msg = gson.fromJson(line, NetworkMessage.class);
                onMessage.accept(this, msg);
            }
        } catch (Exception ignored) {
        } finally {
            onDisconnect.accept(this);
        }
    }

    public void send(NetworkMessage msg) {
        try {
            synchronized (this) {
                out.write(gson.toJson(msg));
                out.write("\n");
                out.flush();
            }
        } catch (Exception ignored) {
        }
    }

    public void close() {
        running = false;
        try { socket.close(); } catch (Exception ignored) {}
    }
}