package com.mycompany.tictactoeserver;

import com.mycompany.tictactoeserver.data.dataSource.connection.ServerHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * JavaFX App
 */
public class App extends Application {
    
    ServerSocket serverSocket;
    
    public App(){
        try{
            serverSocket = new ServerSocket(5004);
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
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("home"), 640, 480);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        new App();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}