package com.partnertaxi.taxipartneradmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TableRow;

import java.util.Objects;

import com.partnertaxi.taxipartneradmin.TableUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VehicleDamageController {

    @FXML private TableView<DamageRecord> damageTable;
    @FXML private TableColumn<DamageRecord, String> colNrSzkody;
    @FXML private TableColumn<DamageRecord, String> colStatus;
    @FXML private TableColumn<DamageRecord, String> colData;
    @FXML private TableColumn<DamageRecord, String> colOpis;
    @FXML private TableColumn<DamageRecord, Void> colZdjecia;

    private static final String PREF_KEY_COLUMNS_ORDER = "vehicleDamageTable.columnsOrder";

    private String rejestracja;

    public void setRejestracja(String rejestracja) {
        this.rejestracja = rejestracja;
        loadDamages();
    }

    @FXML
    public void initialize() {
        colNrSzkody.setCellValueFactory(new PropertyValueFactory<>("nrSzkody"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colOpis.setCellValueFactory(new PropertyValueFactory<>("opis"));

        colZdjecia.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Wyświetl");

            {
                btn.setOnAction(e -> {
                    DamageRecord rec = getTableView().getItems().get(getIndex());
                    openImagesDialog(rec.getZdjecia());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DamageRecord rec = getTableView().getItems().get(getIndex());
                    btn.setDisable(rec.getZdjecia() == null || rec.getZdjecia().isEmpty());
                    setGraphic(btn);
                }
            }
        });

        TableUtils.enableCopyOnCtrlC(damageTable);
        TableUtils.enableColumnsOrderPersistence(damageTable, VehicleDamageController.class, PREF_KEY_COLUMNS_ORDER);

        damageTable.setRowFactory(tv -> {
            TableRow<DamageRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openEditDialog(row.getItem());
                }
            });
            return row;
        });
    }

    private void loadDamages() {
        if (rejestracja == null) return;
        List<DamageRecord> damages = ApiClient.getDamageRecords(rejestracja);
        ObservableList<DamageRecord> items = FXCollections.observableArrayList(damages);
        damageTable.setItems(items);
    }


    private void openImagesDialog(List<String> urls) {
        if (urls == null || urls.isEmpty()) return;

        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(10);
        for (String url : urls) {
            ImageView imageView = new ImageView(new Image(url, true));
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(800);
            box.getChildren().add(imageView);
        }

        ScrollPane scrollPane = new ScrollPane(new StackPane(box));
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Stage stage = new Stage();
        stage.setTitle("Zdjęcia szkody");
        Scene scene = new Scene(scrollPane, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void openEditDialog(DamageRecord rec) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edycja szkody");
        ButtonType saveBtn = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField opisField = new TextField(rec.getOpis());
        TextField kosztField = new TextField(String.valueOf(rec.getKoszt()));
        TextField statusField = new TextField(rec.getStatus() == null ? "" : rec.getStatus());
        Button photoBtn = new Button("Dodaj zdjęcia");
        List<File> photos = new ArrayList<>();
        photoBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png"));
            List<File> selected = fc.showOpenMultipleDialog(damageTable.getScene().getWindow());
            if (selected != null) photos.addAll(selected);
        });

        VBox box = new VBox(10,
                new Label("Opis:"), opisField,
                new Label("Koszt:"), kosztField,
                new Label("Status:"), statusField,
                photoBtn);
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {
                double koszt;
                try {
                    koszt = Double.parseDouble(kosztField.getText().trim());
                } catch (NumberFormatException ex) {
                    return null;
                }
                ApiClient.updateDamageRecord(rec.getId(), opisField.getText(), koszt, statusField.getText(), photos);
                loadDamages();
            }
            return null;
        });

        dialog.showAndWait();
    }
}
