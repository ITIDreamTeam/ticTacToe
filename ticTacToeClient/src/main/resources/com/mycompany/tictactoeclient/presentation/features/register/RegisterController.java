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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
/**
 * FXML Controller class
 *
 * @author ITI
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
    private ImageView eyeIcon;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField confirmPasswordTextField;
    @FXML
    private ImageView eyeIcon1;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void onBackClicked(ActionEvent event) {
    }

    @FXML
    private void toggalePassword(MouseEvent event) {
    }

    @FXML
    private void onSignUpClicked(ActionEvent event) {
    }

    @FXML
    private void onSignInClicked(ActionEvent event) {
    }

}
