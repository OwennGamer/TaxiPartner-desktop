package com.partnertaxi.taxipartneradmin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class EmployeesController {

    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, String> colId;
    @FXML private TableColumn<Employee, String> colName;

    @FXML
    public void initialize() {
        if (colId != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        }
        if (colName != null) {
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
    }

    @FXML
    public void handleAddEmployee(ActionEvent event) {
        System.out.println("Add employee clicked");
    }

    @FXML
    public void handleEditEmployee(ActionEvent event) {
        System.out.println("Edit employee clicked");
    }

    @FXML
    public void handleDeleteEmployee(ActionEvent event) {
        System.out.println("Delete employee clicked");
    }
}
