package com.partnertaxi.taxipartneradmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import com.partnertaxi.taxipartneradmin.TableUtils;

import java.util.List;

public class VehicleDamageController {

    @FXML private TableView<DamageRecord> damageTable;
    @FXML private TableColumn<DamageRecord, Integer> colId;
    @FXML private TableColumn<DamageRecord, String> colDate;
    @FXML private TableColumn<DamageRecord, String> colDescription;
    @FXML private TableColumn<DamageRecord, Double> colCost;

    private static final String PREF_KEY_COLUMNS_ORDER = "vehicleDamageTable.columnsOrder";

    private String rejestracja;

    public void setRejestracja(String rejestracja) {
        this.rejestracja = rejestracja;
        loadDamages();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        TableUtils.enableCopyOnCtrlC(damageTable);
        TableUtils.enableColumnsOrderPersistence(damageTable, VehicleDamageController.class, PREF_KEY_COLUMNS_ORDER);
    }

    private void loadDamages() {
        if (rejestracja == null) return;
        List<DamageRecord> damages = ApiClient.getDamageRecords(rejestracja);
        ObservableList<DamageRecord> items = FXCollections.observableArrayList(damages);
        damageTable.setItems(items);
    }
}
