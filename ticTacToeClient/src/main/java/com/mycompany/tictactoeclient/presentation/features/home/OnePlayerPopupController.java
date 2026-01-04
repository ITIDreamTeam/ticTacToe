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
import com.mycompany.tictactoeclient.presentation.features.game_board.Game_boardController;
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

    private RadioButton easyRadio;

    @FXML
    private ToggleGroup difficultyGroup;

    @FXML
    private CheckBox recordButton;

    @FXML
    private Button startButton;

    private Stage stage;
    @FXML
    private ToggleButton easyButton;
    @FXML
    private ToggleButton mediumButton;
    @FXML
    private ToggleButton hardButton;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        easyButton.setStyle("-fx-background-color: #4E0585; -fx-text-fill: white;");

        // Move logic to a proper handler method for cleanliness
        startButton.setOnAction(e -> handleStartButton(e));
    }

    private void handleStartButton(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/game_board.fxml"));
            Parent root = loader.load();
            Game_boardController gameController = loader.getController();
            gameController.setGameMode(true);

            gameController.setPlayersName("Player", "Computer");
            Stage popupStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage mainStage = (Stage) popupStage.getOwner();

            mainStage.setScene(new Scene(root));
            mainStage.show();
            popupStage.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

    private void startGame() {
        ToggleButton selected = (ToggleButton) difficultyGroup.getSelectedToggle();
        String difficulty = "";

        if (selected == easyButton) {
            difficulty = "easy";
        } else if (selected == mediumButton) {
            difficulty = "medium";
        } else if (selected == hardButton) {
            difficulty = "hard";
        }

        System.out.println("Starting One Player Game:");
        System.out.println("Difficulty: " + difficulty);

        stage.close();
    }

    private void showRecords() {
        System.out.println("Showing One Player Records");
    }
}
