module com.mycompany.tictactoeserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires com.google.gson;
    requires derbyclient;
    opens com.mycompany.tictactoeserver to javafx.fxml;
    opens com.mycompany.tictactoeserver.presentation.features.home to javafx.fxml;
    opens com.mycompany.tictactoeserver.presentation.features.playersDetails to javafx.fxml;
    opens com.mycompany.tictactoeserver.network.request to com.google.gson;
    opens com.mycompany.tictactoeserver.network.response to com.google.gson;
    opens com.mycompany.tictactoeserver.network.dtos to com.google.gson;
    opens com.mycompany.tictactoeserver.network to com.google.gson;
    exports com.mycompany.tictactoeserver;
}
