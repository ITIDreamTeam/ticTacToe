/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.register;

import com.google.gson.Gson;
import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.AuthApi;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.NetworkService;
import com.mycompany.tictactoeclient.network.SocketClient;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author Nadin
 */
public class RegisterController implements Initializable {

    @FXML private TextField userNameTextField;
    @FXML private TextField emailTextField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private ImageView eyeIconPassword;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordTextField;
    @FXML private ImageView eyeIconConfirmPass;
    @FXML private ToggleButton togglePassButton;
    @FXML private ToggleButton toggleConfirmPassButton;

    private Image eyeOpenImage;
    private Image eyeClosedImage;

    private final Gson gson = new Gson();

    private final NetworkService network = NetworkService.getInstance();
    private final AuthApi api = new AuthApi(network);

    private volatile boolean awaitingRegister;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        passwordTextField.setVisible(false);
        passwordTextField.setManaged(false);

        confirmPasswordTextField.setVisible(false);
        confirmPasswordTextField.setManaged(false);

        try {
            eyeOpenImage = new Image(getClass().getResource("/icons/visibility-on_1.png").toExternalForm());
            eyeClosedImage = new Image(getClass().getResource("/icons/Visibility-off-02.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
        }

        network.on(MessageType.REGISTER_RESULT, this::handleRegisterResult);
        network.on(MessageType.ERROR, this::handleError);
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
    private void toggalePassword(ActionEvent event) {
        toggleVisibility(togglePassButton, passwordField, passwordTextField, eyeIconPassword);
    }

    @FXML
    private void toggaleConfirmPassword(ActionEvent event) {
        toggleVisibility(toggleConfirmPassButton, confirmPasswordField, confirmPasswordTextField, eyeIconConfirmPass);
    }

    @FXML
    private void onSignUpClicked(ActionEvent event) {
        String username = userNameTextField.getText().trim();   // Option A identity
        String password = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();
        String email = emailTextField.getText().trim();
        String confirm = confirmPasswordField.isVisible() ? confirmPasswordField.getText() : confirmPasswordTextField.getText();

        // Minimal validation (match server rules)
        if (username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid input", "Username is required.");
            return;
        }
        if (password.length() < 4) {
            showAlert(Alert.AlertType.WARNING, "Invalid input", "Password must be at least 4 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            showAlert(Alert.AlertType.WARNING, "Invalid input", "Passwords do not match.");
            return;
        }

        awaitingRegister = true;

        new Thread(() -> {
            try {
                network.configure("127.0.0.1", 5005); 
                network.connectIfNeeded();
                api.register(username,email, password);
            } catch (Exception e) {
                Platform.runLater(() ->
                        showAlert(Alert.AlertType.ERROR, "Network error", e.getMessage())
                );
            }
        }, "register-thread").start();
    }

    @FXML
    private void onSignInClicked(ActionEvent event) {
        try {
            App.setRoot("login");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handleRegisterResult(NetworkMessage msg) {
        if (!awaitingRegister) return; // ignore if not expecting
        awaitingRegister = false;

        ResultPayload r = gson.fromJson(msg.getPayload(), ResultPayload.class);
        if (r.isSuccess()) {
            showAlert(Alert.AlertType.INFORMATION, "Register success", r.getMessage());
            // simple MVP: go to login screen after register
            try {
                App.setRoot("login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Register failed", r.getCode() + ": " + r.getMessage());
        }
    }

    private void handleError(NetworkMessage msg) {
        ErrorPayload err = gson.fromJson(msg.getPayload(), ErrorPayload.class);
        showAlert(Alert.AlertType.ERROR, "Server error", err.getCode() + ": " + err.getMessage());
    }

    private void toggleVisibility(ToggleButton toggleButton, PasswordField passwordField,
           TextField textField, ImageView eyeIcon) {
        if (toggleButton.isSelected()) {
            textField.setText(passwordField.getText());
            textField.setVisible(true);
            textField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);

            eyeIcon.setImage(eyeOpenImage);
        } else {
            passwordField.setText(textField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            textField.setVisible(false);
            textField.setManaged(false);

            eyeIcon.setImage(eyeClosedImage);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
