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
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/game_board.fxml"));
            Parent root = loader.load();
            Game_boardController gameController = loader.getController();

            GameSession.playerX = session.isLoggedIn() ? session.getUsername() : "Player";
            GameSession.playerO = "Computer";
            gameController.setGameMode(Game_boardController.GameMode.vsComputer);
            gameController.setPlayersName(session.isLoggedIn() ? session.getUsername() : "Player", "Computer");
            ToggleButton selected = (ToggleButton) difficultyGroup.getSelectedToggle();
            GameSessionManager.getInstance().setComputerSession(recordButton.isSelected());
            if (selected == easyButton) {
                difficulty = GameEngine.gameDifficulty.Easy;
            } else if (selected == mediumButton) {
                difficulty = GameEngine.gameDifficulty.Medium;
            } else if (selected == hardButton) {
                difficulty = GameEngine.gameDifficulty.Hard;
            }
            System.out.println("Starting One Player Game:");
            System.out.println("Difficulty: " + difficulty);
            stage.close();
            Navigation.navigateTo(Navigation.gameBoardPage);
        } catch (IOException ex) {
            System.getLogger(OnePlayerPopupController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
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
