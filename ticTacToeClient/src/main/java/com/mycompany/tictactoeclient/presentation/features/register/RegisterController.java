/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.register;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

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
        
    private Image eyeOpenImage;
    private Image eyeClosedImage;
    
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
    }    
    
    @FXML
    private void onBackClicked(ActionEvent event) {
    }

    @FXML
    private void toggalePassword(ActionEvent event) {
        toggleVisibility(
                togglePassButton,
                passwordField,
                passwordTextField,
                eyeIconPassword
        );
    }


    @FXML
    private void toggaleConfirmPassword(ActionEvent event) {
        toggleVisibility(
                toggleConfirmPassButton,
                confirmPasswordField,
                confirmPasswordTextField,
                eyeIconConfirmPass
        );
    }

    
    @FXML
    private void onSignUpClicked(ActionEvent event) {
        String password = passwordField.isVisible()
            ? passwordField.getText()
            : passwordTextField.getText();

        String confirmPassword = confirmPasswordField.isVisible()
                ? confirmPasswordField.getText()
                : confirmPasswordTextField.getText();

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match!");
            return;
        }

        System.out.println("Register Success");
    }

    @FXML
    private void onSignInClicked(ActionEvent event) {
    }

    private void toggleVisibility(
        ToggleButton toggleButton,
        PasswordField passwordField,
        TextField textField,
        ImageView eyeIcon
        ) {
            System.out.println("before toggale burron selcted");
            if (toggleButton.isSelected()) {
                System.out.println("toggale burron selcted");
                System.out.println("Register Success");
                // SHOW password
                textField.setText(passwordField.getText());
                textField.setVisible(true);
                textField.setManaged(true);

                passwordField.setVisible(false);
                passwordField.setManaged(false);

                eyeIcon.setImage(eyeOpenImage);
            } else {
                // HIDE password
                passwordField.setText(textField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);

                textField.setVisible(false);
                textField.setManaged(false);

                eyeIcon.setImage(eyeClosedImage);
            }
        }


}
