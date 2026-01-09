package com.mycompany.tictactoeclient.presentation.features.profile;

import com.mycompany.tictactoeclient.data.models.userSession.UserSession;
import com.mycompany.tictactoeclient.shared.Navigation;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
        usernameField.setText(UserSession.getInstance().getUsername());
        emailField.setText(UserSession.getInstance().getEmail());
        scoreTextField.setText(UserSession.getInstance().getScore()+"");
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
            field.positionCaret(
                field.getText() == null ? 0 : field.getText().length()
            );
            if (saveEditIcon != null) {
                icon.setImage(saveEditIcon);
            }
        } else {
            if (isInputValid(field)) {
                field.setEditable(false);
                field.getStyleClass().remove("text-field-editing");
                field.getStyleClass().add("text-field-readonly");
                
                String newValue = field.getText();
                if (field == usernameField) {
                    UserSession.getInstance().setUsername(newValue);
                    //send to database
                } else if (field == emailField) {
                    UserSession.getInstance().setEmail(newValue);
                }
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
