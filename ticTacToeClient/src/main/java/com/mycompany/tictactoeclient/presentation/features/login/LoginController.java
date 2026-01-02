/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.login;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

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


    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ImageView eyeIcon;
    @FXML
    private TextField passwordTextField;
    
    private boolean passwordVisible = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        passwordTextField.setVisible(false);
        passwordTextField.setManaged(false);
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
            // Show password
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
        try {
            UserSession.getInstance().login("Basmala");
            App.setRoot("home");
        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }

    @FXML
    private void onSignUpClicked(ActionEvent event) {
        try {
            App.setRoot("register");
        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }

}

