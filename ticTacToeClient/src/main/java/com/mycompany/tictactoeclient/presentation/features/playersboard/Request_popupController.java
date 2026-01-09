/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.dataSource.GameApi;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.request.InviteRequest;
import com.mycompany.tictactoeclient.presentation.features.game_board.GameSessionManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

     @FXML private Label statusLabel;
    @FXML private Label playerNameLabel;
    @FXML private ProgressBar timeProgressBar;
    @FXML private CheckBox recordCheckBox;
    @FXML private Button acceptButton;
    @FXML private Button declineButton;

    private final NetworkClient client = NetworkClient.getInstance();
    private final GameApi gameApi = new GameApi(client);
    private final GameSessionManager gameSession = GameSessionManager.getInstance();

    private Stage stage;
    private InviteRequest invite;
    private boolean responded = false;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInviteData(InviteRequest invite) {
        this.invite = invite;
        playerNameLabel.setText(invite.getSenderUsername());
        recordCheckBox.setSelected(invite.isRecordGame());
        recordCheckBox.setDisable(true); 
        statusLabel.setText(invite.getSenderUsername() + " wants to play!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization if needed
    }

    @FXML
    private void onAcceptClick(ActionEvent event) {
        if (responded) return;
        responded = true;
        
        disableButtons();
        handleAccept();
    }

    @FXML
    private void onDeclineClick(ActionEvent event) {
        if (responded) return;
        responded = true;
        
        disableButtons();
        handleDecline();
    }

    @FXML
    private void onCancelClick(ActionEvent event) {
        if (responded) return;
        responded = true;
        
        handleDecline();
    }

    @FXML
    private void onClickCheckBox(ActionEvent event) {
        // Checkbox is disabled
    }

    private void handleAccept() {
        statusLabel.setText("Accepting invitation...");
        
        new Thread(() -> {
            try {
                gameApi.acceptInvite(invite.getSenderUsername(), invite.isRecordGame());
                
                Platform.runLater(() -> {
                    gameSession.setGameSession(
                        invite.getSenderUsername(), 
                        invite.isRecordGame(), 
                        false
                    );
                    
                    App.showInfo("Game Starting", 
                        "Starting game with " + invite.getSenderUsername());
                    
                    closePopup();
                    navigateToGameBoard();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    App.showError("Network Error", 
                        "Failed to accept invite: " + e.getMessage());
                    closePopup();
                });
            }
        }, "accept-invite-thread").start();
    }

    private void handleDecline() {
        statusLabel.setText("Declining invitation...");
        
        new Thread(() -> {
            try {
                gameApi.declineInvite(invite.getSenderUsername());
                
                Platform.runLater(() -> {
                    App.showInfo("Invitation Declined", 
                        "You declined the invitation from " + invite.getSenderUsername());
                    closePopup();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    App.showError("Network Error", 
                        "Failed to decline invite: " + e.getMessage());
                    closePopup();
                });
            }
        }, "decline-invite-thread").start();
    }
    
    private void disableButtons() {
        if (acceptButton != null) acceptButton.setDisable(true);
        if (declineButton != null) declineButton.setDisable(true);
    }

    private void navigateToGameBoard() {
        try {
            App.setRoot("game_board");
        } catch (IOException e) {
            e.printStackTrace();
            App.showError("Navigation Error", "Cannot start game.");
        }
    }

    private void closePopup() {
        if (stage != null) {
            stage.close();
        }
    }
}
