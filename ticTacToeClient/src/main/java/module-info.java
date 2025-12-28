module com.mycompany.tictactoeclient {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.tictactoeclient to javafx.fxml;
    exports com.mycompany.tictactoeclient;
}
