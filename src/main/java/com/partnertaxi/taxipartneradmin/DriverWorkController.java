package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DriverWorkController {

    @FXML private TableView<DriverWorkEntry> workTable;
    @FXML private TableColumn<DriverWorkEntry, String>  dateColumn;
    @FXML private TableColumn<DriverWorkEntry, String>  startTimeColumn;
    @FXML private TableColumn<DriverWorkEntry, String>  endTimeColumn;
    @FXML private TableColumn<DriverWorkEntry, Float>  hoursColumn;
    @FXML private TableColumn<DriverWorkEntry, Integer> kilometersColumn;

    private static final String PREF_KEY_COLUMNS_ORDER = "driverWorkTable.columnsOrder";

    private String driverId;

    public void setDriverId(String driverId) {
        this.driverId = driverId;
        loadWork();
    }

    @FXML
    private void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("hours"));
        kilometersColumn.setCellValueFactory(new PropertyValueFactory<>("kilometers"));

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setGroupingUsed(false);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        hoursColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Float val, boolean empty) {
                super.updateItem(val, empty);
                setText((empty || val == null) ? null : nf.format(val));
            }
        });
        kilometersColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer val, boolean empty) {
                super.updateItem(val, empty);
                setText((empty || val == null) ? null : nf.format(val));
            }
        });

        TableUtils.enableCopyOnCtrlC(workTable);
        TableUtils.enableColumnsOrderPersistence(workTable, DriverWorkController.class, PREF_KEY_COLUMNS_ORDER);
    }

    private void loadWork() {
        if (driverId == null) return;
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(30);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<DriverWorkEntry> data = ApiClient.getDriverSessions(driverId, start.format(fmt), end.format(fmt));
        workTable.getItems().setAll(data);
    }
}
