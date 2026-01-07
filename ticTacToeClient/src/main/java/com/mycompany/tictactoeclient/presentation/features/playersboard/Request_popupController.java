/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.presentation.features.game_board.GameEngine;
import com.mycompany.tictactoeclient.presentation.features.game_board.Game_boardController;
import static com.mycompany.tictactoeclient.presentation.features.home.OnePlayerPopupController.difficulty;
import com.mycompany.tictactoeclient.shared.Navigation;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
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
    Parent root;
    FXMLLoader loader;
    Game_boardController gameController;
    private final UserSession session = UserSession.getInstance();
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        gameController.setPlayersName(session.getUsername(),"invited name");
       
        System.out.println("Starting One Player Game:");
        System.out.println("Difficulty: " + difficulty);
        stage.close();
        Navigation.navigateTo(Navigation.gameBoardPage);

    }
    
    @FXML
    private void onAcceptClick(ActionEvent event) {
        handleStartButton(event);
    }

    @FXML
    private void onCancelClick(ActionEvent event) {
    }
    
    @FXML
    private void onClickCheckBox(ActionEvent event) {
        gameController.changeRecoringIconVisiablitiy(recordCheckBox.isSelected());
    }

}
