module com.mycompany.tictactoeclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    opens com.mycompany.tictactoeclient.presentation.features.home to javafx.fxml;
     opens com.mycompany.tictactoeclient.presentation.features.profile to javafx.fxml;
    opens com.mycompany.tictactoeclient to javafx.fxml;
    exports com.mycompany.tictactoeclient;
}
