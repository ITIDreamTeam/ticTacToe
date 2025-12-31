/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.home;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Basmala
 */
public class HomeController implements Initializable {

    @FXML
    private Label Login;
    @FXML
    private Label register;
    @FXML
    private Button onePlayerButton;
    @FXML
    private Button twoPlayerButton;
    @FXML
    private Button withAFriendButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void onOnePlayerButton(ActionEvent event) {
    }

    @FXML
    private void onTwoPlayerButton(ActionEvent event) {
    }

    @FXML
    private void onWithAFriendButton(ActionEvent event) {
    }
    
}
