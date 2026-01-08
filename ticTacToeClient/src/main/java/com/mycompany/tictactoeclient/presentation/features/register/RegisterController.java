/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.register;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.request.RegisterRequest;
import com.mycompany.tictactoeclient.network.response.ResultPayload;
import com.mycompany.tictactoeclient.shared.Navigation;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Nadin
 */
public class RegisterController implements Initializable {

    @FXML
    private TextField userNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ImageView eyeIconPassword;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField confirmPasswordTextField;
    @FXML
    private ImageView eyeIconConfirmPass;
    @FXML
    private ToggleButton togglePassButton;
    @FXML
    private ToggleButton toggleConfirmPassButton;
    @FXML
    private Button registerButton;

    private Image eyeOpenImage;
    private Image eyeClosedImage;

    private final NetworkClient client = NetworkClient.getInstance();
    private final UserSession session = UserSession.getInstance();

    private Consumer<NetworkMessage> registerResultListener;
    private volatile boolean isProcessing = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPasswordFields();
        loadIcons();
        setupListeners();
    }

    private void setupPasswordFields() {
        passwordTextField.setVisible(false);
        passwordTextField.setManaged(false);
        confirmPasswordTextField.setVisible(false);
        confirmPasswordTextField.setManaged(false);
    }

    private void loadIcons() {
        try {
            eyeOpenImage = new Image(getClass().getResource("/icons/visibility-on_1.png").toExternalForm());
            eyeClosedImage = new Image(getClass().getResource("/icons/Visibility-off-02.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
        }
    }

    private void setupListeners() {
        registerResultListener = this::handleRegisterResult;
        client.on(MessageType.REGISTER_RESULT, registerResultListener);
    }

    public void cleanup() {
        client.off(MessageType.REGISTER_RESULT, registerResultListener);
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        cleanup();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onRegisterClicked(ActionEvent event) {
        if (isProcessing) {
            return;
        }

        String username = userNameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();
        String confirm = confirmPasswordField.isVisible() ? confirmPasswordField.getText() : confirmPasswordTextField.getText();

        if (!validateInput(username, email, password, confirm)) {
            return;
        }

        isProcessing = true;
        disableForm(true);
        new Thread(() -> {
            try {
                client.configure("127.0.0.1", 5005);
                client.connect();

                RegisterRequest req = new RegisterRequest(username, email, password);
                NetworkMessage msg = new NetworkMessage(
                        MessageType.REGISTER,
                        username,
                        "Server",
                        client.getGson().toJsonTree(req)
                );
                client.send(msg);

            } catch (Exception e) {
                Platform.runLater(() -> {
                    App.showError("Connection Error", "Failed to connect to server: " + e.getMessage());
                    isProcessing = false;
                    disableForm(false);
                    client.disconnect();
                });
            }
        }, "register-thread").start();
    }

    private boolean validateInput(String username, String email, String password, String confirm) {
        if (username.isEmpty()) {
            App.showWarning("Invalid Input", "Username is required.");
            return false;
        }

        if (username.length() < 3) {
            App.showWarning("Invalid Input", "Username must be at least 3 characters.");
            return false;
        }

        if (email.isEmpty() || !email.contains("@")) {
            App.showWarning("Invalid Input", "Please enter a valid email address.");
            return false;
        }

        if (password.length() < 4) {
            App.showWarning("Invalid Input", "Password must be at least 4 characters.");
            return false;
        }

        if (!password.equals(confirm)) {
            App.showWarning("Invalid Input", "Passwords do not match.");
            return false;
        }

        return true;
    }

    @FXML
    private void onLoginClicked(ActionEvent event) {
        cleanup();
        Navigation.navigateTo(Navigation.loginPage);
    }

    private void handleRegisterResult(NetworkMessage msg) {
        if (!isProcessing) {
            return;
        }

        isProcessing = false;
        disableForm(false);

        ResultPayload result = client.getGson().fromJson(msg.getPayload(), ResultPayload.class);

        if (result.isSuccess()) {
            // Registration successful - establish session and login
            String username = userNameTextField.getText().trim();
            String email = emailTextField.getText().trim();

            session.login(username, email);

            App.showInfo("Registration Successful",
                    "Welcome " + username + "! You have been registered and logged in.");
            cleanup();
            Navigation.navigateTo(Navigation.homePage);

        } else {
            App.showError("Registration Failed", result.getMessage());
            client.disconnect();
        }
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

    private void disableForm(boolean disable) {
        userNameTextField.setDisable(disable);
        emailTextField.setDisable(disable);
        passwordField.setDisable(disable);
        passwordTextField.setDisable(disable);
        confirmPasswordField.setDisable(disable);
        confirmPasswordTextField.setDisable(disable);
        registerButton.setDisable(disable);
    }

    @FXML
    private void toggalePassword(ActionEvent event) {
        toggleVisibility(togglePassButton, passwordField, passwordTextField, eyeIconPassword);
    }

    @FXML
    private void toggaleConfirmPassword(ActionEvent event) {
        toggleVisibility(toggleConfirmPassButton, confirmPasswordField, confirmPasswordTextField, eyeIconConfirmPass);
    }

}
