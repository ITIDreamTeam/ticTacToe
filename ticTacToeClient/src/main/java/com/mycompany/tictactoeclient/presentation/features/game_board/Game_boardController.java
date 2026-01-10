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

    public GameMode currentMode;
    private Player mySymbol;
    private String opponentName;

    private final NetworkClient client = NetworkClient.getInstance();
    private final GameApi gameApi = new GameApi(client);
    private final GameSessionManager sessionManager = GameSessionManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        if (engine.isGameOver()) return;

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

    private boolean checkLocalGameStatus() {
        Player winner = engine.getWinner();
        
        if (winner != Player.NONE) {
            engine.setGameOver(true);
            updateScore(winner);
            int[] coords = engine.getWinningCoords();
            if (coords != null) drawWinningLine(coords[0], coords[1]);

            String wName = (winner == Player.X) ? playerNameX.getText() : playerNameO.getText();
            
            boolean playWinVideo = false;
            
            if (currentMode == GameMode.vsComputer) {
                playWinVideo = (winner == Player.X);
            } else if (currentMode == GameMode.twoPlayer) {
                playWinVideo = true;
            }

            playVideoAndThen(playWinVideo, () -> showPlayAgainPopup(wName + " Wins!"));
            return true;
        } 

        if (engine.isBoardFull()) {
            engine.setGameOver(true);
            
            // Play draw video (null = draw)
            playDrawVideoAndThen(() -> showPlayAgainPopup("It's a Draw!"));
            return true;
        }

        return false;
    }

    private void playVideoAndThen(boolean isWin, Runnable onFinished) {
        if (videoIsShowing) return;
        videoIsShowing = true;

        String videoFile = isWin ? "/videos/win.mp4" : "/videos/lose.mp4";
        URL resource = getClass().getResource(videoFile);

        if (resource == null) {
            System.err.println("Video file not found: " + videoFile);
            videoIsShowing = false;
            onFinished.run();
            return;
        }

        playVideo(resource, onFinished);
    }

    private void playDrawVideoAndThen(Runnable onFinished) {
        if (videoIsShowing) return;
        videoIsShowing = true;

        String videoFile = "/videos/draw.mp4";
        URL resource = getClass().getResource(videoFile);

        if (resource == null) {
            System.err.println("Draw video not found, using lose video as fallback");
            resource = getClass().getResource("/videos/lose.mp4");
        }

        if (resource == null) {
            System.err.println("No video available");
            videoIsShowing = false;
            onFinished.run();
            return;
        }

        playVideo(resource, onFinished);
    }

    private void playVideo(URL videoResource, Runnable onFinished) {
        Media media = new Media(videoResource.toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(400);
        mediaView.setFitHeight(250);

        StackPane root = new StackPane(mediaView);
        root.setStyle("-fx-background-color: black; -fx-border-color: #3fefef; -fx-border-width: 2;");

        Stage videoStage = new Stage(StageStyle.UNDECORATED);
        videoStage.initModality(Modality.APPLICATION_MODAL);
        Stage owner = (Stage) gameGrid.getScene().getWindow();
        videoStage.initOwner(owner);
        videoStage.setScene(new Scene(root, 400, 250));

        Runnable cleanUp = () -> {
            if (videoStage.isShowing()) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                videoStage.close();
                videoIsShowing = false;
                Platform.runLater(onFinished);
            }
        };

        mediaPlayer.setOnEndOfMedia(cleanUp);
        mediaPlayer.setOnError(cleanUp);
        mediaPlayer.setOnReady(() -> {
            videoStage.setX(owner.getX() + (owner.getWidth() / 2) - 200);
            videoStage.setY(owner.getY() + (owner.getHeight() / 2) - 125);
            videoStage.show();
            mediaPlayer.play();
        });

        PauseTransition watchdog = new PauseTransition(Duration.seconds(6));
        watchdog.setOnFinished(e -> { if(videoIsShowing) cleanUp.run(); });
        watchdog.play();
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
                    move.getPlayer() == com.mycompany.tictactoeclient.data.models.PlayerType.X ? Player.X : Player.O);
            }));
        }
        timeline.setOnFinished(e -> statusLabel.setText("Replay finished."));
        timeline.play();
    }

    private void startNewGame() {
        RecordedGameDetails replay = App.getRecordedGameDetails();
        if (replay != null) {
            startReplay(replay);
            App.setRecordedGameDetails(null);
            return;
        }

        if (currentMode == GameMode.withFriend) setupOnlineGame();
        else setupLocalOrComputerGame();
    }

    private void setupLocalOrComputerGame() {
        playerNameX.setText(sessionManager.getUserName());
        playerNameO.setText(sessionManager.getOpponentName());
        if (currentMode == GameMode.vsComputer) engine.difficulty = OnePlayerPopupController.difficulty;
        engine.resetGame(Player.X);
        resetBoardUI();
        setBoardDisabled(false);
        updateStatusLabelForLocal();
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
        statusLabel.setText((engine.getCurrentPlayer() == Player.X ? playerNameX.getText() : playerNameO.getText()) + "'s Turn");
    }

    // --- NETWORK HANDLERS ---
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
        String message = client.getGson().fromJson(msg.getPayload(), String.class);
        Platform.runLater(() -> {
            engine.setGameOver(true);
            updateScore(mySymbol);
            
            playVideoAndThen(true, () -> {
                App.showInfo("You Win!", message);
                navigateToPlayersBoard();
            });
        });
    }

    private void navigateToPlayersBoard() {
        cleanupOnlineListeners();
        sessionManager.clearSession();
        Navigation.navigateTo(Navigation.playersBoardPage);
    }

    private void quitGame() {
        cleanupOnlineListeners();
        sessionManager.clearSession();
        Navigation.navigateTo(currentMode == GameMode.withFriend ? Navigation.playersBoardPage : Navigation.homePage);
    }

    private void cleanupOnlineListeners() {
        if (currentMode == GameMode.withFriend) {
            client.off(MessageType.UPDATE_BOARD, this::onBoardUpdate);
            client.off(MessageType.GAME_OVER, this::onGameOver);
            client.off(MessageType.OPPONENT_LEFT, this::onOpponentLeft);
        }
    }

    private void showPlayAgainPopup(String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/PlayAgainPopup.fxml"));
            Parent root = loader.load();
            PlayAgainPopupController controller = loader.getController();
            controller.setWinnerName(message);
            controller.setOnPlayAgain(() -> setupLocalOrComputerGame());
            controller.setOnBack(this::quitGame);
            
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, Color.TRANSPARENT));
            stage.showAndWait();
        } catch (IOException ex) { 
            ex.printStackTrace();
        }
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
            blinkingTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> recordingDot.setVisible(!recordingDot.isVisible())));
            blinkingTimeline.setCycleCount(Timeline.INDEFINITE); 
            blinkingTimeline.play();
            engine.startRecording(playerNameX.getText(), playerNameO.getText(), UserSession.getInstance().getUsername());
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
                try { 
                    gameApi.sendSurrender();
                    System.out.println("Surrender sent to server");
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
