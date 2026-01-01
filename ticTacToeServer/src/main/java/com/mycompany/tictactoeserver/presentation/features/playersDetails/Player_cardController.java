/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeserver.presentation.features.playersDetails;

import com.mycompany.tictactoeserver.data.model.Player;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    private Label player_score;
    private Player player; 

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onClickSendRequest(ActionEvent event) {
        try {
        // 1. Load the Popup FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/invite_popup.fxml"));
        Parent root = loader.load();
        
        // Create the Stage
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with main window
        popupStage.initStyle(StageStyle.TRANSPARENT); // Removes default OS window frame
        popupStage.setScene(new Scene(root));
        
        popupStage.getScene().setFill(Color.TRANSPARENT);

        // 3. Show it
        popupStage.showAndWait();

    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    public void setPlayerData(Player player) {
        // --- 2. SAVE THE DATA HERE ---
        this.player = player; 
        // We take the 'player' coming from the outside and save it into our private field.

        player_name.setText(player.getName());
        player_score.setText("" + player.getScore());

        switch (player.getStatus()) {
            case ONLINE:
                player_state.setStyle("-fx-text-fill: #2ecc71;");
                player_state.setText("ONLINE");
                break;
            case OFFLINE:
                player_state.setStyle("-fx-text-fill: #95a5a6;");
                player_state.setText("OFFLINE");
                break;
            case IN_GAME:
                player_state.setStyle("-fx-text-fill: #f1c40f;");
                player_state.setText("IN GAME");
                break;
        }
    }
}
