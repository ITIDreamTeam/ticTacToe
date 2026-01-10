package com.mycompany.tictactoeclient.presentation.features.change_password;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.network.UserSession;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.request.ChangePasswordRequest;
import com.mycompany.tictactoeclient.network.response.ResultPayload;
import com.mycompany.tictactoeclient.shared.Navigation;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.util.function.Consumer;
import javafx.application.Platform;

public class ChangePasswordController implements Initializable {

    @FXML
    private Button backBtn;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField confirmPasswordTextField;
    @FXML
    private ToggleButton togglePassBtn;
    @FXML
    private ImageView passEyeIcon;
    @FXML
    private ToggleButton toggleConfirmPassBtn;
    @FXML
    private ImageView confirmPassEyeIcon;

    private Image eyeOpenImage;
    private Image eyeClosedImage;

    private final NetworkClient client = NetworkClient.getInstance();
    private final UserSession session = UserSession.getInstance();
    private Consumer<NetworkMessage> responseListener;
    private volatile boolean isProcessing = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        loadIcons();
        setupInitialState(passwordTextField, passwordField, passEyeIcon);
        setupInitialState(confirmPasswordTextField, confirmPasswordField, confirmPassEyeIcon);
        setupListeners();
    }

    private void setupListeners() {
        responseListener = this::handleResponse;
        client.on(MessageType.CHANGE_PASSWORD_RESULT, responseListener);
    }

    // Clean up listener when leaving page
    public void cleanup() {
        client.off(MessageType.CHANGE_PASSWORD_RESULT, responseListener);
    }

    private void loadIcons() {
        try {
            eyeOpenImage = new Image(getClass().getResource("/icons/visibility-on_1.png").toExternalForm());
            eyeClosedImage = new Image(getClass().getResource("/icons/Visibility-off-02.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
        }
    }

    private void setupInitialState(TextField tf, PasswordField pf, ImageView icon) {
        tf.setVisible(false);
        pf.setVisible(true);
        if (eyeClosedImage != null) {
            icon.setImage(eyeClosedImage);
        }
    }

    @FXML
    private void onSaveBtnClicked(ActionEvent event) {
        if (isProcessing) {
            return;
        }

        String newPass = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();
        String confirmPass = confirmPasswordField.isVisible() ? confirmPasswordField.getText() : confirmPasswordTextField.getText();

        if (newPass.isEmpty() || newPass.length() < 4) {
            showAlert("Invalid Input", "Password must be at least 4 characters.", Alert.AlertType.WARNING);
            return;
        }
        if (!newPass.equals(confirmPass)) {
            showAlert("Invalid Input", "Passwords do not match.", Alert.AlertType.WARNING);
            return;
        }

        isProcessing = true;
        saveBtn.setDisable(true);

        new Thread(() -> {
            try {
                if (!client.isConnected()) {
                    client.connect();
                }

                ChangePasswordRequest req = new ChangePasswordRequest(session.getUsername(), newPass);

                NetworkMessage msg = new NetworkMessage(
                        MessageType.CHANGE_PASSWORD,
                        session.getUsername(),
                        "Server",
                        client.getGson().toJsonTree(req)
                );
                client.send(msg);

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Connection Error", e.getMessage(), Alert.AlertType.ERROR);
                    isProcessing = false;
                    saveBtn.setDisable(false);
                });
            }
        }).start();
    }

    private void handleResponse(NetworkMessage msg) {
        if (!isProcessing) {
            return;
        }

        Platform.runLater(() -> {
            isProcessing = false;
            saveBtn.setDisable(false);

            ResultPayload result = client.getGson().fromJson(msg.getPayload(), ResultPayload.class);

            if (result.isSuccess()) {
                showAlert("Success", "Password changed successfully!", Alert.AlertType.INFORMATION);
                cleanup();
                Navigation.navigateTo(Navigation.profilePage);
            } else {
                showAlert("Error", (String) result.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void onBackBtnClicked(ActionEvent event) {
        cleanup();
        Navigation.navigateTo(Navigation.profilePage);
    }

    @FXML
    private void onCancelBtnClicked(ActionEvent event) {
        // Just clear fields or go back
        cleanup();
        Navigation.navigateTo(Navigation.profilePage);
    }

    @FXML
    private void onTogglePassClicked(ActionEvent event) {
        toggleVisibility(togglePassBtn, passwordTextField, passwordField, passEyeIcon);
    }

    @FXML
    private void onToggleConfirmPassClicked(ActionEvent event) {
        toggleVisibility(toggleConfirmPassBtn, confirmPasswordTextField, confirmPasswordField, confirmPassEyeIcon);
    }

    private void toggleVisibility(ToggleButton btn, TextField tf, PasswordField pf, ImageView icon) {
        if (btn.isSelected()) {
            tf.setVisible(true);
            pf.setVisible(false);
            if (eyeOpenImage != null) {
                icon.setImage(eyeOpenImage);
            }
            tf.setText(pf.getText()); // Ensure text sync
        } else {
            tf.setVisible(false);
            pf.setVisible(true);
            if (eyeClosedImage != null) {
                icon.setImage(eyeClosedImage);
            }
            pf.setText(tf.getText()); 
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
