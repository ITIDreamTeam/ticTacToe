/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.dataSource.FakeDataSource;
import com.mycompany.tictactoeclient.data.models.Player;
import com.mycompany.tictactoeclient.data.models.Player.PlayerStatus;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.dtos.OnlinePlayersUpdate;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.application.Platform;
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
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        playersListView.setCellFactory(listView -> new PlayerListCell());
//
//        loadDataInBackground();
//        filteredData = new FilteredList<>(masterData, p -> true);
//        playersListView.setItems(filteredData);
//        search_text_field.textProperty().addListener((obs, oldVal, newVal) -> {
//            filteredData.setPredicate(player -> {
//                if (newVal == null || newVal.isEmpty()) {
//                    return true;
//                }
//                String lower = newVal.toLowerCase();
//                return player.getName().toLowerCase().contains(lower)
//                        || String.valueOf(player.getScore()).contains(lower);
//            });
//        });
//    }
//    private void loadDataInBackground() {
//        Task<List<Player>> task = new Task<>() {
//            @Override
//            protected List<Player> call() throws Exception {
//                return FakeDataSource.getAllPlayers();
//            }
//        };
//        task.setOnSucceeded(event -> {
//            List<Player> result = task.getValue();
//            masterData.addAll(result);
//            loadingSpinner.setVisible(false);
//            playersListView.setVisible(true);
//        });
//        task.setOnFailed(event -> {
//            task.getException().printStackTrace();
//            loadingSpinner.setVisible(false);
//        });
//        new Thread(task).start();
//    }
//    private void handlePlayersUpdate(NetworkMessage msg) {
//        OnlinePlayersUpdate data = NetworkClient.getInstance().getGson()
//                .fromJson(msg.getPayload(), OnlinePlayersUpdate.class);
//
//        masterData.clear();
//        
//        if (data.getPlayers() != null) {
//            for (var dto : data.getPlayers()) {
//                Player p = new Player(dto.getName(), dto.getScore(), PlayerStatus.ONLINE);
//                masterData.add(p);
//            }
//        }
//        
//        loadingSpinner.setVisible(false);
//        playersListView.setVisible(true);
//    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playersListView.setCellFactory(listView -> new PlayerListCell());

        filteredData = new FilteredList<>(masterData, p -> true);
        playersListView.setItems(filteredData);
        setupSearchFilter();

        NetworkClient.getInstance().on(MessageType.ONLINE_PLAYERS_UPDATE, this::handlePlayersUpdate);

        requestOnlinePlayers();
    }

    private void requestOnlinePlayers() {
        try {
            loadingSpinner.setVisible(true);
            NetworkMessage request = new NetworkMessage(MessageType.GET_ONLINE_PLAYERS, null, null, null);
            NetworkClient.getInstance().send(request);
        } catch (Exception e) {
            e.printStackTrace();
            loadingSpinner.setVisible(false);
        }
    }

    private void handlePlayersUpdate(NetworkMessage msg) {
        // Debug: Print the raw payload to see exactly what the server sent
        System.out.println("Received Payload: " + msg.getPayload().toString());

        OnlinePlayersUpdate data = NetworkClient.getInstance().getGson()
                .fromJson(msg.getPayload(), OnlinePlayersUpdate.class);

        // Ensure we are on the JavaFX Application Thread
        Platform.runLater(() -> {
            if (data != null && data.getUsernames() != null) {
                masterData.clear();
                for (String username : data.getUsernames()) {
                    masterData.add(new Player(username, 0, PlayerStatus.ONLINE));
                }
            }
            loadingSpinner.setVisible(false);
            playersListView.setVisible(true);
        });
    }

    private void setupSearchFilter() {
        search_text_field.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(player -> {
                if (newVal == null || newVal.isEmpty()) return true;
                
                String lower = newVal.toLowerCase();
                return player.getName().toLowerCase().contains(lower)
                        || String.valueOf(player.getScore()).contains(lower);
            });
        });
    }

    @FXML
    private void onClickBack(ActionEvent event) {
        NetworkClient.getInstance().off(MessageType.ONLINE_PLAYERS_UPDATE, this::handlePlayersUpdate);
        
        try {
            App.setRoot("home");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onTextFieldAction(ActionEvent event) {
    }
}

