package com.mycompany.tictactoeserver.presentation.features.home;

import com.mycompany.tictactoeserver.data.dataSource.dao.PlayerDaoImpl;
import com.mycompany.tictactoeserver.network.GameServer;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class HomeController implements Initializable {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private ToggleButton startToggleBtn;

    @FXML
    private ListView<String> logList;

    private GameServer server;
    private final PlayerDaoImpl DB = new PlayerDaoImpl();

    private XYChart.Data<String, Number> waitingData;
    private XYChart.Data<String, Number> inGameData;
    private XYChart.Data<String, Number> onlineData;
    private XYChart.Data<String, Number> offlineData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        server = GameServer.getInstance();
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        waitingData = new XYChart.Data<>("Waiting", 0);
        inGameData = new XYChart.Data<>("In Game", 0);
        onlineData = new XYChart.Data<>("Online", 0);
        offlineData = new XYChart.Data<>("Offline", 0);

        series.getData().addAll(waitingData, inGameData, onlineData, offlineData);
        barChart.getData().add(series);

        loadPlayerStats();

        Platform.runLater(() -> {
            waitingData.getNode().setStyle("-fx-bar-fill: #00FFFF;");
            inGameData.getNode().setStyle("-fx-bar-fill: #4E0585;");
            onlineData.getNode().setStyle("-fx-bar-fill: #FF00FF;");
            offlineData.getNode().setStyle("-fx-bar-fill: #888888;");

            barChart.getXAxis().lookupAll(".tick-label")
                    .forEach(n -> n.setStyle("-fx-text-fill: white;"));
            barChart.getYAxis().lookupAll(".tick-label")
                    .forEach(n -> n.setStyle("-fx-text-fill: white;"));
        });
    }

    @FXML
    private void onToggaleBtnClicked(ActionEvent event) {

        if (startToggleBtn.isSelected()) {
            startToggleBtn.setDisable(true);
            uiLog("Server starting...");

            new Thread(() -> {
                try {
                    server.start();
                    Platform.runLater(() -> {
                        startToggleBtn.setText("Stop");
                        startToggleBtn.setDisable(false);
                        uiLog("Server started successfully");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        startToggleBtn.setSelected(false);
                        startToggleBtn.setText("Start");
                        startToggleBtn.setDisable(false);
                        uiLog("Server crashed: " + e.getMessage());
                    });
                }
            }).start();

        } else {
            server.stop();
            startToggleBtn.setText("Start");
            uiLog("Server stopped");
        }
    }

    @FXML
    private void onSeeDetailsClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mycompany/tictactoeserver/players_board.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            uiLog("Failed to load Players Screen");
        }
    }

    private void loadPlayerStats() {
        new Thread(() -> {
            int waiting = DB.getPlayersCountBasedOnState(3);
            int inGame = DB.getPlayersCountBasedOnState(2);
            int online = DB.getPlayersCountBasedOnState(1);
            int offline = DB.getPlayersCountBasedOnState(0);

            Platform.runLater(() -> {
                waitingData.setYValue(waiting);
                inGameData.setYValue(inGame);
                onlineData.setYValue(online);
                offlineData.setYValue(offline);
            });
        }).start();
    }

    private void uiLog(String message) {
        Platform.runLater(() -> logList.getItems().add(message));
    }
}
