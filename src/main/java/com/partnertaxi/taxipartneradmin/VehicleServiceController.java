package com.partnertaxi.taxipartneradmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import com.partnertaxi.taxipartneradmin.TableUtils;

import java.util.List;

public class VehicleServiceController {

    @FXML private TableView<ServiceRecord> serviceTable;
    @FXML private TableColumn<ServiceRecord, Integer> colId;
    @FXML private TableColumn<ServiceRecord, String> colDate;
    @FXML private TableColumn<ServiceRecord, String> colDescription;
    @FXML private TableColumn<ServiceRecord, Double> colCost;

    private static final String PREF_KEY_COLUMNS_ORDER = "vehicleServiceTable.columnsOrder";

    private String rejestracja;

    public void setRejestracja(String rejestracja) {
        this.rejestracja = rejestracja;
        loadServices();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        TableUtils.enableCopyOnCtrlC(serviceTable);
        TableUtils.enableColumnsOrderPersistence(serviceTable, VehicleServiceController.class, PREF_KEY_COLUMNS_ORDER);
    }

    private void loadServices() {
        if (rejestracja == null) return;
        List<ServiceRecord> services = ApiClient.getServiceRecords(rejestracja);
        ObservableList<ServiceRecord> items = FXCollections.observableArrayList(services);
        serviceTable.setItems(items);
    }
}
