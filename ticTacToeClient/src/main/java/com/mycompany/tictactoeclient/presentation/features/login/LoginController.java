/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.login;

import com.google.gson.Gson;
import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.AuthApi;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.NetworkService;
import com.mycompany.tictactoeserver.network.dtos.ErrorPayload;
import com.mycompany.tictactoeserver.network.response.ResultPayload;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
/**
 * FXML Controller class
 *
 * @author Nadin
 */
public class LoginController implements Initializable {


     @FXML private TextField emailTextField;     // treat as username for MVP
    @FXML private PasswordField passwordField;
    @FXML private ImageView eyeIcon;
    @FXML private TextField passwordTextField;

    private boolean passwordVisible = false;

    private final Gson gson = new Gson();
    private final NetworkService net = NetworkService.getInstance();
    private final AuthApi api = new AuthApi(net);

    private volatile boolean awaitingLogin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        passwordTextField.setVisible(false);
        passwordTextField.setManaged(false);

        net.on(MessageType.LOGIN_RESULT, this::handleLoginResult);
        net.on(MessageType.ERROR, this::handleError);
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void toggalePassword(MouseEvent event) {
        if (passwordVisible) {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);

            passwordVisible = false;
        } else {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);

            passwordVisible = true;
        }
    }

    @FXML
    private void onSignInClicked(ActionEvent event) {
        String username = emailTextField.getText().trim();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid input", "Username/password required.");
            return;
        }

        awaitingLogin = true;

        new Thread(() -> {
            try {
                net.configure("127.0.0.1", 5005);
                net.connectIfNeeded();
                api.login(username, password);
            } catch (Exception e) {
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Network error", e.getMessage()));
            }
        }, "login-thread").start();
    }

    @FXML
    private void onSignUpClicked(ActionEvent event) {
        try {
            App.setRoot("register");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handleLoginResult(NetworkMessage msg) {
        if (!awaitingLogin) return;
        awaitingLogin = false;

        ResultPayload r = gson.fromJson(msg.getPayload(), ResultPayload.class);
        if (r.isSuccess()) {
            String username = emailTextField.getText().trim().toLowerCase();
            UserSession.getInstance().login(username);

            try {
                App.setRoot("home");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login failed", r.getCode() + ": " + r.getMessage());
        }
    }

    private void handleError(NetworkMessage msg) {
        ErrorPayload err = gson.fromJson(msg.getPayload(), ErrorPayload.class);
        showAlert(Alert.AlertType.ERROR, "Server error", err.getCode() + ": " + err.getMessage());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

}

