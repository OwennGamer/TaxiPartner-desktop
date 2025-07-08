package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditEmployeeController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Employee employee;

    public void setEmployee(Employee e) {
        this.employee = e;
        idField.setText(e.getId());
        nameField.setText(e.getName());
    }

    @FXML
    private void handleSave() {
        if (employee == null) {
            closeWindow();
            return;
        }
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Imię i nazwisko nie może być puste.", ButtonType.OK).showAndWait();
            return;
        }
        employee.nameProperty().set(name);
        ApiClient.updateEmployee(employee);
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
