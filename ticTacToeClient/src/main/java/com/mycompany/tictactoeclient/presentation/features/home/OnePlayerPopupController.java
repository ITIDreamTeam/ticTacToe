/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.home;

/**
 * FXML Controller class
 *
 * @author Basmala
 */
import com.mycompany.tictactoeclient.core.RecordingSettings;
import com.mycompany.tictactoeclient.data.models.GameSession;
import com.mycompany.tictactoeclient.network.UserSession;
import com.mycompany.tictactoeclient.presentation.features.game_board.Game_boardController;
import com.mycompany.tictactoeclient.presentation.features.game_board.GameEngine;
import com.mycompany.tictactoeclient.presentation.features.game_board.GameSessionManager;
import com.mycompany.tictactoeclient.shared.Navigation;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class OnePlayerPopupController implements Initializable {

    @FXML
    private ToggleGroup difficultyGroup;
    @FXML
    private CheckBox recordButton;
    @FXML
    private Button startButton;
    @FXML
    private ToggleButton easyButton;
    @FXML
    private ToggleButton mediumButton;
    @FXML
    private ToggleButton hardButton;

    public static GameEngine.gameDifficulty difficulty = GameEngine.gameDifficulty.Easy;
    private final UserSession session = UserSession.getInstance();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        easyButton.setStyle("-fx-background-color: #4E0585; -fx-text-fill: white;");
        startButton.setOnAction(e -> handleStartButton(e));

        recordButton.selectedProperty().bindBidirectional(
                RecordingSettings.recordingEnabledProperty()
        );
    }

    private void handleStartButton(javafx.event.ActionEvent event) {
        ToggleButton selected = (ToggleButton) difficultyGroup.getSelectedToggle();
        if (selected == easyButton) {
            difficulty = GameEngine.gameDifficulty.Easy;
        } else if (selected == mediumButton) {
            difficulty = GameEngine.gameDifficulty.Medium;
        } else if (selected == hardButton) {
            difficulty = GameEngine.gameDifficulty.Hard;
        }
        GameSessionManager.getInstance().setComputerSession();
        stage.close();
        Navigation.navigateTo(Navigation.gameBoardPage);
    }

    @FXML
    public void onEasyButton() {
        updateStyles(easyButton, mediumButton, hardButton);
    }

    @FXML
    public void onMediumButton() {
        updateStyles(mediumButton, easyButton, hardButton);
    }

    @FXML
    public void onHardButton() {
        updateStyles(hardButton, easyButton, mediumButton);
    }

    private void updateStyles(ToggleButton active, ToggleButton inactive1, ToggleButton inactive2) {
        active.setStyle("-fx-background-color: #4E0585; -fx-text-fill: white;");
        inactive1.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        inactive2.setStyle("-fx-background-color: black; -fx-text-fill: white;");
    }
}
