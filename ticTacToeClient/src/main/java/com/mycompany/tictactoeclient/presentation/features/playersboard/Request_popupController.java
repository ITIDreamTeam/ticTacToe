/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.dataSource.GameApi;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.request.InviteRequest;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Basmala
 */
public class Request_popupController implements Initializable {

    @FXML
    private Label statusLabel;
    @FXML
    private Label playerNameLabel;
    @FXML
    private ProgressBar timeProgressBar;
    @FXML
    private CheckBox recordCheckBox;

    private final NetworkClient client = NetworkClient.getInstance();
    private final GameApi gameApi = new GameApi(client);
    private final UserSession session = UserSession.getInstance();

    private Stage stage;
    private InviteRequest invite = new InviteRequest();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInviteData(InviteRequest invite) {
        this.invite = invite;
        playerNameLabel.setText(invite.getSenderUsername());
        recordCheckBox.setSelected(invite.isRecordGame());
        statusLabel.setText(invite.getSenderUsername() + " wants to play with you!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void onAcceptClick(ActionEvent event) {
        handleAccept();
    }

    @FXML
    private void onDeclineClick(ActionEvent event) {
        handleDecline();
    }

    @FXML
    private void onClickCheckBox(ActionEvent event) {
    }

    private void handleAccept() {
        new Thread(() -> {
            try {
                gameApi.acceptInvite(invite.getSenderUsername(), invite.isRecordGame());

                Platform.runLater(() -> {
                    try {
                        App.showInfo("Game Starting", "Starting game with " + invite.getSenderUsername());
                        closePopup();
                        App.setRoot("game_board");

                    } catch (IOException e) {
                        e.printStackTrace();
                        App.showError("Navigation Error", "Cannot start game.");
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    App.showError("Network Error", "Failed to accept invite: " + e.getMessage());
                });
            }
        }, "accept-invite-thread").start();
    }

    private void handleDecline() {
        new Thread(() -> {
            try {
                gameApi.declineInvite(invite.getSenderUsername());

                Platform.runLater(() -> {
                    App.showInfo("Invitation Declined", "You declined the invitation from " + invite.getSenderUsername());
                    closePopup();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    App.showError("Network Error", "Failed to decline invite: " + e.getMessage());
                    closePopup();
                });
            }
        }, "decline-invite-thread").start();
    }

    private void closePopup() {
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void onCancelClick(ActionEvent event) {
        try {
            if (invite.getSenderUsername() != null) {
                gameApi.declineInvite(invite.getSenderUsername());
            }
            if (stage != null) {
                stage.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
