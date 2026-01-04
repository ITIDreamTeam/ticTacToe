/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.data.dataSource.connection;

import com.mycompany.tictactoeserver.App;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Basmala
 */
public class ServerConnection {
    
    ServerSocket serverSocket;
    
    public ServerConnection(){
        try{
            serverSocket = new ServerSocket(5005);
            System.out.println("Ready to connect");
            while(true){
                Socket socket = serverSocket.accept();
                new ServerHandler(socket);
                
            }
        }catch (IOException ex){
            System.getLogger(App.class.getName()).log(System.Logger.Level.ERROR, ex.getMessage());
        } finally{
            try{
                serverSocket.close();
            }catch (IOException ex){
                System.getLogger(App.class.getName()).log(System.Logger.Level.ERROR, ex.getMessage());
            }
        }
    }
}
