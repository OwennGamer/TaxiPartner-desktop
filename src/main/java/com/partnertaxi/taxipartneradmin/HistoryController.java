package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;

public class HistoryController {

    @FXML private TableView<HistoryEntry> historyTable;
    @FXML private TableColumn<HistoryEntry, String> dateTimeColumn;
    @FXML private TableColumn<HistoryEntry, String> typeColumn;
    @FXML private TableColumn<HistoryEntry, String> descriptionColumn;
    @FXML private TableColumn<HistoryEntry, String> changeValueColumn;
    @FXML private TableColumn<HistoryEntry, String> saldoAfterColumn;

    private String driverId;

    public void setDriverId(String driverId) {
        this.driverId = driverId;
        loadHistory();
    }

    @FXML
    private void initialize() {
        // 1) tekstowe kolumny
        dateTimeColumn   .setCellValueFactory(data -> data.getValue().dateTimeProperty());
        typeColumn       .setCellValueFactory(data -> data.getValue().typeProperty());

        // 2) opis z zamianą kropki na przecinek w częściach liczbowych
        descriptionColumn.setCellValueFactory(data -> {
            String raw = data.getValue().descriptionProperty().get();
            // zamień wszystkie wystąpienia "123.45" na "123,45"
            String formatted = raw.replaceAll("(\\d+)\\.(\\d+)", "$1,$2");
            return new SimpleStringProperty(formatted);
        });

        // 3) przygotuj format liczb z przecinkiem
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setGroupingUsed(false);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        // 4) kolumna "zmiana wartości"
        changeValueColumn.setCellValueFactory(data -> {
            String raw = data.getValue().changeValueProperty().get();
            try {
                float v = Float.parseFloat(raw);
                return new SimpleStringProperty(nf.format(v));
            } catch (Exception e) {
                return new SimpleStringProperty(raw);
            }
        });

        // 5) kolumna "saldo po"
        saldoAfterColumn.setCellValueFactory(data -> {
            String raw = data.getValue().saldoAfterProperty().get();
            try {
                float v = Float.parseFloat(raw);
                return new SimpleStringProperty(nf.format(v));
            } catch (Exception e) {
                return new SimpleStringProperty(raw);
            }
        });
    }

    private void loadHistory() {
        ObservableList<HistoryEntry> list = ApiClient.getCombinedHistory(driverId);
        historyTable.setItems(list);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) historyTable.getScene().getWindow();
        stage.close();
    }
}
