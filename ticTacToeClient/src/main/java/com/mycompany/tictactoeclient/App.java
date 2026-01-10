package com.mycompany.tictactoeclient;

import com.google.gson.Gson;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.presentation.features.game_board.RecordedGameDetails;
import com.mycompany.tictactoeclient.network.UserSession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;
    private static RecordedGameDetails recordedGameDetails;

    private static Alert waitingAlert;

    public static void setWaitingAlert(Alert alert) {
        waitingAlert = alert;
    }

    public static void closeWaitingAlert() {
        if (waitingAlert != null) {
            Platform.runLater(() -> {
                waitingAlert.close();
                waitingAlert = null;
            });
        }
    }

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
        Gson gson = client.getGson();

    client.setGlobalErrorHandler(errorMsg ->
            Platform.runLater(() -> showError("Server Error", errorMsg))
    );

    client.setOnDisconnected(() ->
            Platform.runLater(() -> {
                UserSession.getInstance().clearSession();
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
        NetworkClient.getInstance().disconnect();
    }

    public static <T> T setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        scene.setRoot(root);
        return fxmlLoader.getController();
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

    public static RecordedGameDetails getRecordedGameDetails() {
        return recordedGameDetails;
    }

    public static void setRecordedGameDetails(RecordedGameDetails recordedGameDetails) {
        App.recordedGameDetails = recordedGameDetails;
    }
    
     public static Optional<ButtonType> showConfirmation(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    
    alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
    
    return alert.showAndWait();
}
    public static void main(String[] args) {
        launch();
    }
}
