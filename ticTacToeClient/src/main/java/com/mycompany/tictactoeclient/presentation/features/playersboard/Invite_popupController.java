/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.models.Player;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.presentation.features.game_board.Game_boardController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author yasse
 */
public class Invite_popupController implements Initializable {

    @FXML
    private Label playerNameLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar timeProgressBar;
    @FXML
    private CheckBox recordCheckBox;

    private Stage stage;
    private Player opponent;

    // Timelines
    private Timeline delayTimeline;
    private Timeline timeoutTimeline;
    Parent root;
    FXMLLoader loader;
    Game_boardController gameController;
    private final UserSession session = UserSession.getInstance();
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onClickCheckBox(ActionEvent event) {
        gameController.changeRecoringIconVisiablitiy(recordCheckBox.isSelected());
    }

    @FXML
    private void onCancelClick(ActionEvent event) {
        if (delayTimeline != null) {
            delayTimeline.stop();
        }
        if (timeoutTimeline != null) {
            timeoutTimeline.stop();
        }
        System.out.println("Invitation Cancelled");
        closePopup();
    }
    public void setDisplayData(Player player, Stage stage,ActionEvent event) {
        this.opponent = player;
        this.stage = stage;
        playerNameLabel.setText(player.getName());

        startBufferPhase( event);
    }

    private void startBufferPhase(ActionEvent event) {
        statusLabel.setText("Sending request in...");
        timeProgressBar.setProgress(1.0);
        delayTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> sendNetworkRequest(event))
        );
        delayTimeline.play();
    }

    private void sendNetworkRequest(ActionEvent event) {
        boolean isRecording = recordCheckBox.isSelected();
        System.out.println("Request Sent to " + opponent.getName() + " | Record: " + isRecording);
        statusLabel.setText("Waiting for response...");
        recordCheckBox.setDisable(true); 
        startTimeoutPhase(event);
    }

    private void startTimeoutPhase(ActionEvent event) {
        timeoutTimeline = new Timeline(
                new KeyFrame(Duration.seconds(10), e -> handleTimeout())
        );
        timeoutTimeline.play();
        try {
            App.setRoot("game_board");
            closePopup();
            System.out.println("Go to home");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handleTimeout() {
        System.out.println("No response from " + opponent.getName());
        closePopup();
    }

    private void closePopup() {
        if (stage != null) {
            stage.close();
        }
    }
}
