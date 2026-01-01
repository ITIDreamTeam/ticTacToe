/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.game_board;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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

    private Button[][] buttons = new Button[3][3];
    private GameEngine engine;
    private boolean isVsComputer = true;
    private int xScore = 0;
    private int oScore = 0;

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
        startNewGame();
    }

    private void startNewGame() {
        engine.resetGame();
        statusLabel.setText("Player X Turn");
        winningLine.setVisible(false);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].getStyleClass().removeAll("tile-x", "tile-o", "tile-winning");
                buttons[i][j].setDisable(false);
            }
        }
        setBoardDisabled(false);
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
                statusLabel.setText("Player " + engine.getCurrentPlayer() + " Turn");
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
                statusLabel.setText("Player " + engine.getCurrentPlayer() + " Turn");
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
            statusLabel.setText("Winner: " + winner + "!");
            updateScore(winner);
            setBoardDisabled(true);
            int[] coords = engine.getWinningCoords();
            if (coords != null) {
                drawWinningLine(coords[0], coords[1]);
            }
            showEndGamePopup("Player " + winner + " Wins!",event);

            return true;
        }

        if (engine.isBoardFull()) {
            engine.setGameOver(true);
            statusLabel.setText("It's a Draw!");
            showEndGamePopup("It's a Draw!",event);

            return true;
        }
        return false;
    }

    private void showEndGamePopup(String message,ActionEvent event) {
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
            scoreXLabel.setText("Player X: " + xScore);
        } else {
            oScore++;
            scoreOLabel.setText("Player O: " + oScore);
        }
    }

    private void setBoardDisabled(boolean disable) {
        gameGrid.setDisable(disable);
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/tictactoeclient/home.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Change Password screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
