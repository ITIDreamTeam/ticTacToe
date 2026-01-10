package com.mycompany.tictactoeclient.presentation.features.game_board;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.core.RecordingSettings;
import com.mycompany.tictactoeclient.data.dataSource.GameApi;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.dtos.GameMoveDto;
import com.mycompany.tictactoeclient.presentation.features.game_board.GameEngine.Player;
import com.mycompany.tictactoeclient.presentation.features.home.OnePlayerPopupController;
import com.mycompany.tictactoeclient.data.models.MoveRecord;
import com.mycompany.tictactoeclient.data.models.PlayerType;
import com.mycompany.tictactoeclient.network.UserSession;
import com.mycompany.tictactoeclient.shared.Navigation;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public class Game_boardController implements Initializable {

    @FXML private Label scoreXLabel, scoreOLabel, statusLabel, playerNameX, playerNameO;
    @FXML private GridPane gameGrid;
    @FXML private Line winningLine;
    @FXML private Pane linePane;
    @FXML private Circle recordingDot;
    @FXML private HBox recordingBox;

    public static enum GameMode { vsComputer, twoPlayer, withFriend }

    private Button[][] buttons = new Button[3][3];
    private GameEngine engine;
    private int xScore = 0, oScore = 0;
    private Timeline blinkingTimeline;
    private boolean videoIsShowing = false;
    private boolean isGameEnded = false;
    private boolean isNavigating = false;

    public GameMode currentMode;
    private Player mySymbol;
    private String opponentName;

    private final NetworkClient client = NetworkClient.getInstance();
    private final GameApi gameApi = new GameApi(client);
    private final GameSessionManager sessionManager = GameSessionManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            client.send(new NetworkMessage(
                MessageType.UPDATE_STATUS,
                UserSession.getInstance().getUsername(),
                "Server",
                client.getGson().toJsonTree("IN_GAME") // Sets status back to 3
            ));
        } catch (Exception e) {
            System.err.println("Failed to revert status to WAITING");
        }
        engine = new GameEngine();
        linePane.prefWidthProperty().bind(gameGrid.widthProperty());
        linePane.prefHeightProperty().bind(gameGrid.heightProperty());

        setupGrid();

        this.currentMode = sessionManager.getGameMode();
        if (this.currentMode == null) this.currentMode = GameMode.vsComputer;

        Platform.runLater(() -> {
            
            RecordingSettings.recordingEnabledProperty().addListener((obs, oldV, newV) -> updateRecordingState(newV));
            updateRecordingState(RecordingSettings.isRecordingEnabled());
        });

        startNewGame();
    }

    private void setupGrid() {
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
    }

    // --- MOVE ROUTING ---
    private void handlePlayerMove(ActionEvent event) {
        if (engine.isGameOver() || isGameEnded) return;

        Button clickedButton = (Button) event.getSource();
        int[] coords = (int[]) clickedButton.getUserData();

        switch (currentMode) {
            case vsComputer:
                handleVsComputerMove(clickedButton, coords);
                break;
            case twoPlayer:
                handleTwoPlayerMove(clickedButton, coords);
                break;
            case withFriend:
                handleWithFriendMove(clickedButton, coords);
                break;
        }
    }

    private void handleVsComputerMove(Button btn, int[] coords) {
        if (engine.getCurrentPlayer() != Player.X) return;
        if (engine.makeMove(coords[0], coords[1])) {
            updateButton(btn, Player.X);
            if (!checkLocalGameStatus()) {
                engine.switchTurn();
                triggerComputerThinking();
            }
        }
    }

    private void handleTwoPlayerMove(Button btn, int[] coords) {
        Player p = engine.getCurrentPlayer();
        if (engine.makeMove(coords[0], coords[1])) {
            updateButton(btn, p);
            if (!checkLocalGameStatus()) {
                engine.switchTurn();
                updateStatusLabelForLocal();
            }
        }
    }

    private void handleWithFriendMove(Button btn, int[] coords) {
        if (engine.getCurrentPlayer() != mySymbol) return;

        if (engine.makeMove(coords[0], coords[1])) {
            updateButton(btn, mySymbol);
            engine.switchTurn();
            handleOnlineMove(coords);
        }
    }

    // --- COMPUTER TURN ---
    private void triggerComputerThinking() {
        setBoardDisabled(true);
        statusLabel.setText("Computer is thinking...");
        PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
        pause.setOnFinished(e -> {
            int[] move = engine.getComputerMove();
            if (move != null && engine.makeMove(move[0], move[1])) {
                updateButton(buttons[move[0]][move[1]], Player.O);
                if (!checkLocalGameStatus()) {
                    engine.switchTurn();
                    setBoardDisabled(false);
                    updateStatusLabelForLocal();
                }
            }
        });
        pause.play();
    }

    // --- LOCAL GAME STATUS ---
    private boolean checkLocalGameStatus() {
        Player winner = engine.getWinner();
        
        if (winner != Player.NONE) {
            engine.setGameOver(true);
            isGameEnded = true;
            updateScore(winner);
            int[] coords = engine.getWinningCoords();
            if (coords != null) drawWinningLine(coords[0], coords[1]);

            String wName = (winner == Player.X) ? playerNameX.getText() : playerNameO.getText();
            
            boolean playWinVideo = true;
            if (currentMode == GameMode.vsComputer && winner == Player.O) {
                playWinVideo = false;
            }

            playVideoAndThen(playWinVideo, () -> showPlayAgainPopup(wName + " Wins!"));
            return true;
        } 
        
        if (engine.isBoardFull()) {
            engine.setGameOver(true);
            isGameEnded = true;
            playDrawVideoAndThen(() -> showPlayAgainPopup("It's a Draw!"));
            return true;
        }
        
        return false;
    }

    // --- VIDEO SYSTEM ---
    private void playVideoAndThen(boolean isWin, Runnable onFinished) {
        String path = isWin ? "/videos/win.mp4" : "/videos/lose.mp4";
        playVideoSafe(path, onFinished);
    }

    private void playDrawVideoAndThen(Runnable onFinished) {
        URL drawResource = getClass().getResource("/videos/draw.mp4");
        String path = (drawResource != null) ? "/videos/draw.mp4" : "/videos/lose.mp4";
        playVideoSafe(path, onFinished);
    }

    private void playVideoSafe(String videoPath, Runnable onFinished) {
        if (videoIsShowing || isNavigating) {
            System.out.println("Skipping video - already showing or navigating");
            if (onFinished != null) Platform.runLater(onFinished);
            return;
        }

        URL resource = getClass().getResource(videoPath);
        if (resource == null) {
            System.err.println("Video not found: " + videoPath);
            if (onFinished != null) Platform.runLater(onFinished);
            return;
        }

        Scene currentScene = gameGrid.getScene();
        if (currentScene == null || currentScene.getWindow() == null) {
            System.err.println("Scene not attached, skipping video");
            if (onFinished != null) Platform.runLater(onFinished);
            return;
        }

        videoIsShowing = true;
        Window window = currentScene.getWindow();
        Stage owner = (window instanceof Stage) ? (Stage) window : null;

        if (owner == null) {
            System.err.println("Owner stage not available");
            videoIsShowing = false;
            if (onFinished != null) Platform.runLater(onFinished);
            return;
        }

        MediaPlayer mediaPlayer = null;
        Stage videoStage = null;

        try {
            Media media = new Media(resource.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitWidth(400);
            mediaView.setFitHeight(250);

            StackPane root = new StackPane(mediaView);
            root.setStyle("-fx-background-color: black; -fx-border-color: #3fefef; -fx-border-width: 2;");

            videoStage = new Stage(StageStyle.UNDECORATED);
            videoStage.initModality(Modality.APPLICATION_MODAL);
            videoStage.initOwner(owner);
            videoStage.setScene(new Scene(root, 400, 250));

            final MediaPlayer finalPlayer = mediaPlayer;
            final Stage finalStage = videoStage;

            Runnable cleanUp = () -> {
                try {
                    if (finalStage != null && finalStage.isShowing()) {
                        finalStage.close();
                    }
                } catch (Exception e) {
                    System.err.println("Error closing stage: " + e.getMessage());
                }
                
                try {
                    if (finalPlayer != null) {
                        finalPlayer.stop();
                        finalPlayer.dispose();
                    }
                } catch (Exception e) {
                    System.err.println("Error disposing player: " + e.getMessage());
                }
                
                videoIsShowing = false;
                
                if (onFinished != null) {
                    Platform.runLater(onFinished);
                }
            };

            finalPlayer.setOnEndOfMedia(cleanUp);
            finalPlayer.setOnError(() -> {
                System.err.println("Media error: " + finalPlayer.getError());
                cleanUp.run();
            });

            finalPlayer.setOnReady(() -> {
                try {
                    if (owner.isShowing() && finalStage != null) {
                        finalStage.setX(owner.getX() + (owner.getWidth() / 2) - 200);
                        finalStage.setY(owner.getY() + (owner.getHeight() / 2) - 125);
                        finalStage.show();
                        finalPlayer.play();
                    } else {
                        cleanUp.run();
                    }
                } catch (Exception e) {
                    System.err.println("Error showing video: " + e.getMessage());
                    cleanUp.run();
                }
            });

            PauseTransition watchdog = new PauseTransition(Duration.seconds(7));
            watchdog.setOnFinished(e -> {
                if (videoIsShowing) {
                    System.out.println("Video watchdog timeout");
                    cleanUp.run();
                }
            });
            watchdog.play();

        } catch (Exception e) {
            System.err.println("Exception creating video: " + e.getMessage());
            e.printStackTrace();
            
            if (mediaPlayer != null) {
                try { mediaPlayer.dispose(); } catch (Exception ex) {}
            }
            if (videoStage != null && videoStage.isShowing()) {
                try { videoStage.close(); } catch (Exception ex) {}
            }
            
            videoIsShowing = false;
            if (onFinished != null) Platform.runLater(onFinished);
        }
    }

    // --- NETWORK HANDLERS ---
    private void onBoardUpdate(NetworkMessage msg) {
        if (isGameEnded || isNavigating) return;
        
        GameMoveDto move = client.getGson().fromJson(msg.getPayload(), GameMoveDto.class);
        Platform.runLater(() -> {
            if (isGameEnded || isNavigating) return;
            
            if (engine.makeMove(move.getRow(), move.getCol())) {
                updateButton(buttons[move.getRow()][move.getCol()], engine.getCurrentPlayer());
                engine.switchTurn();
                setBoardDisabled(false);
                statusLabel.setText("Your Turn");
            }
        });
    }

    private void onGameOver(NetworkMessage msg) {
        if (isGameEnded || isNavigating) return;
        
        String result = client.getGson().fromJson(msg.getPayload(), String.class);
        Platform.runLater(() -> {
            if (isGameEnded || isNavigating) return;
            
            isGameEnded = true;
            engine.setGameOver(true);
            setBoardDisabled(true);

            Player winner = engine.getWinner();
            int[] coords = engine.getWinningCoords();
            if (coords != null && winner != Player.NONE) {
                drawWinningLine(coords[0], coords[1]);
            }

            String lowerResult = result.toLowerCase();
            boolean isWin = lowerResult.contains("win");
            boolean isDraw = lowerResult.contains("draw") || lowerResult.contains("tie");

            if (isWin) {
                updateScore(mySymbol);
                playVideoAndThen(true, () -> navigateToPlayersBoard());
            } else if (isDraw) {
                playDrawVideoAndThen(() -> navigateToPlayersBoard());
            } else {
                updateScore(mySymbol == Player.X ? Player.O : Player.X);
                playVideoAndThen(false, () -> navigateToPlayersBoard());
            }
        });
    }

    private void onOpponentLeft(NetworkMessage msg) {
        if (isGameEnded || isNavigating) return;
        
        String message = client.getGson().fromJson(msg.getPayload(), String.class);
        Platform.runLater(() -> {
            if (isGameEnded || isNavigating) return;
            
            isGameEnded = true;
            engine.setGameOver(true);
            updateScore(mySymbol);
            
            playVideoAndThen(true, () -> {
                App.showInfo("You Win!", message);
                navigateToPlayersBoard();
            });
        });
    }

    private void handleOnlineMove(int[] coords) {
        try {
            gameApi.sendGameMove(opponentName, coords[0], coords[1]);
            setBoardDisabled(true);
            statusLabel.setText(opponentName + "'s Turn");
        } catch (Exception ex) {
            ex.printStackTrace();
            App.showError("Network Error", "Failed to send move");
        }
    }

    // --- GAME SETUP ---
    private void startNewGame() {
        isGameEnded = false;
        isNavigating = false;
        
        RecordedGameDetails replay = App.getRecordedGameDetails();
        if (replay != null) {
            startReplay(replay);
            App.setRecordedGameDetails(null);
            return;
        }

        if (currentMode == GameMode.withFriend) setupOnlineGame();
        else setupLocalOrComputerGame();
    }

    private void setupOnlineGame() {
        client.on(MessageType.UPDATE_BOARD, this::onBoardUpdate);
        client.on(MessageType.GAME_OVER, this::onGameOver);
        client.on(MessageType.OPPONENT_LEFT, this::onOpponentLeft);
        
        this.opponentName = sessionManager.getOpponentName();
        this.mySymbol = sessionManager.isMyTurnFirst() ? Player.X : Player.O;
        
        playerNameX.setText(mySymbol == Player.X ? sessionManager.getUserName() : opponentName);
        playerNameO.setText(mySymbol == Player.O ? sessionManager.getUserName() : opponentName);
        
        engine.resetGame(Player.X);
        resetBoardUI();
        setBoardDisabled(engine.getCurrentPlayer() != mySymbol);
        statusLabel.setText(engine.getCurrentPlayer() == mySymbol ? "Your Turn" : opponentName + "'s Turn");
    }

    private void setupLocalOrComputerGame() {
        playerNameX.setText(sessionManager.getUserName());
        playerNameO.setText(sessionManager.getOpponentName());
        if (currentMode == GameMode.vsComputer) {
            engine.difficulty = OnePlayerPopupController.difficulty;
        }
        engine.resetGame(Player.X);
        resetBoardUI();
        setBoardDisabled(false);
        updateStatusLabelForLocal();
    }

    private void startReplay(RecordedGameDetails recordedGame) {
        setPlayersName(recordedGame.getPlayerXName(), recordedGame.getPlayerOName());
        setBoardDisabled(true);
        statusLabel.setText("Replaying game...");
        resetBoardUI();
        Timeline timeline = new Timeline();
        for (int i = 0; i < recordedGame.getMoves().size(); i++) {
            final MoveRecord move = recordedGame.getMoves().get(i);
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i + 1), e -> {
                updateButton(buttons[move.getRow()][move.getCol()], 
                    move.getPlayer() == PlayerType.X ? Player.X : Player.O);
            }));
        }
        timeline.setOnFinished(e -> statusLabel.setText("Replay finished."));
        timeline.play();
    }

    // --- NAVIGATION & CLEANUP ---
    private void navigateToPlayersBoard() {
        if (isNavigating) return;
        isNavigating = true;
        
        cleanupOnlineListeners();
        sessionManager.clearSession();
        Navigation.navigateTo(Navigation.playersBoardPage);
    }

    private void quitGame() {
        if (isNavigating) return;
        isNavigating = true;
        
        cleanupOnlineListeners();
        sessionManager.clearSession();
        Navigation.navigateTo(currentMode == GameMode.withFriend 
            ? Navigation.playersBoardPage 
            : Navigation.homePage);
    }

    private void cleanupOnlineListeners() {
        if (currentMode == GameMode.withFriend) {
            client.off(MessageType.UPDATE_BOARD, this::onBoardUpdate);
            client.off(MessageType.GAME_OVER, this::onGameOver);
            client.off(MessageType.OPPONENT_LEFT, this::onOpponentLeft);
        }
    }

    private void showPlayAgainPopup(String message) {
        if (currentMode == GameMode.withFriend) {
            navigateToPlayersBoard();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/mycompany/tictactoeclient/PlayAgainPopup.fxml")
            );
            Parent root = loader.load();
            PlayAgainPopupController controller = loader.getController();
            controller.setWinnerName(message);
            controller.setOnPlayAgain(() -> {
                isGameEnded = false;
                setupLocalOrComputerGame();
            });
            controller.setOnBack(this::quitGame);
            
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, Color.TRANSPARENT));
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // --- UI HELPERS ---
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

    private void updateButton(Button btn, Player p) {
        btn.setText(p.toString());
        btn.getStyleClass().add(p == Player.X ? "tile-x" : "tile-o");
    }

    private void updateScore(Player w) {
        if (w == Player.X) scoreXLabel.setText(String.valueOf(++xScore));
        else if (w == Player.O) scoreOLabel.setText(String.valueOf(++oScore));
    }

    private void updateStatusLabelForLocal() {
        statusLabel.setText((engine.getCurrentPlayer() == Player.X 
            ? playerNameX.getText() 
            : playerNameO.getText()) + "'s Turn");
    }

    private void drawWinningLine(int startIdx, int endIdx) {
        Button start = buttons[startIdx / 3][startIdx % 3];
        Button end = buttons[endIdx / 3][endIdx % 3];
        winningLine.setStartX(start.getLayoutX() + start.getWidth() / 2);
        winningLine.setStartY(start.getLayoutY() + start.getHeight() / 2);
        winningLine.setEndX(end.getLayoutX() + end.getWidth() / 2);
        winningLine.setEndY(end.getLayoutY() + end.getHeight() / 2);
        winningLine.setVisible(true);
    }

    public void updateRecordingState(boolean recording) {
        if (recording) {
            recordingBox.setVisible(true);
            recordingBox.setManaged(true);
            blinkingTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> recordingDot.setVisible(!recordingDot.isVisible()))
            );
            blinkingTimeline.setCycleCount(Timeline.INDEFINITE);
            blinkingTimeline.play();
            engine.startRecording(playerNameX.getText(), playerNameO.getText(), 
                UserSession.getInstance().getUsername());
        } else {
            if (blinkingTimeline != null) blinkingTimeline.stop();
            recordingBox.setVisible(false);
            recordingBox.setManaged(false);
            engine.stopRecording();
        }
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        if (!engine.isGameOver() && currentMode == GameMode.withFriend) {
            Optional<ButtonType> result = App.showConfirmation(
                "Surrender Game?",
                "Are you sure you want to leave? You will lose this game."
            );

            if (result.isPresent() && result.get() == ButtonType.OK) {
                isGameEnded = true;
                try {
                    gameApi.sendSurrender();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                navigateToPlayersBoard();
            }
            
        } else {
            quitGame();
           
            
        }
        
    }

    private void setBoardDisabled(boolean disabled) {
        gameGrid.setDisable(disabled);
    }

    public void setPlayersName(String x, String o) {
        playerNameX.setText(x);
        playerNameO.setText(o);
    }
}