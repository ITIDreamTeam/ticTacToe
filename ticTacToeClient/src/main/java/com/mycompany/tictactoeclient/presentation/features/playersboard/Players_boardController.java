/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.dataSource.GameApi;
import com.mycompany.tictactoeclient.data.models.Player;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.dtos.OnlinePlayersUpdate;
import com.mycompany.tictactoeclient.network.dtos.PlayerStatsDto;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
    
    private final NetworkClient client = NetworkClient.getInstance();
    private final GameApi gameApi = new GameApi(client);
    private final UserSession session = UserSession.getInstance();
    
    private Consumer<NetworkMessage> onlinePlayersListener;
      
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupListView();
        setupSearchFilter();
        setupListeners();
        loadOnlinePlayers();
    }
     private void setupListView() {
        playersListView.setCellFactory(listView -> new PlayerListCell());
        filteredData = new FilteredList<>(masterData, p -> true);
        playersListView.setItems(filteredData);
    }
    
    private void setupSearchFilter() {
        search_text_field.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(player -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String lower = newVal.toLowerCase();
                return player.getName().toLowerCase().contains(lower) ||
                       String.valueOf(player.getScore()).contains(lower);
            });
        });
    }
    
    private void setupListeners() {
        onlinePlayersListener = this::handleOnlinePlayersUpdate;
        client.on(MessageType.ONLINE_PLAYERS_UPDATE, onlinePlayersListener);
    }
    
    public void cleanup() {
        client.off(MessageType.ONLINE_PLAYERS_UPDATE, onlinePlayersListener);
    }
    
    private void loadOnlinePlayers() {
        if (!session.isOnline()) {
            App.showError("Not Connected", "You must be logged in to view online players.");
            loadingSpinner.setVisible(false);
            return;
        }
        
        loadingSpinner.setVisible(true);
        playersListView.setVisible(false);
        
        new Thread(() -> {
            try {
                gameApi.requestOnlinePlayers();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    App.showError("Network Error", "Failed to load online players: " + e.getMessage());
                    loadingSpinner.setVisible(false);
                });
            }
        }, "load-players-thread").start();
    }
    
    private void handleOnlinePlayersUpdate(NetworkMessage msg) {
        OnlinePlayersUpdate update = client.getGson().fromJson(
            msg.getPayload(), 
            OnlinePlayersUpdate.class
        );
        List<PlayerStatsDto> playersDto = update.getPlayers();
        List<Player> players = new ArrayList();
        
        for (PlayerStatsDto dto : update.getPlayers()) {
                Player player = dto.getPlayer();
                players.add(player);
            }
        Platform.runLater(() -> {
            masterData.clear();
            masterData.addAll(players);
            loadingSpinner.setVisible(false);
            playersListView.setVisible(true);
            
            System.out.println("Loaded " + players.size() + " online/in-game players");
        });
    }

    @FXML
    private void onTextFieldAction(ActionEvent event) {
        // Enter key pressed in search field
    }

    @FXML
    private void onClickBack(ActionEvent event) {
        cleanup();
        try {
            App.setRoot("home");
        } catch (IOException ex) {
            ex.printStackTrace();
            App.showError("Navigation Error", "Cannot navigate to home.");
        }
    }
}
