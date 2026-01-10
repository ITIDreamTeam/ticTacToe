package com.mycompany.tictactoeclient.presentation.features.game_board;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.core.RecordingSettings;
import com.mycompany.tictactoeclient.data.dataSource.RecordedGamesJson;
import com.mycompany.tictactoeclient.data.models.GameSession;
import com.mycompany.tictactoeclient.data.dataSource.GameApi;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.dtos.GameMoveDto;
import com.mycompany.tictactoeclient.presentation.features.game_board.GameEngine.Player;
import com.mycompany.tictactoeclient.presentation.features.home.OnePlayerPopupController;
import com.mycompany.tictactoeclient.data.models.MoveRecord;
import com.mycompany.tictactoeclient.data.models.PlayerType;
import com.mycompany.tictactoeclient.data.models.RecordedGame;
import com.mycompany.tictactoeclient.network.UserSession;
import com.mycompany.tictactoeclient.shared.Navigation;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Game_boardController implements Initializable {

    @FXML
    private Label scoreXLabel;
    @FXML
    private Label scoreOLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private GridPane gameGrid;
    @FXML
    private Line winningLine;
    @FXML
    private Pane linePane;
    @FXML
    private Label playerNameX;
    @FXML
    private Label playerNameO;
    @FXML
    private Circle recordingDot;
    @FXML
    private HBox recordingBox;

    public static enum GameMode {
        vsComputer, twoPlayer, withFriend
    };

    // Add a pane in your FXML to hold the video, or just pop up a new stage
    // For this example, I assume you might want to show it on top of the board
    // @FXML private StackPane videoContainer; 
    private Button[][] buttons = new Button[3][3];
    private GameEngine engine;
    private int xScore = 0;
    private int oScore = 0;
    private Timeline blinkingTimeline;

    public GameMode currentMode;
    private GameEngine.Player nextStarter = GameEngine.Player.X;
    private GameEngine.Player mySymbol;
    private String opponentName;

    private final NetworkClient client = NetworkClient.getInstance();
    private final GameApi gameApi = new GameApi(client);
    private final GameSessionManager sessionManager = GameSessionManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        engine = new GameEngine();

        linePane.prefWidthProperty().bind(gameGrid.widthProperty());
        linePane.prefHeightProperty().bind(gameGrid.heightProperty());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button btn = new Button("");
                btn.getStyleClass().add("game-tile");
                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                btn.setUserData(new int[]{row, col});
                btn.setOnAction(this::handlePlayerMove);
                buttons[row][col] = btn;
                gameGrid.add(btn, col, row);
            }
        }
        this.currentMode = sessionManager.getGameMode();

        Platform.runLater(() -> {
            RecordingSettings.recordingEnabledProperty()
                    .addListener((obs, oldVal, newVal) -> {
                        updateRecordingState(newVal);
                    });
            updateRecordingState(RecordingSettings.isRecordingEnabled());
        });

        if (this.currentMode == null) {
            this.currentMode = GameMode.vsComputer;
        }

        if (this.currentMode == GameMode.withFriend) {
            setupOnlineGame();
        } else {
            setupLocalOrComputerGame();
        }

        RecordedGameDetails recordedGameDetails = App.getRecordedGameDetails();
        if (recordedGameDetails != null) {
            startReplay(recordedGameDetails);
            App.setRecordedGameDetails(null);
        } else {
            startNewGame();
        }
    }

    private void startReplay(RecordedGameDetails recordedGame) {
        setPlayersName(recordedGame.getPlayerXName(), recordedGame.getPlayerOName());
        setBoardDisabled(true);
        statusLabel.setText("Replaying game...");

        // Clear the board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].getStyleClass().removeAll("tile-x", "tile-o", "tile-winning");
                buttons[i][j].setDisable(true); // Disable buttons during replay
            }
        }
        winningLine.setVisible(false);

        Timeline timeline = new Timeline();
        for (int i = 0; i < recordedGame.getMoves().size(); i++) {
            final MoveRecord currentMove = recordedGame.getMoves().get(i);
            Duration time = Duration.seconds(i + 1);
            KeyFrame keyFrame = new KeyFrame(time, e -> {
                Button btn = buttons[currentMove.getRow()][currentMove.getCol()];
                updateButton(btn, currentMove.getPlayer() == PlayerType.X ? Player.X : Player.O);
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.setOnFinished(e -> statusLabel.setText("Replay finished."));
        timeline.play();
    }

    public void setPlayersName(String playerX, String PlayerO) {
        playerNameX.setText(playerX);
        playerNameO.setText(PlayerO);
        statusLabel.setText(playerNameX.getText() + " Turn");
    }

    public void updateRecordingState(boolean isRecorded) {
        System.out.println("is Recorded = " + isRecorded);

        if (isRecorded) {
            startRecordingUI();
            engine.startRecording(playerNameX.getText(), playerNameO.getText(), UserSession.getInstance().getUsername());

        } else {
            stopRecordingUI();
            engine.stopRecording();
        }
    }

    private void startRecordingUI() {
        recordingBox.setVisible(true);
        recordingBox.setManaged(true);

        blinkingTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5),
                        e -> recordingDot.setVisible(!recordingDot.isVisible()))
        );
        blinkingTimeline.setCycleCount(Timeline.INDEFINITE);
        blinkingTimeline.play();
    }

    private void stopRecordingUI() {
        if (blinkingTimeline != null) {
            blinkingTimeline.stop();
        }

        recordingBox.setVisible(false);
        recordingBox.setManaged(false);
    }

    private void startNewGame() {
        engine.resetGame(nextStarter);
        if (currentMode == GameMode.withFriend) {
            setupOnlineGame();
        } else {
            setupLocalOrComputerGame();
        }
    }

    private void setupOnlineGame() {
        client.on(MessageType.UPDATE_BOARD, this::onBoardUpdate);
        client.on(MessageType.GAME_OVER, this::onGameOver);
        client.on(MessageType.OPPONENT_LEFT, this::onOpponentLeft);

        this.opponentName = sessionManager.getOpponentName();
        boolean isMyTurnFirst = sessionManager.isMyTurnFirst();
        updateRecordingState(sessionManager.isRecorded());

        this.mySymbol = isMyTurnFirst ? GameEngine.Player.X : GameEngine.Player.O;
        String myName = UserSession.getInstance().getUsername();

        if (mySymbol == GameEngine.Player.X) {
            playerNameX.setText(myName);
            playerNameO.setText(opponentName);
        } else {
            playerNameX.setText(opponentName);
            playerNameO.setText(myName);
        }

        engine.resetGame(GameEngine.Player.X);
        resetBoardUI();

        if (engine.getCurrentPlayer() != mySymbol) {
            setBoardDisabled(true);
            statusLabel.setText(opponentName + "'s Turn");
        } else {
            setBoardDisabled(false);
            statusLabel.setText("Your Turn");
        }
    }

    private void setupLocalOrComputerGame() {
        playerNameX.setText(sessionManager.getUserName());
        playerNameO.setText(sessionManager.getOpponentName());

        if (currentMode == GameMode.vsComputer) {
            engine.difficulty = OnePlayerPopupController.difficulty;
        }

        startNewLocalGame(GameEngine.Player.X);
    }

    private void startNewLocalGame(GameEngine.Player starter) {
        engine.resetGame(starter);
        resetBoardUI();
        setBoardDisabled(false);
        updateStatusLabelForLocal();

        if (currentMode == GameMode.vsComputer && starter == GameEngine.Player.O) {
            setBoardDisabled(true);
            statusLabel.setText("Computer is thinking...");
            PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
            pause.setOnFinished(e -> performComputerMove());
            pause.play();
        }
    }

    private void handlePlayerMove(ActionEvent event) {
        if (engine.isGameOver()) {
            return;
        }

        if (currentMode == GameMode.withFriend) {
            if (engine.getCurrentPlayer() != mySymbol) {
                return;
            }
        }

        Button clickedButton = (Button) event.getSource();
        int[] coords = (int[]) clickedButton.getUserData();

        if (engine.makeMove(coords[0], coords[1])) {
            updateButton(clickedButton, engine.getCurrentPlayer());
            engine.switchTurn();

            if (currentMode == GameMode.withFriend) {
                try {
                    gameApi.sendGameMove(opponentName, coords[0], coords[1]);
                    setBoardDisabled(true);
                    statusLabel.setText(opponentName + "'s Turn");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }

            if (checkLocalGameStatus()) {
                return;
            }

            if (currentMode == GameMode.vsComputer && engine.getCurrentPlayer() == GameEngine.Player.O) {
                setBoardDisabled(true);
                statusLabel.setText("Computer is thinking...");
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
                pause.setOnFinished(e -> performComputerMove());
                pause.play();
            } else {
                updateStatusLabelForLocal();
            }
        }
    }

    private void performComputerMove() {
        if (engine.isGameOver()) {
            return;
        }
        int[] move = engine.getComputerMove();
        if (move != null) {
            if (engine.makeMove(move[0], move[1])) {
                updateButton(buttons[move[0]][move[1]], engine.getCurrentPlayer());
                engine.switchTurn();

                if (!checkLocalGameStatus()) {
                    setBoardDisabled(false);
                    updateStatusLabelForLocal();
                }
            }
        }
    }

    private void onBoardUpdate(NetworkMessage msg) {
        GameMoveDto move = client.getGson().fromJson(msg.getPayload(), GameMoveDto.class);
        Platform.runLater(() -> {
            if (engine.makeMove(move.getRow(), move.getCol())) {
                updateButton(buttons[move.getRow()][move.getCol()], engine.getCurrentPlayer());
                engine.switchTurn();
                setBoardDisabled(false);
                statusLabel.setText("Your Turn");
            }
        });
    }

    private void onGameOver(NetworkMessage msg) {
        String result = client.getGson().fromJson(msg.getPayload(), String.class);
        Platform.runLater(() -> {
            engine.setGameOver(true);
            setBoardDisabled(true);
            statusLabel.setText(result);

            boolean isWin = result.toLowerCase().contains("win");
            boolean isLose = result.toLowerCase().contains("lose");

            if (isWin) {
                updateScore(mySymbol);
                updateUserSessionScore(100);
            } else if (isLose) {
                updateScore(mySymbol == Player.X ? Player.O : Player.X);
                updateUserSessionScore(-50);
            } else {
                updateUserSessionScore(10);
            }
            playVideoAndThen(isWin, () -> {
                quitGame();
            });
        });
    }

    private void onOpponentLeft(NetworkMessage msg) {
        Platform.runLater(() -> {
            engine.setGameOver(true);
            setBoardDisabled(true);
            updateScore(mySymbol);
            updateUserSessionScore(50);
            playVideoAndThen(true, () -> {
                App.showInfo("You Win!", "Opponent Surrendered");
                quitGame();
            });
        });
    }

    private boolean checkLocalGameStatus() {
        GameEngine.Player winner = engine.getWinner();
        if (winner != GameEngine.Player.NONE) {
            updateScore(winner);
            int[] coords = engine.getWinningCoords();
            if (coords != null) {
                drawWinningLine(coords[0], coords[1]);
            }

            String wName = (winner == Player.X) ? playerNameX.getText() : playerNameO.getText();
            boolean isComputerWin = (currentMode == GameMode.vsComputer && winner == Player.O);
            boolean isDraw = false;
            playVideoAndThen(!isComputerWin, () -> {
                showPlayAgainPopup(wName + " Wins!");
            });
            return true;
        } else if (engine.isBoardFull()) {
            playVideoAndThen(false, () -> {
                showPlayAgainPopup("It's a Draw!");
            });
            return true;
        }
        return false;
    }

    private void playVideoAndThen(boolean isWin, Runnable onFinished) {
        // we will add video here 
        System.out.println(isWin ? "Playing WIN video..." : "Playing LOSE/DRAW video...");

        PauseTransition videoDuration = new PauseTransition(Duration.seconds(3));
        videoDuration.setOnFinished(e -> {
            Platform.runLater(onFinished);
        });
        videoDuration.play();
    }

    private void showPlayAgainPopup(String message) {
        Scene scene = gameGrid.getScene();
        if (scene == null || scene.getWindow() == null) {
            System.out.println("Warning: Scene is null, skipping popup.");
            return;
        }
        Stage owner = (Stage) scene.getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/PlayAgainPopup.fxml"));
            Parent root = loader.load();
            PlayAgainPopupController popup = loader.getController();

            popup.setWinnerName(message);
            popup.setOnPlayAgain(() -> startNewLocalGame(GameEngine.Player.X));
            popup.setOnBack(() -> quitGame());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(new Scene(root, Color.TRANSPARENT));

            stage.initOwner(owner);
            stage.setX(owner.getX() + (owner.getWidth() / 2) - 175);
            stage.setY(owner.getY() + (owner.getHeight() / 2) - 125);
            stage.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        if (currentMode == GameMode.withFriend && !engine.isGameOver()) {
            try {
                gameApi.sendSurrender();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        quitGame();
    }

    private void quitGame() {
        if (currentMode == GameMode.withFriend) {
            client.off(MessageType.UPDATE_BOARD, this::onBoardUpdate);
            client.off(MessageType.GAME_OVER, this::onGameOver);
            client.off(MessageType.OPPONENT_LEFT, this::onOpponentLeft);
        }
        sessionManager.clearSession();
        if (currentMode == GameMode.withFriend) {
            Navigation.navigateTo(Navigation.playersBoardPage);
        } else {
            Navigation.navigateTo(Navigation.homePage);
        }
    }

    private void updateStatusLabelForLocal() {
        if (engine.getCurrentPlayer() == Player.X) {
            statusLabel.setText(playerNameX.getText() + "'s Turn");
        } else {
            statusLabel.setText(playerNameO.getText() + "'s Turn");
        }
    }

    private void resetBoardUI() {
        winningLine.setVisible(false);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].getStyleClass().removeAll("tile-x", "tile-o", "tile-winning");
                buttons[i][j].setDisable(false);
            }
        }
    }

    private void updateButton(Button btn, GameEngine.Player player) {
        btn.setText(player.toString());
        btn.getStyleClass().add(player == GameEngine.Player.X ? "tile-x" : "tile-o");
    }

    private void setBoardDisabled(boolean disable) {
        gameGrid.setDisable(disable);
    }

    private void updateScore(GameEngine.Player winner) {
        if (winner == GameEngine.Player.X) {
            xScore++;
            scoreXLabel.setText(String.valueOf(xScore));
        } else if (winner == GameEngine.Player.O) {
            oScore++;
            scoreOLabel.setText(String.valueOf(oScore));
        }
    }

    private void drawWinningLine(int startIdx, int endIdx) {
        int startRow = startIdx / 3;
        int startCol = startIdx % 3;
        int endRow = endIdx / 3;
        int endCol = endIdx % 3;
        Button startButton = buttons[startRow][startCol];
        Button endButton = buttons[endRow][endCol];

        double startX = startButton.getLayoutX() + startButton.getWidth() / 2;
        double startY = startButton.getLayoutY() + startButton.getHeight() / 2;
        double endX = endButton.getLayoutX() + endButton.getWidth() / 2;
        double endY = endButton.getLayoutY() + endButton.getHeight() / 2;

        winningLine.setStartX(startX);
        winningLine.setStartY(startY);
        winningLine.setEndX(endX);
        winningLine.setEndY(endY);
        winningLine.setVisible(true);
    }

    private void updateUserSessionScore(int pointsToAdd) {
        if (sessionManager.isOnlineGame()) {
            UserSession session = UserSession.getInstance();
            int currentScore = session.getScore();
            session.setScore(currentScore + pointsToAdd);
            System.out.println("Local Session Updated: New Score = " + session.getScore());
        }
    }
}
