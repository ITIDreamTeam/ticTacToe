/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeserver.presentation.features.playersDetails;

import com.mycompany.tictactoeserver.data.dataSource.FakeDataSource;
import com.mycompany.tictactoeserver.data.model.Player;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author yasse
 */
public class Players_boardController implements Initializable {

    @FXML
    private TextField search_text_field;
    @FXML
    private VBox playersContainer;

    private List<Player> allPlayers;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        allPlayers = FakeDataSource.getAllPlayers();
        loadPlayersList(allPlayers);
        search_text_field.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPlayers(newValue);
        });
    }

    private void filterPlayers(String query) {
        if (query == null || query.isEmpty()) {
            loadPlayersList(allPlayers);
            return;
        }
        String lowerCaseQuery = query.toLowerCase();
        List<Player> filteredList = allPlayers.stream()
                .filter(player
                        -> player.getName().toLowerCase().contains(lowerCaseQuery)
                || String.valueOf(player.getScore()).startsWith(lowerCaseQuery)
                )
                .collect(Collectors.toList());
        loadPlayersList(filteredList);
    }

    private void loadPlayersList(List<Player> players) {
        playersContainer.getChildren().clear();
        for (Player player : players) {
            try {
                // Load the single card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeserver/player_card.fxml"));
                HBox cardBox = loader.load();

                Player_cardController cardController = loader.getController();
                cardController.setPlayerData(player);

                playersContainer.getChildren().add(cardBox);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onTextFieldAction(ActionEvent event) {
    }

    public void loadPlayers(List<Player> players) {
        playersContainer.getChildren().clear();

        for (Player player : players) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tictactoeserver/player_card.fxml"));
                HBox cardBox = loader.load();
                Player_cardController cardController = loader.getController();
                cardController.setPlayerData(player);
                playersContainer.getChildren().add(cardBox);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void onBackClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tictactoeserver/home.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load Home Screen FXML");
        }
    }

}
