package com.mycompany.tictactoeclient.presentation.features.saved_replays;

import com.mycompany.tictactoeclient.data.models.RecordedGame;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

public class GameListCell extends ListCell<RecordedGame> {

    @FXML
    private Label playerLabel;
    @FXML
    private Label dateLabel;

    // NEW: The buttons you added
    @FXML
    private Button playBtn;
    @FXML
    private Button deleteBtn;

    private FXMLLoader mLLoader;
    private AnchorPane rootAnchorPane;

    @Override
    protected void updateItem(RecordedGame game, boolean empty) {
        super.updateItem(game, empty);

        if (empty || game == null) {
            setText(null);
            setGraphic(null);
        } else {
            // 1. Load FXML if needed
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/recordedGameCell.fxml"));
                mLLoader.setController(this);

                try {
                    rootAnchorPane = mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 2. Set Text Data
            playerLabel.setText(game.getPlayerInfo());
            dateLabel.setText(game.getDate() + " " + game.getTime());

            // 3. SET BUTTON ACTIONS
            // We use 'getItem()' to know WHICH game this row represents
            playBtn.setOnAction(event -> {
                System.out.println("Playing game against: " + getItem().getPlayerInfo());
                // TODO: Add logic to navigate to the Replay Board
            });

            deleteBtn.setOnAction(event -> {
                System.out.println("Deleting game from: " + getItem().getDate());
                // To remove it from the list visually, we need a way to talk back to the Controller
                // For now, just print to console.
                getListView().getItems().remove(getItem());
            });

            // 4. Show the row
            setText(null);
            setGraphic(rootAnchorPane);
        }
    }
}
