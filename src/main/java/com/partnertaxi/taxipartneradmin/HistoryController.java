package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

import com.partnertaxi.taxipartneradmin.TableUtils;

import java.text.NumberFormat;
import java.util.Locale;

public class HistoryController {

    @FXML private TableView<HistoryEntry> historyTable;
    @FXML private TableColumn<HistoryEntry, String> dateTimeColumn;
    @FXML private TableColumn<HistoryEntry, String> typeColumn;
    @FXML private TableColumn<HistoryEntry, String> descriptionColumn;
    @FXML private TableColumn<HistoryEntry, String> changeValueColumn;
    @FXML private TableColumn<HistoryEntry, String> saldoAfterColumn;
    @FXML private TableColumn<HistoryEntry, Void> photoColumn;

    private static final String PREF_KEY_COLUMNS_ORDER = "historyTable.columnsOrder";
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

        // 6) kolumna "zdjęcie" z przyciskiem
        photoColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Wyświetl");

            {
                btn.setOnAction(e -> {
                    HistoryEntry entry = getTableView().getItems().get(getIndex());
                    if (entry.getReceiptPhotoUrl() != null) {
                        openImageDialog(entry.getReceiptPhotoUrl());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HistoryEntry entry = getTableView().getItems().get(getIndex());
                    btn.setDisable(!entry.isPhotoAvailable());
                    setGraphic(btn);
                }
            }
        });

        TableUtils.enableCopyOnCtrlC(historyTable);
        TableUtils.enableColumnsOrderPersistence(historyTable, HistoryController.class, PREF_KEY_COLUMNS_ORDER);
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

    private void openImageDialog(String imageUrl) {
        ImageView imageView = new ImageView(new Image(imageUrl, true));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(800);

        ScrollPane scrollPane = new ScrollPane(new StackPane(imageView));
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Stage stage = new Stage();
        stage.setTitle("Podgląd zdjęcia");
        Scene scene = new Scene(scrollPane, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(
                HelloApplication.class.getResource("style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
