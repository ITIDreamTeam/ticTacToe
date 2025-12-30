/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author yasse
 */
public class Player_cardController implements Initializable {

    @FXML
    private ImageView player_card_image;
    @FXML
    private Label player_name;
    @FXML
    private Label win_score;
    @FXML
    private Label lose_score;
    @FXML
    private Label player_state;
    @FXML
    private Button send_request_button;
    @FXML
    private Label player_score;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void onClickSendRequest(ActionEvent event) {
    }
    
}
