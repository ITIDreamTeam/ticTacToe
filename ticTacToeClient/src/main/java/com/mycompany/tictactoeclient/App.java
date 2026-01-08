package com.mycompany.tictactoeclient;

import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.NetworkClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * JavaFX App
 */
public class App extends Application {

 private static Scene scene;
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML("home"), 640, 480);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Tic Tac Toe Online");
        stage.setOnCloseRequest(e -> handleAppClose());
        stage.show();
    }
    
    @Override
    public void init() {
        NetworkClient client = NetworkClient.getInstance();
        
        client.setGlobalErrorHandler(errorMsg -> 
            Platform.runLater(() -> showError("Server Error", errorMsg))
        );
        
        client.setOnDisconnected(() -> 
            Platform.runLater(() -> {
                UserSession.getInstance().setOffline();
                showWarning("Connection Lost", 
                    "Connection to server was lost. You have been logged out.");
                try {
                    setRoot("home");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            })
        );
    }
    
    @Override
    public void stop() {
        UserSession.getInstance().logout();
        handleAppClose();
    }
    
    private void handleAppClose() {
        UserSession session = UserSession.getInstance();
        if (session.isLoggedIn()) {
            session.logout();
        }
    }
    
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }
    
    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }
    
    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }
    
    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        if (primaryStage != null) {
            alert.initOwner(primaryStage);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void main(String[] args) {
        launch();
    }
}