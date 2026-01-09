/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.home;

/**
 *
 * @author Basmala
 */
import com.mycompany.tictactoeclient.presentation.features.game_board.GameSessionManager;
import com.mycompany.tictactoeclient.presentation.features.game_board.Game_boardController;
import com.mycompany.tictactoeclient.shared.Navigation;

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
    Parent root;
    FXMLLoader loader;
    Game_boardController gameController;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/game_board.fxml"));
            root = loader.load();
            gameController = loader.getController();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        player1Field.setText("Player1");
        player2Field.setText("Player2");
        startButton.setOnAction(e -> {
            handleStartButton(e);
        });        
    }

    @FXML
    public void onRecordButton() {
        gameController.changeRecoringIconVisiablitiy(recordButton.isSelected());
    }

    private void handleStartButton(ActionEvent event) {
        String p1 = player1Field.getText().isEmpty() ? "Player 1" : player1Field.getText();
        String p2 = player2Field.getText().isEmpty() ? "Player 2" : player2Field.getText();
        GameSessionManager.getInstance().setGameSession(p1, p2, recordButton.isSelected(), true);
        Game_boardController.setGameMode(Game_boardController.GameMode.twoPlayer);
        stage.close();
        Navigation.navigateTo(Navigation.gameBoardPage);
    }

}
