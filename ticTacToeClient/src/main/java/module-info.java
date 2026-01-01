module com.mycompany.tictactoeclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    opens com.mycompany.tictactoeclient.presentation.features.playersboard to javafx.fxml;
    opens com.mycompany.tictactoeclient.presentation.features.home to javafx.fxml;
<<<<<<< HEAD
    opens com.mycompany.tictactoeclient.presentation.features.profile to javafx.fxml;
    opens com.mycompany.tictactoeclient.presentation.features.login to javafx.fxml;
=======
     opens com.mycompany.tictactoeclient.presentation.features.profile to javafx.fxml;
     opens com.mycompany.tictactoeclient.presentation.features.change_password to javafx.fxml;
>>>>>>> 51ff98e7d0ccd1c6edf5affec0a15c2127d194b4
    opens com.mycompany.tictactoeclient to javafx.fxml;
    exports com.mycompany.tictactoeclient;
}
