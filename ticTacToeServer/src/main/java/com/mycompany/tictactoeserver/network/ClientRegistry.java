/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author yasse
 */
public final class ClientRegistry {
    private final ConcurrentHashMap<String, ClientSession> online = new ConcurrentHashMap<>();

    public boolean isOnline(String username) {
        return online.containsKey(normalizeKey(username));
    }

    public boolean addOnline(String username, ClientSession session) {
        String key = normalizeKey(username);
        return online.putIfAbsent(key, session) == null;
    }

    public void remove(ClientSession session) {
        if (session.getUsername() == null) return;
        online.remove(normalizeKey(session.getUsername()), session);
    }
    
    public void remove(String username) {
        online.remove(normalizeKey(username));
    }

    public ClientSession get(String username) {
        return online.get(normalizeKey(username));
    }

    public List<String> onlineUsernames() {
        return new ArrayList<>(online.keySet());
    }

    public Collection<ClientSession> allSessions() {
        return online.values();
    }
    
    public int count() {
        return online.size();
    }
    
    public void disconnectAll() {
        for (ClientSession session : online.values()) {
            try {
                session.close();
            } catch (Exception e) {
                System.err.println("Error disconnecting client: " + e.getMessage());
            }
        }
        online.clear();
    }
    
    private String normalizeKey(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }
}