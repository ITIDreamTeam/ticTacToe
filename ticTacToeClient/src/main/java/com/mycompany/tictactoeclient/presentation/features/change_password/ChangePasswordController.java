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
    private String originalPassword = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        try {
            eyeOpenImage = new Image(getClass().getResource("/icons/visibility-on_1.png").toExternalForm());
            eyeClosedImage = new Image(getClass().getResource("/icons/Visibility-off-02.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
        }
        originalPassword = "12345678";
        passwordField.setText(originalPassword);
        confirmPasswordField.setText(originalPassword);
        setupInitialState(passwordTextField, passwordField, passEyeIcon);
        setupInitialState(confirmPasswordTextField, confirmPasswordField, confirmPassEyeIcon);
    }

    private void setupInitialState(TextField tf, PasswordField pf, ImageView icon) {
        tf.setVisible(false);
        pf.setVisible(true);
        if (eyeClosedImage != null) {
            icon.setImage(eyeClosedImage);
        }
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
        } else {
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
        passwordTextField.setVisible(false);
        passwordField.setVisible(true);
        confirmPasswordTextField.setVisible(false);
        confirmPasswordField.setVisible(true);
        togglePassBtn.setSelected(false);
        toggleConfirmPassBtn.setSelected(false);
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
