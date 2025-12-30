package com.mycompany.tictactoeclient;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class PrimaryController implements Initializable {

    DataAccessLayer d = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        d = new DataAccessLayer();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

}
