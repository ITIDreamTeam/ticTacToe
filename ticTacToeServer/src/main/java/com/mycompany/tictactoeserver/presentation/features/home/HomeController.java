/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.tictactoeserver.presentation.features.home;

import com.mycompany.tictactoeserver.data.dataSource.dao.PlayerDaoImpl;
import com.mycompany.tictactoeserver.network.AuthService;
import com.mycompany.tictactoeserver.network.GameServer;
import com.mycompany.tictactoeserver.network.MessageRouter;
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
/**
 * FXML Controller class
 *
 * @author Nadin
 */
public class HomeController implements Initializable {


    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private ToggleButton startToggleBtn;
    @FXML
    private ListView<String> logList;
     private GameServer server;
     private boolean isServerRunning = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        server = GameServer.getInstance();
        
        barChart.setLegendVisible(false); 
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        /*Dummy Data*/
        XYChart.Data<String, Number> inGameData = new XYChart.Data<>("In Game", 45);
        XYChart.Data<String, Number> onlineData = new XYChart.Data<>("Online", 120);
        XYChart.Data<String, Number> offlineData = new XYChart.Data<>("Offline", 80);

        series.getData().addAll(inGameData, onlineData, offlineData);

        barChart.getData().add(series);

        /*add colors to Bars of Chart*/
        /*didn't work when I try to load it from css file, it also must set after addimg data*/
        inGameData.getNode().setStyle("-fx-bar-fill: #4E0585;");   // Purple
        onlineData.getNode().setStyle("-fx-bar-fill: #FF00FF;");  // Neon Pink
        offlineData.getNode().setStyle("-fx-bar-fill: #888888;");   // Grey
        
        Platform.runLater(() -> {
        barChart.getXAxis().lookupAll(".tick-label").forEach(node -> node.setStyle("-fx-text-fill: white;"));
        barChart.getYAxis().lookupAll(".tick-label").forEach(node -> node.setStyle("-fx-text-fill: white;"));
        });
    }    
    
@FXML
    private void onToggaleBtnClicked(ActionEvent event) {
       if (startToggleBtn.isSelected()) {
        startToggleBtn.setText("Stop");
        new Thread(() -> {
            try {
                uiLog("Server starting...");
                server.start(); 
            } catch (Exception ex) {
                ex.printStackTrace();
                uiLog("Server crashed: " + ex.getMessage());
                Platform.runLater(() -> {
                    startToggleBtn.setSelected(false);
                    startToggleBtn.setText("Start");
                });
            }
        }).start();
    } else {
        startToggleBtn.setText("Start");
        server.stop();
        uiLog("Server stopped");
    }
    }

    @FXML
    private void onSeeDetailsClicked(ActionEvent event) {
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeserver/players_board.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to load Players Screen FXML");
            }
        }
    private void uiLog(String s) {
        Platform.runLater(() -> logList.getItems().add(s));
    }
}
