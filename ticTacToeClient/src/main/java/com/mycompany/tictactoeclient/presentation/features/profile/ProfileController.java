package com.mycompany.tictactoeclient.presentation.features.profile;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ProfileController implements Initializable {

    @FXML
    private Button backBtn;
    @FXML
    private Button changePassBtn;

    // --- Edit Buttons ---
    @FXML
    private Button usernameEditIconBtn;
    @FXML
    private Button emailEditIconBtn;

    // --- Fields ---
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField scoreTextField;

    @FXML
    private Button viewRecordedGamesBtn;

    // Image variables
    private Image editIcon;
    private Image saveEditIcon;
    @FXML
    private ImageView usernameEditIcon;
    @FXML
    private ImageView emailEditIcon;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 2. Load the images
        try {
            // "Edit" = on press allow changes
            editIcon = new Image(getClass().getResource("/icons/Edit-20.png").toExternalForm());
            // "Save Edit" = on press save changes
            saveEditIcon = new Image(getClass().getResource("/icons/Save-edit-01.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
        }
    }

    // --- STEP 2: The Magic Logic (Toggle Edit/Save) ---
    // Now accepts the 'Button' as a parameter to change its text
    private void toggleEditMode(TextField field, Button btn, ImageView icon) {
        if (!field.isEditable()) {
            // A. Switch to EDIT MODE
            field.setEditable(true);

            // Visuals: Editing Style
            field.getStyleClass().remove("text-field-readonly");
            field.getStyleClass().add("text-field-editing");

            // Logic: Focus
            field.requestFocus();
            field.positionCaret(field.getText().length());

            if (saveEditIcon != null) {
                icon.setImage(saveEditIcon);
            }

        } else {
            // B. Switch back to READ-ONLY (Save)
            field.setEditable(false);

            // Visuals: Ghost Style
            field.getStyleClass().remove("text-field-editing");
            field.getStyleClass().add("text-field-readonly");

            if (editIcon != null) {
                icon.setImage(editIcon);
            }
            // Logic: Save to Database
            // d.updateUser(field.getText()); // Example
            System.out.println("Saving new value: " + field.getText());
        }
    }

    // --- Event Handlers ---
    @FXML
    private void onEditUsername(ActionEvent event) {
        // Pass both the Field AND the Button
        toggleEditMode(usernameField, usernameEditIconBtn, usernameEditIcon);
    }

    @FXML
    private void onEditEmail(ActionEvent event) {
        // Pass both the Field AND the Button
        toggleEditMode(emailField, emailEditIconBtn, emailEditIcon);
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        // Navigation Logic
        /*
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
    }

    @FXML
    private void onChangePasswordClicked(ActionEvent event) {
        try {
            // 1. Load the ChangePassword FXML
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/tictactoeclient/changePassword.fxml"));

            // 2. Get the Stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Switch Scenes
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
        // Navigation Logic
    }
}
