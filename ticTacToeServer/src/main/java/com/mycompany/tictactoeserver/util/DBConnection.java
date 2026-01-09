/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.derby.jdbc.ClientDriver;

/**
 *
 * @author Nadin
 */
public class DBConnection {
    private static final String URL = "jdbc:derby://localhost:1527/ticTacToe";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private DBConnection() {
        
    }
    public static Connection getConnection() throws SQLException {
            DriverManager.registerDriver(new ClientDriver());
            return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
