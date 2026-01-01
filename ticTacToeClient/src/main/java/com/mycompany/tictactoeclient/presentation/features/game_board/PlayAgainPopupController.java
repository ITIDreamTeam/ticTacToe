/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.game_board;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author yasse
 */
public class PlayAgainPopupController implements Initializable {

    @FXML private Label winnerLabel;
    
    // Simple interface to communicate back to the main controller
    private Runnable onPlayAgain;
    private Runnable onBack;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Init logic if needed
    }

    public void setWinnerName(String text) {
        winnerLabel.setText(text);
    }

    public void setOnPlayAgain(Runnable handler) {
        this.onPlayAgain = handler;
    }

    public void setOnBack(Runnable handler) {
        this.onBack = handler;
    }

    @FXML
    private void onPlayAgainClicked() {
        if (onPlayAgain != null) onPlayAgain.run();
        closeWindow();
    }

    @FXML
    private void onBackClicked() {
        if (onBack != null) onBack.run();
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) winnerLabel.getScene().getWindow();
        stage.close();
    }
}
