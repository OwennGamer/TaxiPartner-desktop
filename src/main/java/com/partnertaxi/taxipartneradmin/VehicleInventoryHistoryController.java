package com.partnertaxi.taxipartneradmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.partnertaxi.taxipartneradmin.TableUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class VehicleInventoryHistoryController {

    @FXML private TableView<InventoryHistoryRecord> histTable;
    @FXML private TableColumn<InventoryHistoryRecord, Integer> colHistId;
    @FXML private TableColumn<InventoryHistoryRecord, String>  colHistDateTime;
    @FXML private TableColumn<InventoryHistoryRecord, String>  colHistDriver;    // teraz String
    @FXML private TableColumn<InventoryHistoryRecord, Integer> colHistPrzebieg;
    @FXML private Button btnOpenRecord;

    private static final String PREF_KEY_COLUMNS_ORDER = "vehicleInventoryHistoryTable.columnsOrder";

    // Ustawiane przez FleetController przed wczytaniem okna
    private String rejestracja;

    /** Setter na rejestrację pojazdu – wywołaj przed loadHistory() */
    public void setRejestracja(String rejestracja) {
        this.rejestracja = rejestracja;
    }

    @FXML
    public void initialize() {
        colHistId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colHistDateTime.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        colHistDriver.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        colHistPrzebieg.setCellValueFactory(new PropertyValueFactory<>("przebieg"));

        // Przyciski aktywne dopiero po zaznaczeniu wiersza
        histTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnOpenRecord.setDisable(newSel == null);
        });

        TableUtils.enableCopyOnCtrlC(histTable);
        TableUtils.enableColumnsOrderPersistence(histTable, VehicleInventoryHistoryController.class, PREF_KEY_COLUMNS_ORDER);
    }

    /**
     * Załaduj dane historii – wywołaj zaraz po skonfigurowaniu rejestracji.
     */
    public void loadHistory() {
        List<InventoryHistoryRecord> history = ApiClient.getInventoryHistory(rejestracja);
        ObservableList<InventoryHistoryRecord> items = FXCollections.observableArrayList(history);
        histTable.setItems(items);
    }

    @FXML
    public void onOpenInventoryRecord(ActionEvent event) {
        InventoryHistoryRecord selected = histTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/partnertaxi/taxipartneradmin/inventory_detail_view.fxml")
            );
            Parent root = loader.load();

            // Przekaż ID rekordu do kontrolera szczegółów
            InventoryDetailController ctrl = loader.getController();
            ctrl.setRecordId(selected.getId());

            Stage stage = new Stage();
            stage.setTitle("Szczegóły inwentaryzacji");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Nie można otworzyć szczegółów").showAndWait();
        }
    }
}
