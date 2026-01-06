/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.game_board;

import com.mycompany.tictactoeclient.presentation.features.game_board.GameEngine.Player;
import com.mycompany.tictactoeclient.presentation.features.home.OnePlayerPopupController;
import java.io.IOException;
import java.net.URL;
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
    private boolean isRecorded;

    private GameEngine.Player nextStarter = GameEngine.Player.X;
    private Button[][] buttons = new Button[3][3];
    private GameEngine engine;
    private boolean isVsComputer;
    private int xScore = 0;
    private int oScore = 0;
    private Timeline blinkingTimeline;

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
        engine.difficulty = OnePlayerPopupController.difficulty;
        startNewGame();
    }

    public void setPlayersName(String playerX, String PlayerO) {
        playerNameX.setText(playerX);
        playerNameO.setText(PlayerO);
        statusLabel.setText(playerNameX.getText() + " Turn");
    }

    public void setGameMode(boolean isVsComputer) {
        this.isVsComputer = isVsComputer;
    }

    public void setIsRecorded(boolean isRecorded) {
        this.isRecorded = isRecorded;
        System.out.println("is Recorded = " + isRecorded);
        
        if (isRecorded) {
            startRecordingUI();

        } else {
            stopRecordingUI();
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
        if (isVsComputer) {
            if (nextStarter == Player.X) {
                statusLabel.setText(playerNameX.getText() + " Turn");
            }
        } else {
            if (nextStarter == Player.X) {
                statusLabel.setText(playerNameX.getText() + " Turn");
            } else {
                statusLabel.setText(playerNameO.getText() + " Turn");
            }
        }
        winningLine.setVisible(false);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].getStyleClass().removeAll("tile-x", "tile-o", "tile-winning");
                buttons[i][j].setDisable(false);
            }
        }
        setBoardDisabled(false);
        if (isVsComputer && nextStarter == GameEngine.Player.O) {
            statusLabel.setText("Computer is thinking...");
            setBoardDisabled(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
            pause.setOnFinished(e -> performComputerMove(null));
            pause.play();
        }

    }

    private void handlePlayerMove(ActionEvent event) {
        if (engine.isGameOver()) {
            return;
        }

        Button clickedButton = (Button) event.getSource();
        int[] coords = (int[]) clickedButton.getUserData();
        if (engine.makeMove(coords[0], coords[1])) {
            updateButton(clickedButton, engine.getCurrentPlayer());
            if (checkGameStatus(event)) {
                return;
            }
            engine.switchTurn();
            if (isVsComputer && engine.getCurrentPlayer() == GameEngine.Player.O) {
                setBoardDisabled(true);
                statusLabel.setText("Computer is thinking...");
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
                pause.setOnFinished(e -> performComputerMove(event));
                pause.play();
            } else {
                if (engine.getCurrentPlayer() == Player.X) {
                    statusLabel.setText(playerNameX.getText() + " Turn");
                } else {
                    statusLabel.setText(playerNameO.getText() + " Turn");
                }
            }
        }
    }

    private void performComputerMove(ActionEvent event) {
        if (engine.isGameOver()) {
            return;
        }

        int[] move = engine.getComputerMove();
        if (move != null) {
            engine.makeMove(move[0], move[1]);
            updateButton(buttons[move[0]][move[1]], engine.getCurrentPlayer());
            if (!checkGameStatus(event)) {
                engine.switchTurn();
                statusLabel.setText(playerNameX.getText() + " Turn");
                setBoardDisabled(false);
            }
        }
    }

    private void updateButton(Button btn, GameEngine.Player player) {
        btn.setText(player.toString());
        btn.getStyleClass().add(player == GameEngine.Player.X ? "tile-x" : "tile-o");
    }

    private boolean checkGameStatus(ActionEvent event) {
        GameEngine.Player winner = engine.getWinner();
        if (winner != GameEngine.Player.NONE) {
            engine.setGameOver(true);
            nextStarter = winner;
            String winnerName;
            if (winner == GameEngine.Player.X) {
                winnerName = playerNameX.getText();
            } else {
                winnerName = playerNameO.getText();
            }
            statusLabel.setText("Winner: " + winnerName + "!");
            updateScore(winner);
            setBoardDisabled(true);
            int[] coords = engine.getWinningCoords();
            if (coords != null) {
                drawWinningLine(coords[0], coords[1]);
            }
            showEndGamePopup(winnerName + " Wins!", event);

            return true;
        }

        if (engine.isBoardFull()) {
            engine.setGameOver(true);
            statusLabel.setText("It's a Draw!");
            showEndGamePopup("It's a Draw!", event);

            return true;
        }
        return false;
    }

    private void showEndGamePopup(String message, ActionEvent event) {
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(e -> {
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/PlayAgainPopup.fxml"));
                    Parent root = loader.load();

                    PlayAgainPopupController popupController = loader.getController();

                    popupController.setWinnerName(message);
                    popupController.setOnPlayAgain(() -> startNewGame());
                    popupController.setOnBack(() -> onBackClicked(event));
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.setScene(new Scene(root, Color.TRANSPARENT));
                    Stage mainStage = (Stage) gameGrid.getScene().getWindow();
                    stage.initOwner(mainStage);
                    double x = mainStage.getX() + (mainStage.getWidth() / 2) - 175;
                    double y = mainStage.getY() + (mainStage.getHeight() / 2) - 125;
                    stage.setX(x);
                    stage.setY(y);

                    stage.showAndWait();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        });

        delay.play();
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
        winningLine.setStroke(Color.BLUEVIOLET);
        winningLine.setStrokeWidth(4.0);
        winningLine.setVisible(true);
    }

    private void updateScore(GameEngine.Player winner) {
        if (winner == GameEngine.Player.X) {
            xScore++;
            scoreXLabel.setText("" + xScore);
        } else {
            oScore++;
            scoreOLabel.setText("" + oScore);
        }
    }

    private void setBoardDisabled(boolean disable) {
        gameGrid.setDisable(disable);
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gameGrid.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
