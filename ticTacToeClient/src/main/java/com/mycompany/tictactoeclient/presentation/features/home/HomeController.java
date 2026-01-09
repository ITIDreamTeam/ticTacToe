package com.mycompany.tictactoeclient.presentation.features.home;

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
import com.mycompany.tictactoeclient.network.NetworkClient;
import com.mycompany.tictactoeclient.shared.Navigation;

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

    private final UserSession session = UserSession.getInstance();
    private final NetworkClient client = NetworkClient.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupButtonHoverEffects();
        updateUI();
    }

    private void updateUI() {
        if (session.isLoggedIn()) {
            firstHyperlink.setText(session.getUsername());
            secondHyperlink.setText("Logout");
            if (session.isOnline()) {
                withAFriendButton.setDisable(false);
            } else {
                withAFriendButton.setDisable(true);
            }
        } else {
            firstHyperlink.setText("Login");
            secondHyperlink.setText("Register");
            withAFriendButton.setDisable(true);
        }
    }

    @FXML
    private void onWithAFriendButton(ActionEvent event) {
        if (!session.isLoggedIn() || !session.isOnline()) {
            App.showWarning("Login Required",
                    "You must be logged in and connected to play online.");
            Navigation.navigateTo(Navigation.loginPage);
            return;
        }

        client.sendFindMatchRequest();

        Alert waitingAlert = new Alert(Alert.AlertType.INFORMATION);
        waitingAlert.setTitle("Matchmaking");
        waitingAlert.setHeaderText(null);
        waitingAlert.setContentText("Waiting for an opponent...");
        waitingAlert.show();
        App.setWaitingAlert(waitingAlert);
    }

    @FXML
    private void onFirstHyperlink(ActionEvent event) {
        if (session.isLoggedIn()) {
            Navigation.navigateTo(Navigation.profilePage);
        } else {
            Navigation.navigateTo(Navigation.loginPage);
        }
    }

    @FXML
    private void onSecondHyperlink(ActionEvent event) {
        if (session.isLoggedIn()) {
            handleLogout();
        } else {
            Navigation.navigateTo(Navigation.registerPage);
        }
    }

    @FXML
    public void onTwoPlayerButton(ActionEvent event) {
        showPopup("two-player-popup.fxml", "Two Player Game Setup");
    }

    @FXML
    public void onOnePlayerButton(ActionEvent event) {
        showPopup("one-player-popup.fxml", "One Player Game Setup");
    }

    private void handleLogout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Logout");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to logout?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                session.logout();
                App.showInfo("Logged Out", "You have been logged out successfully.");
                updateUI();
            }
            UserSession.getInstance().logout();
        });   
    }

    private void showPopup(String fxmlFile, String title) {
        try {
            URL fxmlUrl = findFXMLResource(fxmlFile);

            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML file: " + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(App.getPrimaryStage());
            popupStage.setResizable(false);

            Scene scene = new Scene(root);
            loadCSS(scene);
            Object controller = loader.getController();
            setStageInController(controller, popupStage);

            popupStage.setScene(scene);
            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            App.showError("Error", "Cannot load popup: " + e.getMessage());
        }
    }

    private URL findFXMLResource(String fxmlFile) {
        URL url = getClass().getResource("../../../../" + fxmlFile);
        if (url != null) {
            return url;
        }
        url = getClass().getResource("/com/mycompany/tictactoeclient/" + fxmlFile);
        if (url != null) {
            return url;
        }
        return getClass().getResource("/" + fxmlFile);
    }

    private void loadCSS(Scene scene) {
        try {
            URL cssUrl = getClass().getResource("../../../../styles/style.css");
            if (cssUrl == null) {
                cssUrl = getClass().getResource("/styles/style.css");
            }
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Could not load CSS: " + e.getMessage());
        }
    }

    private void setStageInController(Object controller, Stage stage) {
        if (controller instanceof TwoPlayerPopupController) {
            ((TwoPlayerPopupController) controller).setStage(stage);
        } else if (controller instanceof OnePlayerPopupController) {
            ((OnePlayerPopupController) controller).setStage(stage);
        }
    }

    private void setupButtonHoverEffects() {
        Button[] buttons = {onePlayerButton, twoPlayerButton, withAFriendButton};
        for (Button btn : buttons) {
            btn.setOnMouseEntered(e
                    -> btn.setStyle("-fx-background-color: #FF00FF; -fx-text-fill: white;"));
            btn.setOnMouseExited(e
                    -> btn.setStyle("-fx-background-color: #4E0585; -fx-text-fill: white;"));
        }
    }
}
