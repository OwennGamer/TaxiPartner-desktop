package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.Locale;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLoginButton() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("EMPTY_FIELDS");
            return;
        }

        String error = ApiClient.login(username, password);

        if (error == null) {
            HelloApplication.changeScene("drivers-view.fxml", "Zarządzanie kierowcami");
        } else {
            showAlert(error);
        }
    }

    private void showAlert(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);

        if ("EMPTY_FIELDS".equals(error)) {
            alert.setTitle("Puste pola");
            alert.setContentText("Wprowadź login i hasło.");
        } else if (error.toLowerCase(Locale.ROOT).contains("połączenia")) {
            alert.setTitle("Błąd połączenia");
            alert.setContentText("Nie można połączyć z serwerem.");
        } else {
            alert.setTitle("Błąd logowania");
            alert.setContentText(error);
        }

        alert.showAndWait();


    }
}
