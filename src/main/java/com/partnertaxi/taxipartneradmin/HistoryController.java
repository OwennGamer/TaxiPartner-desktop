package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

import com.partnertaxi.taxipartneradmin.TableUtils;

import java.text.NumberFormat;
import java.util.List;
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
                    List<String> urls = entry.getReceiptPhotoUrls();
                    if (urls != null && !urls.isEmpty()) {
                        openImageDialog(urls, 0);
                    } else if (entry.getReceiptPhotoUrl() != null) {
                        openImageDialog(List.of(entry.getReceiptPhotoUrl()), 0);
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

    private void openImageDialog(List<String> imageUrls, int startIndex) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        int safeIndex = Math.max(0, Math.min(startIndex, imageUrls.size() - 1));
        ImageView imageView = new ImageView(new Image(imageUrls.get(safeIndex), true));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(800);

        DoubleProperty rotationAngle = new SimpleDoubleProperty(0);
        imageView.rotateProperty().bind(rotationAngle);

        ScrollPane scrollPane = new ScrollPane(new StackPane(imageView));
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Button rotateLeft = new Button("Obróć w lewo");
        rotateLeft.setOnAction(e -> rotationAngle.set((rotationAngle.get() - 90 + 360) % 360));

        Button rotateRight = new Button("Obróć w prawo");
        rotateRight.setOnAction(e -> rotationAngle.set((rotationAngle.get() + 90) % 360));

        Button prev = new Button("Poprzednie");
        Button next = new Button("Następne");

        final int[] currentIndex = {safeIndex};
        Runnable updateImage = () -> {
            imageView.setImage(new Image(imageUrls.get(currentIndex[0]), true));
            prev.setDisable(currentIndex[0] == 0);
            next.setDisable(currentIndex[0] >= imageUrls.size() - 1);
            rotationAngle.set(0);
        };

        prev.setOnAction(e -> {
            if (currentIndex[0] > 0) {
                currentIndex[0]--;
                updateImage.run();
            }
        });
        next.setOnAction(e -> {
            if (currentIndex[0] < imageUrls.size() - 1) {
                currentIndex[0]++;
                updateImage.run();
            }
        });

        HBox controls = new HBox(10, prev, next, rotateLeft, rotateRight);
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-padding: 10; -fx-background-color: #222; -fx-border-color: #444; -fx-border-width: 1 0 0 0;");

        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);
        root.setBottom(controls);

        Stage stage = new Stage();
        stage.setTitle("Podgląd zdjęcia");
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(
                HelloApplication.class.getResource("style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        updateImage.run();
        stage.show();
    }
}
