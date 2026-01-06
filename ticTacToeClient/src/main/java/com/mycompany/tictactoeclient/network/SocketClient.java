/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.function.Consumer;
import javafx.application.Platform;

/**
 *
 * @author yasse
 */
public final class SocketClient {
   private final Gson gson = new Gson();

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private volatile boolean running;
    private Consumer<NetworkMessage> onMessage;

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void connect(String host, int port) throws Exception {
        if (isConnected()) return;

        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        running = true;
        new Thread(this::readLoop, "socket-read-loop").start();
    }

    private void readLoop() {
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                NetworkMessage msg = gson.fromJson(line, NetworkMessage.class);
                if (onMessage != null) Platform.runLater(() -> onMessage.accept(msg));
            }
        } catch (Exception ignored) {
        } finally {
            closeLocal();
        }
    }

    public void send(NetworkMessage msg) throws Exception {
        synchronized (this) {
            out.write(gson.toJson(msg));
            out.write("\n");
            out.flush();
        }
    }

    public void disconnect() {
        running = false;
        closeLocal();
    }

    private void closeLocal() {
        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
    }

    public void setOnMessage(Consumer<NetworkMessage> onMessage) {
        this.onMessage = onMessage;
    }

    public Gson gson() { return gson; }
}