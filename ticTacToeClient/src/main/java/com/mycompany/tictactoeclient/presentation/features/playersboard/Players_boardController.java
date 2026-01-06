/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.dataSource.FakeDataSource;
import com.mycompany.tictactoeclient.data.models.Player;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author yasse
 */
public class Players_boardController implements Initializable {

    @FXML
    private TextField search_text_field;
    @FXML
    private ListView<Player> playersListView;
    @FXML
    private ProgressIndicator loadingSpinner;
    private final ObservableList<Player> masterData = FXCollections.observableArrayList();
    private FilteredList<Player> filteredData;
      
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playersListView.setCellFactory(listView -> new PlayerListCell());

        loadDataInBackground();
        filteredData = new FilteredList<>(masterData, p -> true);
        playersListView.setItems(filteredData);
        search_text_field.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(player -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String lower = newVal.toLowerCase();
                return player.getName().toLowerCase().contains(lower)
                        || String.valueOf(player.getScore()).contains(lower);
            });
        });
    }
    private void loadDataInBackground() {
        Task<List<Player>> task = new Task<>() {
            @Override
            protected List<Player> call() throws Exception {
                return FakeDataSource.getAllPlayers();
            }
        };
        task.setOnSucceeded(event -> {
            List<Player> result = task.getValue();
            masterData.addAll(result);
            loadingSpinner.setVisible(false);
            playersListView.setVisible(true);
        });
        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            loadingSpinner.setVisible(false);
        });
        new Thread(task).start();
    }

    @FXML
    private void onTextFieldAction(ActionEvent event) {
    }

    @FXML
    private void onClickBack(ActionEvent event) {
        try {
            App.setRoot("home");
            System.out.println("Go to home");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
