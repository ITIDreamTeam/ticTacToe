package com.mycompany.tictactoeclient.presentation.features.game_board;

import com.mycompany.tictactoeclient.App;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.network.NetworkMessage;
import com.mycompany.tictactoeclient.network.MessageType;
import com.mycompany.tictactoeclient.network.dtos.GameMoveDto;
import com.mycompany.tictactoeclient.network.dtos.GameStartDto;
import com.mycompany.tictactoeclient.presentation.features.game_board.GameEngine.Player;
import com.mycompany.tictactoeclient.presentation.features.home.OnePlayerPopupController;
import com.mycompany.tictactoeclient.shared.Navigation;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Game_boardController implements Initializable {

    @FXML private Label scoreXLabel;
    @FXML private Label scoreOLabel;
    @FXML private Label statusLabel;
    @FXML private GridPane gameGrid;
    @FXML private Line winningLine;
    @FXML private Pane linePane;
    @FXML private Label playerNameX;
    @FXML private Label playerNameO;
    @FXML public ImageView recordingIcon;

    public static enum GameMode {vsComputer, twoPlayer, withFriend};
    public static GameMode currentMode = GameMode.vsComputer;
    
    private GameEngine.Player nextStarter = GameEngine.Player.X;
    private Button[][] buttons = new Button[3][3];
    private GameEngine engine;
    private int xScore = 0;
    private int oScore = 0;

    private GameEngine.Player mySymbol;
    private String opponentName;
    private final NetworkClient client = NetworkClient.getInstance();

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
        
        client.on(MessageType.UPDATE_BOARD, this::onBoardUpdate);
        client.on(MessageType.GAME_OVER, this::onGameOver);
        client.on(MessageType.OPPONENT_LEFT, this::onOpponentLeft);

        if (currentMode != GameMode.withFriend) {
            engine.difficulty = OnePlayerPopupController.difficulty;
            startNewGame();
        }
    }

    public void setupOnlineGame(GameStartDto dto) {
        currentMode = GameMode.withFriend;
        this.opponentName = dto.getOpponentName();
        this.mySymbol = dto.isIsPlayerX() ? GameEngine.Player.X : GameEngine.Player.O;
        String myName = UserSession.getInstance().getUsername();
        
        if (dto.isIsPlayerX()) {
            setPlayersName(myName, opponentName);
        } else {
            setPlayersName(opponentName, myName);
        }
        
        startNewGame();
        
        if (engine.getCurrentPlayer() != mySymbol) {
            setBoardDisabled(true);
            statusLabel.setText(opponentName + "'s Turn");
        } else {
            setBoardDisabled(false);
            statusLabel.setText("Your Turn");
        }
    }
    
    public void setPlayersName(String playerX, String PlayerO) {
        playerNameX.setText(playerX);
        playerNameO.setText(PlayerO);
        statusLabel.setText(playerNameX.getText() + " Turn");
    }

    public static void setGameMode(GameMode mode) {
        currentMode = mode;
    }

    private void startNewGame() {
        engine.resetGame(nextStarter);
        if (currentMode == GameMode.vsComputer) {
            if (nextStarter == Player.X) {
                statusLabel.setText(playerNameX.getText() + " Turn");
            }
        } else if(currentMode != GameMode.withFriend) {
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
        if (currentMode == GameMode.vsComputer && nextStarter == GameEngine.Player.O) {
            statusLabel.setText("Computer is thinking...");
            setBoardDisabled(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
            pause.setOnFinished(e -> performComputerMove(null));
            pause.play();
        }
    }

    private void handlePlayerMove(ActionEvent event) {
        if (engine.isGameOver()) return;

        if (currentMode == GameMode.withFriend) {
            if (engine.getCurrentPlayer() != mySymbol) {
                return;
            }
        }

        Button clickedButton = (Button) event.getSource();
        int[] coords = (int[]) clickedButton.getUserData();
        
        if (engine.makeMove(coords[0], coords[1])) {
            updateButton(clickedButton, engine.getCurrentPlayer());
            
            if (currentMode == GameMode.withFriend) {
                client.sendGameMove(new GameMoveDto(coords[0], coords[1]));
                setBoardDisabled(true);
                statusLabel.setText(opponentName + "'s Turn");
                return; // Online game status is checked by server
            }

            if (checkGameStatus(event)) return;
            
            engine.switchTurn();
            
            if (currentMode == GameMode.vsComputer && engine.getCurrentPlayer() == GameEngine.Player.O) {
                setBoardDisabled(true);
                statusLabel.setText("Computer is thinking...");
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
                pause.setOnFinished(e -> performComputerMove(event));
                pause.play();
            } else {
                statusLabel.setText(engine.getCurrentPlayer() == Player.X ? playerNameX.getText() + " Turn" : playerNameO.getText() + " Turn");
            }
        }
    }

    private void performComputerMove(ActionEvent event) {
        if (engine.isGameOver()) return;

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
            handleEndOfGame(winner, event);
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

    private void handleEndOfGame(Player winner, ActionEvent event) {
        engine.setGameOver(true);
        nextStarter = winner;
        String winnerName = (winner == Player.X) ? playerNameX.getText() : playerNameO.getText();
        statusLabel.setText("Winner: " + winnerName + "!");
        updateScore(winner);
        setBoardDisabled(true);
        int[] coords = engine.getWinningCoords();
        if (coords != null) {
            drawWinningLine(coords[0], coords[1]);
        }
        showEndGamePopup(winnerName + " Wins!", event);
    }
    
    private void onBoardUpdate(NetworkMessage msg) {
        GameMoveDto move = client.getGson().fromJson(msg.getPayload(), GameMoveDto.class);
        Platform.runLater(() -> applyOpponentMove(move));
    }

    private void applyOpponentMove(GameMoveDto move) {
        if (engine.makeMove(move.getRow(), move.getCol())) {
            updateButton(buttons[move.getRow()][move.getCol()], engine.getCurrentPlayer());
            engine.switchTurn(); // Server is the source of truth, but we need to switch turns locally for UI
            setBoardDisabled(false);
            statusLabel.setText("Your Turn");
        }
    }
    
    private void onGameOver(NetworkMessage msg) {
        String result = client.getGson().fromJson(msg.getPayload(), String.class);
        Platform.runLater(() -> {
            engine.setGameOver(true);
            statusLabel.setText(result);
            setBoardDisabled(true);
            showEndGamePopup(result, null);
        });
    }

    private void onOpponentLeft(NetworkMessage msg) {
        String message = "Your opponent has left the game.";
        Platform.runLater(() -> {
            engine.setGameOver(true);
            setBoardDisabled(true);
            App.showInfo("Opponent Left", message);
            showEndGamePopup("You Win!", null);
        });
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
                    if (currentMode == GameMode.withFriend) {
                        popupController.setOnPlayAgain(() -> {
                            Stage stage = (Stage) gameGrid.getScene().getWindow();
                            stage.close();
                            Navigation.navigateTo(Navigation.homePage);
                        });
                    } else {
                        popupController.setOnPlayAgain(() -> startNewGame());
                    }
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
        // Here we should also notify the server that we are leaving the game.
        // For now, just navigate home.
        if(currentMode == GameMode.withFriend && !engine.isGameOver()){
            client.disconnect(); // This will trigger opponent left on the other side.
        }
        Navigation.navigateTo(Navigation.homePage);
    }
    
    public void changeRecoringIconVisiablitiy(boolean vis){
        recordingIcon.setVisible(vis);
    }
}