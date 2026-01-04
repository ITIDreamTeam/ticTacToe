/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

/**
 *
 * @author Basmala
 */
public class ServerHandler extends Thread {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private static Vector<ServerHandler> clients = new Vector<>();
    
    public ServerHandler(Socket socket) {
        System.out.println("Start connection with client");
        this.socket = socket;
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            clients.add(this);
            start();

        } catch (IOException ex) {
             System.getLogger(ServerHandler.class.getName()).log(System.Logger.Level.ERROR, ex.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                String message = dataInputStream.readUTF();
                sendMessageToAll(message);
            }
        } catch (IOException ex) {
            System.out.println("Client left");
        } finally {
            closeConnection(); 
        }
    }
    
    private void closeConnection() {
        try {
            clients.remove(this);
            if(dataInputStream != null) dataInputStream.close();
            if(dataOutputStream != null) dataOutputStream.close();
            if(socket != null) socket.close();
        } catch (IOException ex) {
            System.out.println("closeConnection ");
             System.getLogger(ServerHandler.class.getName()).log(System.Logger.Level.ERROR, ex.getMessage());
        }
    }

    private void sendMessageToAll(String message) {
        for (int i = 0; i < clients.size(); i++) {
            ServerHandler client = clients.get(i);
            try {
                client.dataOutputStream.writeUTF(message);
            } catch (IOException ex) {
                clients.remove(client);
                client.closeConnection();
                i--;
            }
        }
    }
    

}
