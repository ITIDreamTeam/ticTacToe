/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.dataSource.GameApi;
import com.mycompany.tictactoeclient.data.models.Player;
import com.mycompany.tictactoeclient.network.NetworkClient;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private Button send_request_button;
    @FXML
    private Label player_score;
    private Player player;
    GameApi gameApi = new GameApi(NetworkClient.getInstance());

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
            gameApi.sendGameInvite(player.getName(), false);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/invite_popup.fxml"));
            Parent root = loader.load();
            Invite_popupController popupController = loader.getController();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.setScene(new Scene(root));
            popupStage.getScene().setFill(Color.TRANSPARENT);
            Stage ownerStage = (Stage) send_request_button.getScene().getWindow();
            popupStage.initOwner(ownerStage);
            popupStage.setOnShown(e -> {
                double x = ownerStage.getX() + (ownerStage.getWidth() - popupStage.getWidth()) / 2;
                double y = ownerStage.getY() + (ownerStage.getHeight() - popupStage.getHeight()) / 2;
                popupStage.setX(x);
                popupStage.setY(y);
            });
            popupController.setDisplayData(this.player, popupStage);
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.getLogger(Player_cardController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public void setPlayerData(Player player) {
        this.player = player;

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
            case WAITING:
                player_state.setStyle("-fx-text-fill: #f1c40f;");
                player_state.setText("WAITING");
        }
    }
}
