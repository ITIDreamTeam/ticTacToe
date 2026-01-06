/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author yasse
 */
public final class ClientRegistry {
    private final ConcurrentHashMap<String, ClientSession> online = new ConcurrentHashMap<>();

    public boolean isOnline(String username) {
        return online.containsKey(username);
    }

    // returns false if username already online
    public boolean addOnline(String username, ClientSession session) {
        return online.putIfAbsent(username, session) == null;
    }

    public void remove(ClientSession session) {
        if (session.getUsername() == null) return;
        online.remove(session.getUsername(), session);
    }

    public ClientSession get(String username) {
        return online.get(username);
    }

    public List<String> onlineUsernames() {
        return new ArrayList<>(online.keySet());
    }

    public Iterable<ClientSession> allSessions() {
        return online.values();
    }
}