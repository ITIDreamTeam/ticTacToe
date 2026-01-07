/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource.connection;

//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
import com.mycompany.tictactoeserver.data.model.ActiveGame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Basmala
 */
public class ServerHandler extends Thread {

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String playerId;
    private boolean isRunning;
    private static ConcurrentHashMap<String, ServerHandler> onlinePlayers = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ActiveGame> activeGames = new ConcurrentHashMap<>();
    private ExecutorService messageProcessor = Executors.newSingleThreadExecutor();
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    public ServerHandler(Socket socket) {
        System.out.println("Start connection with client");
        this.socket = socket;
        this.isRunning = true;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            start();
            messageProcessor.submit(this::processMessageQueue);
        } catch (IOException ex) {
            System.getLogger(ServerHandler.class.getName()).log(System.Logger.Level.ERROR, ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (isRunning && !socket.isClosed()) {
                String jsonMessage = dataInputStream.readUTF();
                System.out.println("Received from " + playerId + ": " + jsonMessage);
                
                if (!messageQueue.offer(jsonMessage, 100, TimeUnit.MILLISECONDS)) {
                    System.err.println("Message queue full, dropping message");
                }
            }
        } catch (IOException ex) {
            System.out.println("Client disconnected: " + (playerId != null ? playerId : "Unknown"));
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        } finally {
            playerLogout();
            closeConnection();
        }
    }

    private void closeConnection() {
        isRunning = false;
        try {
            if (dataInputStream != null) dataInputStream.close();
            if (dataOutputStream != null) dataOutputStream.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    private void processMessageQueue() {
        while (isRunning) {
            try {
                String jsonMessage = messageQueue.poll(100, TimeUnit.MILLISECONDS);
                if (jsonMessage != null) {
                    processMessage(jsonMessage);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
     
    private void processMessage(String jsonMessage) {
        /*
        Login - Player authentication

        Register - New player registration

        Leaderboard - Get online players

        Invite - Invite player to game

        GameMove - Send game move

        GameState - Get current game state

        GameResult - Send game result

        Logout - Player goes offline

        Heartbeat - Connection keep-alive

        */
    }
    
    private void playerLogout() {
        
    }
    
}
