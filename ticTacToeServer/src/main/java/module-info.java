module com.mycompany.tictactoeserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;

    opens com.mycompany.tictactoeserver to javafx.fxml;
    exports com.mycompany.tictactoeserver;
}
