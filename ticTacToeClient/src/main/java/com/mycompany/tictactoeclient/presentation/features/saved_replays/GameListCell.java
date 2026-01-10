package com.mycompany.tictactoeclient.presentation.features.saved_replays;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.dataSource.RecordedGamesJson;
import com.mycompany.tictactoeclient.data.models.RecordedGame;
import com.mycompany.tictactoeclient.presentation.features.game_board.RecordedGameDetails;
import com.mycompany.tictactoeclient.shared.Navigation;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

public class GameListCell extends ListCell<RecordedGame> {

    private Label playerLabel;
    private Label dateLabel;
    private Button playBtn;
    private Button deleteBtn;
    private AnchorPane anchorPane;
    private ObservableList<RecordedGame> gameList;

    public GameListCell(ObservableList<RecordedGame> gameList) {
        super();
        this.gameList = gameList;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/RecordedGameCell.fxml"));
            anchorPane = loader.load();
            playerLabel = (Label) anchorPane.lookup("#playerLabel");
            dateLabel = (Label) anchorPane.lookup("#dateLabel");
            playBtn = (Button) anchorPane.lookup("#playBtn");
            deleteBtn = (Button) anchorPane.lookup("#deleteBtn");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(RecordedGame game, boolean empty) {
        super.updateItem(game, empty);
        if (empty || game == null) {
            setText(null);
            setGraphic(null);
        } else {
            playerLabel.setText(game.getPlayerXName() + " vs " + game.getPlayerOName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dateLabel.setText(game.getGameDate().format(formatter));

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
                RecordedGamesJson.deleteGame(game);
                gameList.remove(game);
            });

            setGraphic(anchorPane);
        }
    }
}