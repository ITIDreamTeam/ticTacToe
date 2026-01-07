/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.home;

/**
 *
 * @author Basmala
 */
import com.mycompany.tictactoeclient.core.RecordingSettings;
import com.mycompany.tictactoeclient.presentation.features.game_board.Game_boardController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TwoPlayerPopupController implements Initializable {

    @FXML
    private TextField player1Field;

    @FXML
    private TextField player2Field;

    @FXML
    private CheckBox recordButton;

    @FXML
    private Button startButton;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        player1Field.setText("Player1");
        player2Field.setText("Player2");
        startButton.setOnAction(e -> {
            handleStartButton(e);
        });
        recordButton.selectedProperty().bindBidirectional(
                RecordingSettings.recordingEnabledProperty()
        );
    }

    @FXML
    public void onRecordButton() {

    }

    private void handleStartButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/game_board.fxml"));
            Parent root = loader.load();
            Game_boardController gameController = loader.getController();
            String p1 = player1Field.getText().isEmpty() ? "Player 1" : player1Field.getText();
            String p2 = player2Field.getText().isEmpty() ? "Player 2" : player2Field.getText();
            gameController.setPlayersName(p1, p2);
            gameController.setGameMode(false);
            
            Stage mainStage = (Stage) this.stage.getOwner();
            Scene scene = new Scene(root);
            mainStage.setScene(scene);
            mainStage.show();
            this.stage.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showRecords() {
        System.out.println("Showing Two Player Records");
    }
}
