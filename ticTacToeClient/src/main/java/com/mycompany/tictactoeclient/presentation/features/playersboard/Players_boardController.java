/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

import com.google.gson.Gson;
import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.dataSource.GameApi;
import com.mycompany.tictactoeclient.data.models.Player;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.UserSession;
import com.mycompany.tictactoeclient.network.dtos.OnlinePlayersUpdate;
import com.mycompany.tictactoeclient.network.dtos.PlayerStatsDto;
import com.mycompany.tictactoeclient.network.request.InviteRequest;
import com.mycompany.tictactoeclient.network.response.InviteResponse;
import com.mycompany.tictactoeclient.presentation.features.game_board.GameSessionManager;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    private final GameSessionManager gameSession = GameSessionManager.getInstance();

    private final Gson gson = new Gson();

    private Consumer<NetworkMessage> onlinePlayersListener;
    private Consumer<NetworkMessage> receiveInviteListener;
    private static Stage currentInvitePopup;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            client.send(new NetworkMessage(
                    MessageType.UPDATE_STATUS,
                    UserSession.getInstance().getUsername(),
                    "Server",
                    client.getGson().toJsonTree("ONLINE")
            ));
        } catch (Exception e) {
            System.err.println("Failed to update status to ONLINE");
        }

        setupListView();
        setupSearchFilter();
        setupAllListeners();
        requestOnlinePlayers();
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
                return player.getName().toLowerCase().contains(lower)
                        || String.valueOf(player.getScore()).contains(lower);
            });
        });
    }

    private void setupAllListeners() {
        onlinePlayersListener = this::handleOnlinePlayersUpdate;
        client.on(MessageType.ONLINE_PLAYERS_UPDATE, onlinePlayersListener);

        receiveInviteListener = this::handleReceiveInvite;
        client.on(MessageType.SEND_REQUEST, receiveInviteListener);
    }

    public void cleanup() {
        client.off(MessageType.ONLINE_PLAYERS_UPDATE, onlinePlayersListener);
        client.off(MessageType.SEND_REQUEST, receiveInviteListener);

        if (currentInvitePopup != null && currentInvitePopup.isShowing()) {
            currentInvitePopup.close();
            currentInvitePopup = null;
        }
    }

    private void handleOnlinePlayersUpdate(NetworkMessage msg) {
        try {
            OnlinePlayersUpdate update = client.getGson().fromJson(
                    msg.getPayload(),
                    OnlinePlayersUpdate.class
            );

            if (update != null && update.getPlayers() != null) {
                List<Player> players = new ArrayList<>();
                String currentUsername = session.getUsername();

                for (PlayerStatsDto dto : update.getPlayers()) {
                    Player p = dto.getPlayer();
                    if (!p.getName().equalsIgnoreCase(currentUsername)) {
                        p.setWins(dto.getWins());
                        p.setLosses(dto.getLosses());
                        players.add(p);
                    }
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
            Platform.runLater(() -> {
                App.showError("Error", "Failed to update player list: " + e.getMessage());
            });
        }
    }

    private void requestOnlinePlayers() {
        try {
            loadingSpinner.setVisible(true);
            playersListView.setVisible(false);

            client.send(new NetworkMessage(
                    MessageType.GET_ONLINE_PLAYERS,
                    session.getUsername(),
                    "server",
                    null
            ));
        } catch (Exception e) {
            System.err.println("Failed to request online players: " + e.getMessage());
            Platform.runLater(() -> {
                loadingSpinner.setVisible(false);
                App.showError("Network Error", "Failed to load players.");
            });
        }
    }

    private void handleReceiveInvite(NetworkMessage msg) {
        try {
            InviteRequest invite = gson.fromJson(msg.getPayload(), InviteRequest.class);

            if (invite == null || invite.getSenderUsername() == null) {
                System.err.println("Invalid invite received");
                return;
            }

            System.out.println("Received invite from: " + invite.getSenderUsername());

            Platform.runLater(() -> showInviteRequestPopup(invite));

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                App.showError("Error", "Failed to process invite: " + e.getMessage());
            });
        }
    }

    private void showInviteRequestPopup(InviteRequest invite) {
        if (currentInvitePopup != null && currentInvitePopup.isShowing()) {
            System.out.println("Popup already showing, ignoring new invite");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mycompany/tictactoeclient/request_popup.fxml")
            );
            Parent root = loader.load();
            Request_popupController popupController = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.setScene(new Scene(root));
            popupStage.getScene().setFill(Color.TRANSPARENT);

            if (playersListView.getScene() != null && playersListView.getScene().getWindow() != null) {
                Stage ownerStage = (Stage) playersListView.getScene().getWindow();
                popupStage.initOwner(ownerStage);

                javafx.beans.value.ChangeListener<Number> centerListener = (obs, oldVal, newVal) -> {
                    if (popupStage.isShowing() && !Double.isNaN(popupStage.getWidth())) {
                        double x = ownerStage.getX() + (ownerStage.getWidth() - popupStage.getWidth()) / 2;
                        double y = ownerStage.getY() + (ownerStage.getHeight() - popupStage.getHeight()) / 2;
                        popupStage.setX(x);
                        popupStage.setY(y);
                    }
                };

                ownerStage.xProperty().addListener(centerListener);
                ownerStage.yProperty().addListener(centerListener);

                popupStage.setOnShown(e -> {
                    centerListener.changed(null, null, null);
                });

                popupStage.setOnHidden(e -> {
                    currentInvitePopup = null;
                    ownerStage.xProperty().removeListener(centerListener);
                    ownerStage.yProperty().removeListener(centerListener);
                });
            }

            currentInvitePopup = popupStage;

            popupController.setStage(popupStage);
            popupController.setInviteData(invite);

            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            App.showError("Error", "Cannot show invite popup.");
            currentInvitePopup = null;
        }
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

    @FXML
    private void onTextFieldAction(ActionEvent event) {
        // Search on enter key
    }
}
