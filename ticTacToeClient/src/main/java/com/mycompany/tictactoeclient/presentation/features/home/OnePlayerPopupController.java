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
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class OnePlayerPopupController implements Initializable{

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
    }
    @FXML
    public void onRecordButton(){
    }

    @FXML
    public void onEasyButton(){
        easyButton.setStyle("-fx-background-color: #4E0585;");
        mediumButton.setStyle("-fx-background-color: black;");
        hardButton.setStyle("-fx-background-color: black;");
    }
    @FXML
    public void onMediumButton(){
        easyButton.setStyle("-fx-background-color: black;");
        mediumButton.setStyle("-fx-background-color: #4E0585;");
        hardButton.setStyle("-fx-background-color: black;");
    }
    @FXML
    public void onHardButton(){
        easyButton.setStyle("-fx-background-color: black;");
        mediumButton.setStyle("-fx-background-color: black;");
        hardButton.setStyle("-fx-background-color: #4E0585;");

    }

    private void startGame() {
        RadioButton selected = (RadioButton) difficultyGroup.getSelectedToggle();
        String difficulty = selected.getUserData().toString();
        
        System.out.println("Starting One Player Game:");
        System.out.println("Difficulty: " + difficulty);

        // Close the popup
        stage.close();
        
        // TODO: Start your actual game here
    }

    private void showRecords() {
        System.out.println("Showing One Player Records");
        // TODO: Implement record display
    }
}
