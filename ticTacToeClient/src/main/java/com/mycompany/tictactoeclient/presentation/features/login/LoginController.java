/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.login;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.request.RegisterRequest;
import com.mycompany.tictactoeclient.network.response.ResultPayload;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
/**
 * FXML Controller class
 *
 * @author Nadin
 */
public class LoginController implements Initializable {


 @FXML private TextField nameTextField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private ImageView eyeIcon;

    
    private Image eyeOpenImage;
    private Image eyeClosedImage;
    
    private final NetworkClient client = NetworkClient.getInstance();
    private final UserSession session = UserSession.getInstance();
    
    private Consumer<NetworkMessage> loginResultListener;
    private volatile boolean isProcessing = false;
    @FXML
    private ToggleButton togglePassButton;
    @FXML
    private Button signInButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPasswordFields();
        loadIcons();
        setupListeners();
    }
    
    private void setupPasswordFields() {
        passwordTextField.setVisible(false);
        passwordTextField.setManaged(false);
    }
    
    private void loadIcons() {
        try {
            eyeOpenImage = new Image(getClass().getResource("/icons/visibility-on_1.png").toExternalForm());
            eyeClosedImage = new Image(getClass().getResource("/icons/Visibility-off-02.png").toExternalForm());
            eyeIcon.setImage(eyeClosedImage);
        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
        }
    }
    
    private void setupListeners() {
        loginResultListener = this::handleLoginResult;
        client.on(MessageType.LOGIN_RESULT, loginResultListener);
    }
    
    public void cleanup() {
        client.off(MessageType.LOGIN_RESULT, loginResultListener);
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        navigateToHome();
    }

    private void togglePassword(ActionEvent event) {
        if (togglePassButton.isSelected()) {
            // Show password
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            eyeIcon.setImage(eyeOpenImage);
        } else {
            // Hide password
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            eyeIcon.setImage(eyeClosedImage);
        }
    }

    @FXML
    private void onSignInClicked(ActionEvent event) {
        if (isProcessing) {
            return; // Prevent double submission
        }
        
        String username = nameTextField.getText().trim();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();

        // Validation
        if (!validateInput(username, password)) {
            return;
        }

        isProcessing = true;
        disableForm(true);
        
        // Connect and login
        new Thread(() -> {
            try {
                client.configure("127.0.0.1", 5005);
                client.connect();
                
                // Create login request
                RegisterRequest req = new RegisterRequest(username, null, password); 
                NetworkMessage msg = new NetworkMessage(
                    MessageType.LOGIN,
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
        }, "login-thread").start();
    }
    
    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            App.showWarning("Invalid Input", "Username is required.");
            return false;
        }
        
        if (password.isEmpty()) {
            App.showWarning("Invalid Input", "Password is required.");
            return false;
        }
        
        if (password.length() < 4) {
            App.showWarning("Invalid Input", "Password must be at least 4 characters.");
            return false;
        }
        
        return true;
    }

    @FXML
    private void onSignUpClicked(ActionEvent event) {
        navigateToRegister();
    }

    private void handleLoginResult(NetworkMessage msg) {
        if (!isProcessing) return;
        
        isProcessing = false;
        disableForm(false);
        
        ResultPayload result = client.getGson().fromJson(msg.getPayload(), ResultPayload.class);
        
        if (result.isSuccess()) {
            // Login successful - establish session
            String username = nameTextField.getText().trim();
            
            // Note: We don't have email from login response, so pass null or fetch it separately
            session.login(username, null);
            
            App.showInfo("Login Successful", "Welcome back, " + username + "!");
            
            // Navigate to home (user is now logged in)
            navigateToHome();
            
        } else {
            // Login failed - disconnect
            App.showError("Login Failed", result.getMessage());
            client.disconnect();
        }
    }
    
    private void disableForm(boolean disable) {
        nameTextField.setDisable(disable);
        passwordField.setDisable(disable);
        passwordTextField.setDisable(disable);
        togglePassButton.setDisable(disable);
        signInButton.setDisable(disable);
    }
    
    private void navigateToHome() {
        try {
            cleanup();
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
            App.showError("Navigation Error", "Cannot navigate to home page.");
        }
    }
    
    private void navigateToRegister() {
        try {
            cleanup();
            App.setRoot("register");
        } catch (IOException e) {
            e.printStackTrace();
            App.showError("Navigation Error", "Cannot navigate to register page.");
        }
    }

    @FXML
    private void toggalePassword(MouseEvent event) {
    }
}

