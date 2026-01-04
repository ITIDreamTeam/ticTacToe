/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.playersboard;

/**
 *
 * @author yasse
 */
import com.mycompany.tictactoeclient.data.models.Player;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import java.io.IOException;
import javafx.geometry.Pos;

public class PlayerListCell extends ListCell<Player> {
    private Node graphic;
    private Player_cardController controller;

    public PlayerListCell() {
        setAlignment(Pos.CENTER);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/tictactoeclient/player_card.fxml"));
            graphic = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            setText("Error loading card");
        }
    }

    @Override
    protected void updateItem(Player player, boolean empty) {
        super.updateItem(player, empty);

        if (empty || player == null) {
            setText(null);
            setGraphic(null);
        } else {
            controller.setPlayerData(player);
            setGraphic(graphic);
        }
    }
}