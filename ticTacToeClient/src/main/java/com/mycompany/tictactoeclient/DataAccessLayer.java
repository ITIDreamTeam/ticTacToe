/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient;

/**
 *
 * @author Basmala
 */
import javafx.scene.control.*;
import java.sql.*;

public class DataAccessLayer {

    private Connection connection;
    private ResultSet resultSet;

    public DataAccessLayer() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/ticTacToe",
                    "root",
                    "root"
            );
            System.out.println("************************************************Connected to database***************************************");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   
}
