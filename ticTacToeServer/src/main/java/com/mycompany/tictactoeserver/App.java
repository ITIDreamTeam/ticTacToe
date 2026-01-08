package com.mycompany.tictactoeserver;

import com.mycompany.tictactoeserver.network.GameServer;
import com.mycompany.tictactoeserver.util.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * JavaFX App
 */

public class App extends Application {
    
    
    private static Scene scene;
    private GameServer server;
 
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("home"), 640, 480);
         try (Connection con = DBConnection.getConnection()) {

            if (con != null && !con.isClosed()) {
                System.out.println("Connected to database successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Failed to connect to database");
            e.printStackTrace();
        }
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    @Override
    public void stop(){
    server = GameServer.getInstance(); 
    server.stop();
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