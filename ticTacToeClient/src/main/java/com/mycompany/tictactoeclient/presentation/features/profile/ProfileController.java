package com.mycompany.tictactoeclient.presentation.features.profile;

import com.mycompany.tictactoeclient.App;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.regex.Pattern;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ProfileController implements Initializable {

    @FXML
    private Button backBtn;
    @FXML
    private Button changePassBtn;
    @FXML
    private Button usernameEditIconBtn;
    @FXML
    private Button emailEditIconBtn;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField scoreTextField;

    @FXML
    private Button viewRecordedGamesBtn;
    private Image editIcon;
    private Image saveEditIcon;
    @FXML
    private ImageView usernameEditIcon;
    @FXML
    private ImageView emailEditIcon;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            editIcon = new Image(getClass().getResource("/icons/Edit-20.png").toExternalForm());
            saveEditIcon = new Image(getClass().getResource("/icons/Save-edit-01.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
        }
    }

    private void toggleEditMode(TextField field, Button btn, ImageView icon) {
        if (!field.isEditable()) {
            field.setEditable(true);
            field.getStyleClass().remove("text-field-readonly");
            field.getStyleClass().add("text-field-editing");
            field.requestFocus();
            field.positionCaret(field.getText().length());
            if (saveEditIcon != null) {
                icon.setImage(saveEditIcon);
            }
        } else {
            if (isInputValid()) {
                field.setEditable(false);
                field.getStyleClass().remove("text-field-editing");
                field.getStyleClass().add("text-field-readonly");

                if (editIcon != null) {
                    icon.setImage(editIcon);
                }
                System.out.println("Saving new value: " + field.getText());
            }
        }
    }

    @FXML
    private void onEditUsername(ActionEvent event) {
        toggleEditMode(usernameField, usernameEditIconBtn, usernameEditIcon);

    }

    @FXML
    private void onEditEmail(ActionEvent event) {
        toggleEditMode(emailField, emailEditIconBtn, emailEditIcon);
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        try {
            App.setRoot("home");

        } catch (IOException e) {
            System.err.println("Error loading Change Password screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onChangePasswordClicked(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/tictactoeclient/changePassword.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading Change Password screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onViewRecordedGames(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/tictactoeclient/recordedGames.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading Profile screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isInputValid() {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        String nameRegex = "^[a-zA-Z\\s'-]+$";
        if (!Pattern.matches(emailRegex, emailField.getText())) {
            showAlert("Invalid Email", "Please enter a valid email address (e.g., name@example.com).");
            return false;
        }

        if (!Pattern.matches(nameRegex, usernameField.getText())) {
            showAlert("Missing Data", "Username is required\n can contain only letters or - or ' or space.");
            return false;
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
