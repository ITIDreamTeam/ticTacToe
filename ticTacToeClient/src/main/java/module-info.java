module com.mycompany.tictactoeclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires com.google.gson;
    requires javafx.graphics;
    requires javafx.media;
    opens com.mycompany.tictactoeclient.presentation.features.playersboard to javafx.fxml;
    opens com.mycompany.tictactoeclient.presentation.features.home to javafx.fxml;

     opens com.mycompany.tictactoeclient.presentation.features.change_password to javafx.fxml;
     opens com.mycompany.tictactoeclient.presentation.features.saved_replays to javafx.fxml;
    opens com.mycompany.tictactoeclient.presentation.features.profile to javafx.fxml;
    opens com.mycompany.tictactoeclient.presentation.features.login to javafx.fxml;
    opens com.mycompany.tictactoeclient.presentation.features.game_board to javafx.fxml;
    opens com.mycompany.tictactoeclient.presentation.features.register to javafx.fxml;
    
    opens com.mycompany.tictactoeclient.network.request to com.google.gson;
    opens com.mycompany.tictactoeclient.network to com.google.gson;
    opens com.mycompany.tictactoeclient.network.response to com.google.gson;
    opens com.mycompany.tictactoeclient.network.dtos to com.google.gson;
    opens com.mycompany.tictactoeclient.data.models to com.google.gson;
    opens com.mycompany.tictactoeclient to javafx.fxml;
    exports com.mycompany.tictactoeclient;
}
