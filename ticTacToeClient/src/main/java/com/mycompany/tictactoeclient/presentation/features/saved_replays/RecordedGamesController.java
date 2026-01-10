package com.mycompany.tictactoeclient.presentation.features.saved_replays;

import com.mycompany.tictactoeclient.data.dataSource.RecordedGamesJson;
import com.mycompany.tictactoeclient.data.models.RecordedGame;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.mycompany.tictactoeclient.shared.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class RecordedGamesController implements Initializable {

    @FXML
    private ListView<RecordedGame> recordedGamesList;

    private ObservableList<RecordedGame> gameList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<RecordedGame> games = RecordedGamesJson.loadGames();
        gameList = FXCollections.observableArrayList(games);
        recordedGamesList.setItems(gameList);
        recordedGamesList.setCellFactory(new Callback<ListView<RecordedGame>, ListCell<RecordedGame>>() {
            @Override
            public ListCell<RecordedGame> call(ListView<RecordedGame> listView) {
                return new GameListCell(gameList);
            }
        });
    }

    @FXML
    private void onBackBtnClicked(ActionEvent event) {
        Navigation.navigateTo(Navigation.homePage);
    }
}