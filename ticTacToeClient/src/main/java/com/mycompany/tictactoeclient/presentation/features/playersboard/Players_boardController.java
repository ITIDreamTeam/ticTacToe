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
        playersListView.setCellFactory(listView -> new PlayerListCell());
        filteredData = new FilteredList<>(masterData, p -> true);
        playersListView.setItems(filteredData);
        setupSearchFilter();

        NetworkClient.getInstance().on(MessageType.ONLINE_PLAYERS_UPDATE, this::handleOnlinePlayersUpdate);

        requestOnlinePlayers();
    }

    private void handleOnlinePlayersUpdate(NetworkMessage msg) {
        try {
            OnlinePlayersUpdate update = NetworkClient.getInstance().getGson().fromJson(
                msg.getPayload(), 
                OnlinePlayersUpdate.class
            );

            if (update != null && update.getPlayers() != null) {
                List<Player> players = new ArrayList<>();
                for (PlayerStatsDto dto : update.getPlayers()) {
                    Player p = dto.getPlayer();
                    p.setWins(dto.getWins());
                    p.setLosses(dto.getLosses());
                    players.add(p);
                }

                Platform.runLater(() -> {
                    masterData.clear();
                    masterData.addAll(players);
                    loadingSpinner.setVisible(false);
                    playersListView.setVisible(true);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestOnlinePlayers() {
        try{
        loadingSpinner.setVisible(true);
        NetworkClient.getInstance().send(new NetworkMessage(MessageType.GET_ONLINE_PLAYERS, null, null, null));
        }catch (Exception e){
                System.out.println(e.getMessage());
        }
    }
    
    
    
    private void handlePlayersUpdate(NetworkMessage msg) {
        // Debug: Print the raw payload to see exactly what the server sent
        System.out.println("Received Payload: " + msg.getPayload().toString());

        OnlinePlayersUpdate data = NetworkClient.getInstance().getGson()
                .fromJson(msg.getPayload(), OnlinePlayersUpdate.class);

        // Ensure we are on the JavaFX Application Thread
        Platform.runLater(() -> {
            if (data != null && data.getPlayers() != null) {
                masterData.clear();
                for (PlayerStatsDto player : data.getPlayers()) {
                    Player p=player.getPlayer();
                    p.setWins(player.getWins());
                    p.setLosses(player.getLosses());
                    masterData.add(p);
                }
            }
            loadingSpinner.setVisible(false);
            playersListView.setVisible(true);
        });
    

        filteredData = new FilteredList<>(masterData, p -> true);
        playersListView.setItems(filteredData);
    }

    
    private void setupSearchFilter() {
        search_text_field.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(player -> {
                if (newVal == null || newVal.isEmpty()) return true;
                
                String lower = newVal.toLowerCase();
                return player.getName().toLowerCase().contains(lower) ||
                       String.valueOf(player.getScore()).contains(lower);
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

