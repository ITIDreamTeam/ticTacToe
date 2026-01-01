/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.home;

/**
 *
 * @author Basmala
 */
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
        // Set default names if needed
        player1Field.setText("Player1");
        player2Field.setText("Player2");

        // Add button actions
        startButton.setOnAction(e -> startGame());
        recordButton.setOnAction(e -> showRecords());
    }

    @FXML
    public void onRecordButton(){

    }

    private void startGame() {
        String player1 = player1Field.getText().isEmpty() ? "Player1" : player1Field.getText();
        String player2 = player2Field.getText().isEmpty() ? "Player2" : player2Field.getText();

        System.out.println("Starting Two Player Game:");
        System.out.println("Player 1: " + player1);
        System.out.println("Player 2: " + player2);
        
        // Close the popup
        stage.close();
        
        // TODO: Start your actual game here
    }

    private void showRecords() {
        System.out.println("Showing Two Player Records");
        // TODO: Implement record display
    }
}
