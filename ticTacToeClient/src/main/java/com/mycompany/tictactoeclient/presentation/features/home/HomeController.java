package com.mycompany.tictactoeclient.presentation.features.home;

import com.mycompany.tictactoeclient.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.App;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author Basmala
 */
public class HomeController implements Initializable {

    @FXML
    private Button onePlayerButton;
    @FXML
    private Button twoPlayerButton;
    @FXML
    private Button withAFriendButton;
    @FXML
    private Hyperlink firstHyperlink;

    @FXML
    private Hyperlink secondHyperlink;

    private UserSession userSession;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userSession = UserSession.getInstance();
        setupButtonHoverEffects();
        updateLoginUI();
        System.out.print("Home again!!");

    }

    private void updateLoginUI() {
        if (userSession.isLoggedIn()) {
            firstHyperlink.setText("Basmala");
            secondHyperlink.setText("Logout");
        } else {
            firstHyperlink.setText("Login");
            secondHyperlink.setText("Register");
        }
    }

    @FXML
    private void onWithAFriendButton(ActionEvent event) {
        try {
            App.setRoot("players_board");
            System.out.println("Go to players board");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onFirstHyperlink(ActionEvent event) {

        if (firstHyperlink.getText().equals("Login")) {
            System.out.println("Login");
            navigateToLogin();

        } else {
            navigateToProfile();
        }

    }

    @FXML
    private void onSecondHyperlink(ActionEvent event) {

        if (secondHyperlink.getText().equals("Register")) {
            System.out.println("navigateToRegister");
            navigateToRegister();
        } else if (secondHyperlink.getText().equals("Logout")) {
            System.out.println("Logout");
            userSession.logout();
            updateLoginUI();
        } else {
            System.out.println("non");
        }

    }

    @FXML
    public void onTwoPlayerButton() {
        showPopup("two-player-popup.fxml", "Two Player Game Setup");
    }

    @FXML
    public void onOnePlayerButton() {
        showPopup("one-player-popup.fxml", "One Player Game Setup");
    }

    @FXML
    public void onUserNameButton() {
        showPopup("one-player-popup.fxml", "One Player Game Setup");
    }

    private void showPopup(String fxmlFile, String title) {
        try {
            System.out.println("Loading popup: " + fxmlFile);
            String relativePath = "../../../../" + fxmlFile;
            System.out.println("Trying relative path: " + relativePath);

            URL fxmlUrl = getClass().getResource(relativePath);
            if (fxmlUrl == null) {
                String absolutePath = "/com/mycompany/tictactoeclient/" + fxmlFile;
                System.out.println("Trying absolute path: " + absolutePath);
                fxmlUrl = getClass().getResource(absolutePath);
            }

            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML file: " + fxmlFile
                        + "\nTried: " + relativePath + " and "
                        + "/com/mycompany/tictactoeclient/" + fxmlFile);
            }

            System.out.println("Success! Found at: " + fxmlUrl);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            if (onePlayerButton != null && onePlayerButton.getScene() != null) {
                popupStage.initOwner(onePlayerButton.getScene().getWindow());
            }

            popupStage.setResizable(false);

            Scene scene = new Scene(root, Color.TRANSPARENT);

            try {
                URL cssUrl = getClass().getResource("../../../../styles/style.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS loaded from: " + cssUrl);
                } else {
                    System.out.println("CSS file not found");
                }
            } catch (Exception e) {
                System.err.println("Could not load CSS: " + e.getMessage());
            }

            Object controller = loader.getController();
            if (controller != null) {
                if (controller instanceof TwoPlayerPopupController) {
                    ((TwoPlayerPopupController) controller).setStage(popupStage);
                } else if (controller instanceof OnePlayerPopupController) {
                    ((OnePlayerPopupController) controller).setStage(popupStage);
                }
            }

            popupStage.setScene(scene);
            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot load popup");
            alert.setContentText("Error loading " + fxmlFile + ":\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    private void setupButtonHoverEffects() {
        Button[] buttons = {onePlayerButton, twoPlayerButton, withAFriendButton};
        for (Button btn : buttons) {
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #FF00FF; -fx-text-fill: white;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #4E0585; -fx-text-fill: white;"));
        }
    }

    private void navigateToRegister() {
        try {
            App.setRoot("register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToLogin() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToProfile() {
        try {

            App.setRoot("profile");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
