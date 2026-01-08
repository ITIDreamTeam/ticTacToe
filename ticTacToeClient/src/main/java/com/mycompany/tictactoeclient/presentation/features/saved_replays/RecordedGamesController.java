package com.mycompany.tictactoeclient.presentation.features.saved_replays;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.models.RecordedGame; // Import your POJO
import com.mycompany.tictactoeclient.shared.Navigation;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

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
        ObservableList<RecordedGame> games = FXCollections.observableArrayList();

        games.add(new RecordedGame("WithPlayer1", "20-12-2025", "02:22AM"));
        games.add(new RecordedGame("WithPlayer2", "20-12-2025", "02:32AM"));
        games.add(new RecordedGame("WithPlayer3", "20-12-2025", "02:44AM"));
        games.add(new RecordedGame("WithComputer", "20-12-2025", "12:22PM"));
        games.add(new RecordedGame("WithPlayer1", "20-12-2025", "02:22AM"));
        games.add(new RecordedGame("WithPlayer2", "20-12-2025", "02:32AM"));
        games.add(new RecordedGame("WithPlayer3", "20-12-2025", "02:44AM"));
        games.add(new RecordedGame("WithComputer", "20-12-2025", "12:22PM"));
        games.add(new RecordedGame("WithPlayer1", "20-12-2025", "02:22AM"));
        games.add(new RecordedGame("WithPlayer2", "20-12-2025", "02:32AM"));
        games.add(new RecordedGame("WithPlayer3", "20-12-2025", "02:44AM"));
        games.add(new RecordedGame("WithComputer", "20-12-2025", "12:22PM"));
        games.add(new RecordedGame("WithPlayer1", "20-12-2025", "02:22AM"));
        games.add(new RecordedGame("WithPlayer2", "20-12-2025", "02:32AM"));
        games.add(new RecordedGame("WithPlayer3", "20-12-2025", "02:44AM"));
        games.add(new RecordedGame("WithComputer", "20-12-2025", "12:22PM"));
        games.add(new RecordedGame("WithPlayer1", "20-12-2025", "02:22AM"));
        games.add(new RecordedGame("WithPlayer2", "20-12-2025", "02:32AM"));
        games.add(new RecordedGame("WithPlayer3", "20-12-2025", "02:44AM"));
        games.add(new RecordedGame("WithComputer", "20-12-2025", "12:22PM"));
        recordedGamesList.setItems(games);
    }

    @FXML
    private void onBackBtnClicked(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/profile.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    } catch (IOException ex) {
        ex.printStackTrace();
    }
    }
}
