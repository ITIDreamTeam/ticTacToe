module com.mycompany.tictactoeserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires com.google.gson;

    opens com.mycompany.tictactoeserver to javafx.fxml;
    opens com.mycompany.tictactoeserver.presentation.features.home to javafx.fxml;
    opens com.mycompany.tictactoeserver.presentation.features.playersDetails to javafx.fxml;

    exports com.mycompany.tictactoeserver;
}
