/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.shared;

import com.mycompany.tictactoeclient.App;
import java.io.IOException;

/**
 *
 * @author Basmala
 */
public class Navigation {
    public static String homePage="home";
    public static String recordedGamesPage="RecordedGames";
    public static String changePasswordPage="changePassword";
    public static String gameBoardPage="game_board";
    public static String loginPage="login";
    public static String playersBoardPage="players_board";
    public static String profilePage="profile";
    public static String registerPage="register";
    public static String playAgainPopup="PlayAgainPopup";

    public static void navigateTo(String page) {
        try {
            App.setRoot(page);
        } catch (IOException e) {
            e.printStackTrace();
            App.showError("Navigation Error", "Cannot navigate to register page.");
        }
    }
}
