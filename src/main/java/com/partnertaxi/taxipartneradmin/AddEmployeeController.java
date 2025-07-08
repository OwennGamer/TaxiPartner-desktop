package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddEmployeeController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML
    private void handleSave() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        if (id.isEmpty() || name.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Uzupełnij wszystkie pola.", ButtonType.OK).showAndWait();
            return;
        }
        Employee e = new Employee(id, name);
        ApiClient.addEmployee(e);
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage st = (Stage) cancelButton.getScene().getWindow();
        st.close();
    }
}
