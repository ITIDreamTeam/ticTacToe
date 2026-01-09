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
import com.mycompany.tictactoeclient.shared.Navigation;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TwoPlayerPopupController implements Initializable {

  @FXML private TextField player1Field;
    @FXML private TextField player2Field;
    @FXML private CheckBox recordButton;
    @FXML private Button startButton;
    
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        player1Field.setText("Player 1");
        player2Field.setText("Player 2");
        startButton.setOnAction(this::handleStartButton);        
    }

    @FXML public void onRecordButton() {} 

    private void handleStartButton(ActionEvent event) {
        String p1 = player1Field.getText().trim().isEmpty() ? "Player 1" : player1Field.getText();
        String p2 = player2Field.getText().trim().isEmpty() ? "Player 2" : player2Field.getText();
        GameSessionManager.getInstance().setLocalPvpSession(p1, p2, recordButton.isSelected());
        stage.close();
        Navigation.navigateTo(Navigation.gameBoardPage);
    }

}
