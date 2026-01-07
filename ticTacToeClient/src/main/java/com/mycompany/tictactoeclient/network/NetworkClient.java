/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network;

import com.google.gson.Gson;
import com.mycompany.tictactoeclient.network.dtos.ErrorPayload;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import javafx.application.Platform;

/**
 *
 * @author yasse
 */
public class NetworkClient {
    private static final NetworkClient INSTANCE = new NetworkClient();
    private final Gson gson = new Gson();
    private final Map<MessageType, List<Consumer<NetworkMessage>>> listeners = new ConcurrentHashMap<>();
    
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private volatile boolean running;
    
    private String host = "127.0.0.1";
    private int port = 5005;
    
    // Global handlers
    private Consumer<String> globalErrorHandler;
    private Runnable onDisconnected;
    
    private NetworkClient() {}
    
    public static NetworkClient getInstance() { return INSTANCE; }
    
    public void configure(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed() && running;
    }
    
    public void connect() throws Exception {
        if (isConnected()) return;
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        running = true;
        new Thread(this::readLoop, "network-reader").start();
    }
    
    private void readLoop() {
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                NetworkMessage msg = gson.fromJson(line, NetworkMessage.class);
                Platform.runLater(() -> notifyListeners(msg));
            }
        } catch (Exception e) {
            if (running && onDisconnected != null) {
                Platform.runLater(onDisconnected);
            }
        } finally {
            closeSocket();
        }
    }
    
    public void send(NetworkMessage msg) throws Exception {
        synchronized (this) {
            if (!isConnected()) {
                throw new Exception("Not connected to server");
            }
            out.write(gson.toJson(msg));
            out.write("\n");
            out.flush();
        }
    }
    
    public void disconnect() {
        running = false;
        closeSocket();
    }
    
    private void closeSocket() {
        try { 
            if (socket != null) socket.close(); 
        } catch (Exception ignored) {}
        socket = null;
        in = null;
        out = null;
    }
    
    public void on(MessageType type, Consumer<NetworkMessage> listener) {
        listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(listener);
    }
    
    public void off(MessageType type, Consumer<NetworkMessage> listener) {
        List<Consumer<NetworkMessage>> list = listeners.get(type);
        if (list != null) {
            list.remove(listener);
        }
    }
    
    public void clearListeners() {
        listeners.clear();
    }
    
    private void notifyListeners(NetworkMessage msg) {
        if (msg.getType() == MessageType.ERROR) {
            if (globalErrorHandler != null) {
                ErrorPayload err = gson.fromJson(msg.getPayload(), ErrorPayload.class);
                globalErrorHandler.accept(err.getCode() + ": " + err.getMessage());
            }
        }
        
        List<Consumer<NetworkMessage>> list = listeners.get(msg.getType());
        if (list != null) {
            list.forEach(l -> l.accept(msg));
        }
    }
    
    public void setGlobalErrorHandler(Consumer<String> handler) {
        this.globalErrorHandler = handler;
    }
    
    public void setOnDisconnected(Runnable handler) {
        this.onDisconnected = handler;
    }
    
    public Gson getGson() { return gson; }

}