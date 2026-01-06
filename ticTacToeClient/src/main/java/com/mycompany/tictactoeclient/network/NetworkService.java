/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 *
 * @author yasse
 */
public class NetworkService {
    private static final NetworkService INSTANCE = new NetworkService();

    private final SocketClient socket = new SocketClient();
    private final Map<MessageType, List<Consumer<NetworkMessage>>> listeners = new ConcurrentHashMap<>();

    private String host = "127.0.0.1";
    private int port = 5005;

    private NetworkService() {
        socket.setOnMessage(this::dispatch);
    }

    public static NetworkService getInstance() {
        return INSTANCE;
    }

    public void configure(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    // Call from background thread (don’t block JavaFX)
    public void connectIfNeeded() throws Exception {
        socket.connect(host, port);
    }

    public void disconnectSilently() {
        socket.disconnect();
    }

    public void send(NetworkMessage msg) throws Exception {
        socket.send(msg);
    }

    public void on(MessageType type, Consumer<NetworkMessage> listener) {
        listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    private void dispatch(NetworkMessage msg) {
        List<Consumer<NetworkMessage>> list = listeners.get(msg.getType());
        if (list != null) {
            for (Consumer<NetworkMessage> l : list) l.accept(msg);
        }
    }

    public SocketClient socket() { return socket; }
}
