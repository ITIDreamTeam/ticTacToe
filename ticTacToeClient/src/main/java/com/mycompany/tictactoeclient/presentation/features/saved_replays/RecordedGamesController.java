package com.mycompany.tictactoeclient.presentation.features.saved_replays;

import com.mycompany.tictactoeclient.data.models.RecordedGame; // Import your POJO
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

    // 1. UPDATE TYPE: Change <?> to <RecordedGame>
    @FXML
    private ListView<RecordedGame> recordedGamesList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // 2. SET FACTORY: Tell the list to use your custom GameListCell
        recordedGamesList.setCellFactory(param -> new GameListCell());

        loadRecordedGames();
    }

    private void loadRecordedGames() {
        // 3. CREATE DATA: Use your POJO class
        ObservableList<RecordedGame> games = FXCollections.observableArrayList();

        // Add mock data matching your design
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

        // 4. SET ITEMS
        recordedGamesList.setItems(games);
    }

    @FXML
    private void onBackBtnClicked(ActionEvent event) {
                        try {
            // 1. Load the profile screen
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/tictactoeclient/profile.fxml"));
            
            // 2. Get the current Window (Stage)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // 3. Swap the scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error loading Profile screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
