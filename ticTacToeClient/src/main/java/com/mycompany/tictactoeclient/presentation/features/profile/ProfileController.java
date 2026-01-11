package com.mycompany.tictactoeclient.presentation.features.profile;

import com.mycompany.tictactoeclient.network.UserSession;
import com.mycompany.tictactoeclient.shared.Navigation;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class ProfileController implements Initializable {

    @FXML
    private Button backBtn;
    @FXML
    private Button changePassBtn;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField scoreTextField;
    
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usernameField.setText(UserSession.getInstance().getUsername());
        emailField.setText(UserSession.getInstance().getEmail());
        scoreTextField.setText(UserSession.getInstance().getScore()+"");
    }


    @FXML
    private void onBackClicked(ActionEvent event) {
        Navigation.navigateTo(Navigation.homePage);
    }

    @FXML
    private void onChangePasswordClicked(ActionEvent event) {
        Navigation.navigateTo(Navigation.changePasswordPage);
    }

    @FXML
    private void onViewRecordedGames(ActionEvent event) {
        Navigation.navigateTo(Navigation.recordedGamesPage);
    }

    private boolean isInputValid(TextField field) {
        String text = field.getText();

        if (text == null || text.isBlank()) {
            showAlert("Missing Data", "Field cannot be empty.");
            return false;
        }

        if (field == emailField) {
            String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
            if (!text.matches(emailRegex)) {
                showAlert("Invalid Email", "Please enter a valid email address (e.g., name@example.com).");
                return false;
            }
        }

        if (field == usernameField) {
            String nameRegex = "^[a-zA-Z\\s'-]+$";
            if (!text.matches(nameRegex)) {
                showAlert("Invalid Username",
                        "Username can contain only letters, spaces, - or '");
                return false;
            }
        }

        return true;
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.setResizable(true);
        alert.showAndWait();
    }
}
