package com.mycompany.tictactoeclient.presentation.features.change_password;

import com.mycompany.tictactoeclient.App;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

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

    // Toggle Buttons & Icons
    @FXML
    private ToggleButton togglePassBtn;
    @FXML
    private ImageView passEyeIcon;
    @FXML
    private ToggleButton toggleConfirmPassBtn;
    @FXML
    private ImageView confirmPassEyeIcon;

    // Image variables
    private Image eyeOpenImage;
    private Image eyeClosedImage;
    // storing the original password to revert back to when clicking Cancel button
    private String originalPassword = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Bind the text fields
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        // 2. Load the images
        try {
            // "Open Eye" = Visible Password
            eyeOpenImage = new Image(getClass().getResource("/icons/visibility-on_1.png").toExternalForm());
            // "Slash Eye" = Hidden Password
            eyeClosedImage = new Image(getClass().getResource("/icons/Visibility-off-02.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
        }

        // 3. SIMULATE FETCHING USER DATA
        // Later, you will replace "12345678" with data from your User object
        originalPassword = "12345678";

        // 4. Set the fields to this original value immediately
        passwordField.setText(originalPassword);
        confirmPasswordField.setText(originalPassword);

        // 3. Set Initial State
        // Defaults: Text Hidden, Dots Shown, Icon = Slash (Closed)
        setupInitialState(passwordTextField, passwordField, passEyeIcon);
        setupInitialState(confirmPasswordTextField, confirmPasswordField, confirmPassEyeIcon);
    }

    private void setupInitialState(TextField tf, PasswordField pf, ImageView icon) {
        tf.setVisible(false);
        pf.setVisible(true);
        if (eyeClosedImage != null) {
            icon.setImage(eyeClosedImage); // Default to Slash
        }
    }

    // --- TOGGLE ACTIONS ---
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
            // STATE: SHOW PASSWORD
            // We want the Open Eye here
            tf.setVisible(true);
            pf.setVisible(false);
            if (eyeOpenImage != null) {
                icon.setImage(eyeOpenImage);
            }
        } else {
            // STATE: HIDE PASSWORD
            // We want the Slash Eye here
            tf.setVisible(false);
            pf.setVisible(true);
            if (eyeClosedImage != null) {
                icon.setImage(eyeClosedImage);
            }
        }
    }

    @FXML
    private void onBackBtnClicked(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }

    @FXML
    private void onSaveBtnClicked(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }

    @FXML
    private void onCancelBtnClicked(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException ex) {
           ex.printStackTrace();
        }
        // 1. REVERT values to the original password
        passwordField.setText(originalPassword);
        confirmPasswordField.setText(originalPassword);

        // 2. Reset visibility to "Hidden" (Safe Mode)
        // We force the 'dots' to show and the 'plain text' to hide
        passwordTextField.setVisible(false);
        passwordField.setVisible(true);
        confirmPasswordTextField.setVisible(false);
        confirmPasswordField.setVisible(true);

        // 3. Reset Toggle Buttons (Unpress them)
        togglePassBtn.setSelected(false);
        toggleConfirmPassBtn.setSelected(false);

        // 4. Reset Icons to "Slash" (Closed Eye)
        if (eyeClosedImage != null) {
            passEyeIcon.setImage(eyeClosedImage);
            confirmPassEyeIcon.setImage(eyeClosedImage);
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
