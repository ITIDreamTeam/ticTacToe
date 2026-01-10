/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.core.RecordingSettings;
import com.mycompany.tictactoeclient.data.dataSource.GameApi;
import com.mycompany.tictactoeclient.data.models.Player;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.UserSession;
import com.mycompany.tictactoeclient.network.response.InviteResponse;
import com.mycompany.tictactoeclient.presentation.features.game_board.GameSessionManager;
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

    private final NetworkClient client = NetworkClient.getInstance();
    private final GameApi gameApi = new GameApi(client);
    private final UserSession session = UserSession.getInstance();

    private Timeline delayTimeline;
    private Timeline timeoutTimeline;
    private Timeline progressTimeline;

    private Consumer<NetworkMessage> acceptListener;
    private Consumer<NetworkMessage> declineListener;

    private boolean responseReceived = false;
    private boolean inviteSent = false;
    private boolean cancelled = false;
    @FXML
    private Button cancel_button;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
                client.send(new NetworkMessage(
                    MessageType.UPDATE_STATUS,
                    UserSession.getInstance().getUsername(),
                    "Server",
                    client.getGson().toJsonTree("WAITING") // Sets status back to 3
                ));
            } catch (Exception e) {
                System.err.println("Failed to revert status to WAITING");
            }
    
        setupListeners();
        recordCheckBox.selectedProperty().bindBidirectional(
                RecordingSettings.recordingEnabledProperty()
        );
        cancel_button.setVisible(false);
        cancel_button.setManaged(false);
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
            delayTimeline = null;
        }
        if (timeoutTimeline != null) {
            timeoutTimeline.stop();
            timeoutTimeline = null;
        }
        if (progressTimeline != null) {
            progressTimeline.stop();
            progressTimeline = null;
        }
    }

    @FXML
    private void onCancelClick(ActionEvent event) {
        if (responseReceived || cancelled) {
            return;
        }
        
        cancelled = true;
        responseReceived = true;
        
        cleanup();
        
        if (inviteSent) {
            sendCancelNotification();
        } else {
            closePopup();
        }
    }

    private void sendCancelNotification() {
        statusLabel.setText("Cancelling invitation...");
        
        new Thread(() -> {
            try {
                gameApi.declineInvite(opponent.getName());
                System.out.println("Cancel notification sent for invite to: " + opponent.getName());
                
                Platform.runLater(() -> {
                    closePopup();
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    closePopup();
                });
            }
        }, "cancel-invite-thread").start();
    }

    public void setDisplayData(Player player, Stage stage) {
        this.opponent = player;
        this.stage = stage;
        playerNameLabel.setText(player.getName());
        
        setupWindowFollowing();
        startBufferPhase();
    }

    private void setupWindowFollowing() {
        if (stage == null || stage.getOwner() == null) {
            return;
        }

        Stage ownerStage = (Stage) stage.getOwner();
        
        javafx.beans.value.ChangeListener<Number> positionListener = (obs, oldVal, newVal) -> {
            if (stage.isShowing() && !Double.isNaN(stage.getWidth()) && !Double.isNaN(stage.getHeight())) {
                double x = ownerStage.getX() + (ownerStage.getWidth() - stage.getWidth()) / 2;
                double y = ownerStage.getY() + (ownerStage.getHeight() - stage.getHeight()) / 2;
                stage.setX(x);
                stage.setY(y);
            }
        };

        ownerStage.xProperty().addListener(positionListener);
        ownerStage.yProperty().addListener(positionListener);
        ownerStage.widthProperty().addListener(positionListener);
        ownerStage.heightProperty().addListener(positionListener);

        stage.setOnHidden(e -> {
            ownerStage.xProperty().removeListener(positionListener);
            ownerStage.yProperty().removeListener(positionListener);
            ownerStage.widthProperty().removeListener(positionListener);
            ownerStage.heightProperty().removeListener(positionListener);
        });

        stage.setOnShown(e -> positionListener.changed(null, null, null));
    }

    private void startBufferPhase() {
        statusLabel.setText("Preparing to send invite...");
        timeProgressBar.setProgress(1.0);
        
        delayTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> sendInviteRequest())
        );
        delayTimeline.play();
    }

    private void sendInviteRequest() {
        if (inviteSent || responseReceived || cancelled) {
            return;
        }

        inviteSent = true;
        boolean recordGame = recordCheckBox.isSelected();

        new Thread(() -> {
            try {
                gameApi.sendGameInvite(opponent.getName(), recordGame);

                Platform.runLater(() -> {
                    if (!responseReceived && !cancelled) {
                        statusLabel.setText("Waiting for " + opponent.getName() + "'s response...");
                        recordCheckBox.setDisable(true);
                        startTimeoutPhase();
                    }
                });

                System.out.println("Invite sent to " + opponent.getName() + " | Record: " + recordGame);

            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (!responseReceived && !cancelled) {
                        responseReceived = true;
                        App.showError("Network Error", "Failed to send invite: " + e.getMessage());
                        cleanup();
                        closePopup();
                    }
                });
            }
        }, "send-invite-thread").start();
    }

    private void startTimeoutPhase() {
        final double totalSeconds = 30.0;
        
        progressTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), e -> {
                    if (!responseReceived && !cancelled) {
                        double current = timeProgressBar.getProgress();
                        double decrement = 0.1 / totalSeconds;
                        timeProgressBar.setProgress(Math.max(0, current - decrement));
                    }
                })
        );
        progressTimeline.setCycleCount((int) (totalSeconds * 10));
        progressTimeline.play();

        timeoutTimeline = new Timeline(
                new KeyFrame(Duration.seconds(totalSeconds), e -> {
                    if (!responseReceived && !cancelled) {
                        handleTimeout();
                    }
                })
        );
        timeoutTimeline.play();
    }

    private void handleAcceptResponse(NetworkMessage msg) {
        if (responseReceived || cancelled) {
            return;
        }
        if (!msg.getUsername().equalsIgnoreCase(opponent.getName())) {
            return;
        }
        
        responseReceived = true;
        cleanup();

        Platform.runLater(() -> {
            try {
                InviteResponse response = client.getGson().fromJson(msg.getPayload(), InviteResponse.class);

                GameSessionManager.getInstance().setOnlineSession(
                        opponent.getName(),
                        true,
                        response.isRecordGame()
                );
                
                App.showInfo("Invitation Accepted",
                        opponent.getName() + " accepted your invitation!");

                closePopup();
                
                App.setRoot("game_board");
                
            } catch (IOException e) {
                e.printStackTrace();
                App.showError("Navigation Error", "Cannot start game.");
            }
        });
    }

    private void handleDeclineResponse(NetworkMessage msg) {
        if (responseReceived || cancelled) {
            return;
        }
        if (!msg.getUsername().equalsIgnoreCase(opponent.getName())) {
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
        responseReceived = true;
        cleanup();
        
        Platform.runLater(() -> {
            App.showWarning("No Response", opponent.getName() + " did not respond to your invitation.");
            closePopup();
        });
    }

    private void closePopup() {
        if (stage != null) {
            stage.close();
        }
    }
}
