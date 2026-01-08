package com.mycompany.tictactoeclient.presentation.features.saved_replays;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.dataSource.RecordedGamesJson;
import com.mycompany.tictactoeclient.data.models.RecordedGame; // Import your POJO
import com.mycompany.tictactoeclient.shared.Navigation;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class RecordedGamesController implements Initializable {

    @FXML
    private Button backBtn;
    @FXML
    private ListView<RecordedGame> recordedGamesList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        recordedGamesList.setCellFactory(param -> new GameListCell());

        loadRecordedGames();
    }

    private void loadRecordedGames() {
        ObservableList<RecordedGame> games
                = FXCollections.observableArrayList(
                        RecordedGamesJson.loadGames()
                );

        recordedGamesList.setItems(games);
    }

    @FXML
    private void onBackBtnClicked(ActionEvent event) {
        Navigation.navigateTo(Navigation.profilePage);
    }
}
