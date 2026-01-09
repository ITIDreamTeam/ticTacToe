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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
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
    }
     
    private void handleStartButton(javafx.event.ActionEvent event) { 
        GameSession.playerX = session.isLoggedIn() ? session.getUsername() : "Player";
        GameSession.playerO = "Computer";
        Game_boardController.setGameMode(Game_boardController.GameMode.vsComputer);
        ToggleButton selected = (ToggleButton) difficultyGroup.getSelectedToggle();
        GameSessionManager.getInstance().setGameSession("computer", recordButton.isSelected(), true);
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

    }

    @FXML
    public void onRecordButton() {
        GameSession.recordingEnabled = recordButton.isSelected();
    }

    @FXML
    public void onEasyButton() {
        easyButton.setStyle("-fx-background-color: #4E0585;");
        mediumButton.setStyle("-fx-background-color: black;");
        hardButton.setStyle("-fx-background-color: black;");
    }

    @FXML
    public void onMediumButton() {
        easyButton.setStyle("-fx-background-color: black;");
        mediumButton.setStyle("-fx-background-color: #4E0585;");
        hardButton.setStyle("-fx-background-color: black;");
    }

    @FXML
    public void onHardButton() {
        easyButton.setStyle("-fx-background-color: black;");
        mediumButton.setStyle("-fx-background-color: black;");
        hardButton.setStyle("-fx-background-color: #4E0585;");

    }
}
