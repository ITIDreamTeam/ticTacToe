package com.mycompany.tictactoeclient.presentation.features.saved_replays;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.models.RecordedGame;
import com.mycompany.tictactoeclient.shared.Navigation;
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
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/recordedGameCell.fxml"));
                mLLoader.setController(this);
                try {
                    rootAnchorPane = mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            playerLabel.setText(game.getPlayerInfo());
            dateLabel.setText(game.getFormattedDate());
            playBtn.setOnAction(event -> {
                System.out.println("Playing game against: " + getItem().getPlayerInfo());
                App.setRecordedGameDetails(new com.mycompany.tictactoeclient.presentation.features.game_board.RecordedGameDetails(
                        game.getPlayerXName(),
                        game.getPlayerOName(),
                        game.getFormattedDate(),
                        game.getMoves()
                ));
                Navigation.navigateTo(Navigation.gameBoardPage);
            });

            deleteBtn.setOnAction(event -> {
                System.out.println("Deleting game from: " + getItem().getFormattedDate());
                getListView().getItems().remove(getItem());
            });
            setText(null);
            setGraphic(rootAnchorPane);
        }
    }
}
