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
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.presentation.features.game_board.Game_boardController;
import com.mycompany.tictactoeclient.presentation.features.game_board.GameEngine;
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
    Parent root;
    FXMLLoader loader;
    Game_boardController gameController;
    private final UserSession session = UserSession.getInstance();
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        easyButton.setStyle("-fx-background-color: #4E0585; -fx-text-fill: white;");
        startButton.setOnAction(e -> handleStartButton(e));
        try {
            loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/game_board.fxml"));
            root = loader.load();
            gameController = loader.getController();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
     
    private void handleStartButton(javafx.event.ActionEvent event) { 
        Game_boardController.setGameMode(Game_boardController.GameMode.vsComputer);
        gameController.setPlayersName(session.isLoggedIn()?session.getUsername():"Player", "Computer");
        ToggleButton selected = (ToggleButton) difficultyGroup.getSelectedToggle();
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

    private String getSelectedDifficulty() {
        if (difficultyGroup.getSelectedToggle() == mediumButton) {
            return "medium";
        }
        if (difficultyGroup.getSelectedToggle() == hardButton) {
            return "hard";
        }
        return "easy";
    }

    @FXML
    public void onRecordButton() {
        gameController.changeRecoringIconVisiablitiy(recordButton.isSelected());
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
