/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.dataSource.GameApi;
import com.mycompany.tictactoeclient.data.models.Player;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.response.InviteResponse;
import com.mycompany.tictactoeclient.presentation.features.game_board.Game_boardController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

    private final NetworkClient client = NetworkClient.getInstance();
    private final GameApi gameApi = new GameApi(client);
    private final UserSession session = UserSession.getInstance();

    private Timeline delayTimeline;
    private Timeline timeoutTimeline;

    private Consumer<NetworkMessage> acceptListener;
    private Consumer<NetworkMessage> declineListener;

    private boolean responseReceived = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupListeners();
    }

    private void setupListeners() {
        acceptListener = this::handleAcceptResponse;
        declineListener = this::handleDeclineResponse;

        client.on(MessageType.ACCEPT_REQUEST, acceptListener);
        client.on(MessageType.DECLINE_REQUEST, declineListener);
    }

    public void cleanup() {
        client.off(MessageType.ACCEPT_REQUEST, acceptListener);
        client.off(MessageType.DECLINE_REQUEST, declineListener);

        if (delayTimeline != null) {
            delayTimeline.stop();
        }
        if (timeoutTimeline != null) {
            timeoutTimeline.stop();
        }
    }

    @FXML
    private void onClickCheckBox(ActionEvent event) {
    }

    @FXML
    private void onCancelClick(ActionEvent event) {
        cleanup();
        closePopup();
    }

    public void setDisplayData(Player player, Stage stage) {
        this.opponent = player;
        this.stage = stage;
        playerNameLabel.setText(player.getName());
        startBufferPhase();
    }

    private void startBufferPhase() {
        statusLabel.setText("Preparing to send invite...");
        timeProgressBar.setProgress(1.0);
        delayTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> sendInviteRequest())
        );
        delayTimeline.play();
    }

    private void sendInviteRequest() {
        boolean recordGame = recordCheckBox.isSelected();

        new Thread(() -> {
            try {
                gameApi.sendGameInvite(opponent.getName(), recordGame);

                Platform.runLater(() -> {
                    statusLabel.setText("Waiting for " + opponent.getName() + "'s response...");
                    recordCheckBox.setDisable(true);
                    startTimeoutPhase();
                });

                System.out.println("Invite sent to " + opponent.getName() + " | Record: " + recordGame);

            } catch (Exception e) {
                Platform.runLater(() -> {
                    App.showError("Network Error", "Failed to send invite: " + e.getMessage());
                    cleanup();
                    closePopup();
                });
            }
        }, "send-invite-thread").start();
    }

    private void startTimeoutPhase() {
        timeoutTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> {
                    if (!responseReceived) {
                        handleTimeout();
                    }
                })
        );
        timeoutTimeline.play();
    }

    private void handleAcceptResponse(NetworkMessage msg) {
        if (responseReceived) {
            return;
        }

        InviteResponse response = client.getGson().fromJson(msg.getPayload(), InviteResponse.class);
        if (!response.getSenderUsername().equalsIgnoreCase(opponent.getName())) {
            return;
        }

        responseReceived = true;
        cleanup();

        Platform.runLater(() -> {
            try {
                App.showInfo("Invitation Accepted", opponent.getName() + " accepted your invitation!");
                App.setRoot("game_board");
                closePopup();
            } catch (IOException e) {
                e.printStackTrace();
                App.showError("Navigation Error", "Cannot start game.");
            }
        });
    }

    private void handleDeclineResponse(NetworkMessage msg) {
        if (responseReceived) {
            return;
        }
        InviteResponse response = client.getGson().fromJson(msg.getPayload(), InviteResponse.class);
        if (!response.getSenderUsername().equalsIgnoreCase(opponent.getName())) {
            return;
        }
        responseReceived = true;
        cleanup();
        Platform.runLater(() -> {
            App.showWarning("Invitation Declined", opponent.getName() + " declined your invitation.");
            closePopup();
        });
    }

    private void handleTimeout() {
        cleanup();
        App.showWarning("No Response", opponent.getName() + " did not respond to your invitation.");
        closePopup();
    }

    private void closePopup() {
        if (stage != null) {
            stage.close();
        }
    }
}
