/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeserver.presentation.features.playersDetails;

import com.mycompany.tictactoeserver.data.model.Player;
import static com.mycompany.tictactoeserver.data.model.Player.PlayerState.IN_GAME;
import com.mycompany.tictactoeserver.data.model.PlayerStatsDto;
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
    private PlayerStatsDto player; 

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
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.setScene(new Scene(root));
        
        popupStage.getScene().setFill(Color.TRANSPARENT);

        // 3. Show it
        popupStage.showAndWait();

    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    public void setPlayerData(PlayerStatsDto player) {
        this.player = player; 

        player_name.setText(player.getPlayer().getName());
        player_score.setText("" + player.getPlayer().getScore());
        win_score.setText(Integer.toString(player.getWins()));
        lose_score.setText(Integer.toString(player.getLosses()));
        switch (player.getPlayer().getPlayerState()) {
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
            case WAITING:
                player_state.setStyle("-fx-text-fill: #f1c40f;");
                player_state.setText("WAITING");
                break;
        }
    }
}
