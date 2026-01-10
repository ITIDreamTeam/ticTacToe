/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.mycompany.tictactoeclient.data.models.GameSession;
import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.dataSource.GameApi;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.UserSession;
import com.mycompany.tictactoeclient.network.request.InviteRequest;
import com.mycompany.tictactoeclient.presentation.features.game_board.GameSessionManager;
import static com.mycompany.tictactoeclient.presentation.features.home.OnePlayerPopupController.difficulty;
import com.mycompany.tictactoeclient.shared.Navigation;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    @FXML
    private Button acceptButton;
    @FXML
    private Button declineButton;

    private final NetworkClient client = NetworkClient.getInstance();
    private final GameApi gameApi = new GameApi(client);
    private final GameSessionManager gameSession = GameSessionManager.getInstance();
    private final UserSession session = UserSession.getInstance();

    private Stage stage;
    private InviteRequest invite;
    private boolean responded = false;

    private Timeline timeoutTimeline;
    private Timeline progressTimeline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        timeProgressBar.setProgress(1.0);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        setupWindowFollowing();
    }

    public void setInviteData(InviteRequest invite) {
        this.invite = invite;
        playerNameLabel.setText(invite.getSenderUsername());
        recordCheckBox.setSelected(invite.isRecordGame());
        recordCheckBox.setDisable(true);
        statusLabel.setText(invite.getSenderUsername() + " wants to play!");
        
        startTimeout();
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

    private void startTimeout() {
        final double totalSeconds = 30.0;
        
        progressTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), e -> {
                    if (!responded) {
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
                    if (!responded) {
                        handleTimeout();
                    }
                })
        );
        timeoutTimeline.play();
    }

    private void cleanup() {
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
    private void onAcceptClick(ActionEvent event) {
        if (responded) {
            return;
        }
        responded = true;
        
        cleanup();
        disableButtons();
        handleAccept();
    }

    @FXML
    private void onDeclineClick(ActionEvent event) {
        if (responded) {
            return;
        }
        responded = true;
        
        cleanup();
        disableButtons();
        handleDecline();
    }

    @FXML
    private void onClickCheckBox(ActionEvent event) {
        // Checkbox is disabled, but method needed for FXML
    }

    @FXML
    private void onCancelClick(ActionEvent event) {
        // Optional cancel button - just close the popup
        if (responded) {
            return;
        }
        responded = true;
        
        cleanup();
        disableButtons();
        handleDecline();
    }

    private void handleAccept() {
        statusLabel.setText("Accepting invitation...");

        new Thread(() -> {
            try {
                gameApi.acceptInvite(invite.getSenderUsername(), invite.isRecordGame());

                Platform.runLater(() -> {
                    gameSession.setOnlineSession(
                            invite.getSenderUsername(),
                            false,
                            invite.isRecordGame()
                    );
                    
                    closePopup();
                    navigateToGameBoard();
                });

            } catch (Exception e) {
                e.printStackTrace();
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
                System.out.println("Decline sent to server for: " + invite.getSenderUsername());

                Platform.runLater(() -> {
                    closePopup();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    App.showError("Network Error",
                            "Failed to decline invite: " + e.getMessage());
                    closePopup();
                });
            }
        }, "decline-invite-thread").start();
    }

    private void handleTimeout() {
        responded = true;
        cleanup();
        
        Platform.runLater(() -> {
            statusLabel.setText("Invitation expired...");
            disableButtons();
            
            new Thread(() -> {
                try {
                    gameApi.declineInvite(invite.getSenderUsername());
                    System.out.println("Auto-decline sent to server for: " + invite.getSenderUsername());
                } catch (Exception e) {
                    System.err.println("Failed to auto-decline: " + e.getMessage());
                }
                
                Platform.runLater(() -> {
                    closePopup();
                });
            }, "timeout-decline-thread").start();
        });
    }

    private void disableButtons() {
        if (acceptButton != null) {
            acceptButton.setDisable(true);
        }
        if (declineButton != null) {
            declineButton.setDisable(true);
        }
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
