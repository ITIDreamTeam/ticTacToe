/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeserver.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Nadin
 */

public class TestDBConnection {

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            if (con != null && !con.isClosed()) {
                System.out.println("Connected to database successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Failed to connect to database");
            e.printStackTrace();
        }
    }
}

