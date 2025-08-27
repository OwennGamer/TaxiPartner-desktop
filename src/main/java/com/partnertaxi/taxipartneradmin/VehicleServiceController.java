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
import javafx.stage.Stage;
import javafx.scene.control.TableRow;

import java.util.Objects;

import com.partnertaxi.taxipartneradmin.TableUtils;

import java.util.List;

public class VehicleServiceController {

    @FXML private TableView<ServiceRecord> serviceTable;
    @FXML private TableColumn<ServiceRecord, String> colData;
    @FXML private TableColumn<ServiceRecord, String> colOpis;
    @FXML private TableColumn<ServiceRecord, Double> colKoszt;
    @FXML private TableColumn<ServiceRecord, Void> colZdjecia;

    private static final String PREF_KEY_COLUMNS_ORDER = "vehicleServiceTable.columnsOrder";

    private String rejestracja;

    public void setRejestracja(String rejestracja) {
        this.rejestracja = rejestracja;
        loadServices();
    }

    @FXML
    public void initialize() {
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colOpis.setCellValueFactory(new PropertyValueFactory<>("opis"));
        colKoszt.setCellValueFactory(cellData -> cellData.getValue().kosztProperty().asObject());

        colZdjecia.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Wyświetl");

            {
                btn.setOnAction(e -> {
                    ServiceRecord rec = getTableView().getItems().get(getIndex());
                    openImagesDialog(rec.getZdjecia());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    btn.setDisable(true);
                } else {
                    ServiceRecord rec = getTableView().getItems().get(getIndex());
                    btn.setDisable(rec.getZdjecia() == null || rec.getZdjecia().isEmpty());
                    setGraphic(btn);
                }
            }
        });

        TableUtils.enableCopyOnCtrlC(serviceTable);
        TableUtils.enableColumnsOrderPersistence(serviceTable, VehicleServiceController.class, PREF_KEY_COLUMNS_ORDER);

        serviceTable.setRowFactory(tv -> {
            TableRow<ServiceRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openEditDialog(row.getItem());
                }
            });
            return row;
        });
    }

    private void loadServices() {
        if (rejestracja == null) return;
        List<ServiceRecord> services = ApiClient.getServiceRecords(rejestracja);
        ObservableList<ServiceRecord> items = FXCollections.observableArrayList(services);
        serviceTable.setItems(items);
    }


    private void openImagesDialog(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Brak zdjęć").showAndWait();
            return;
        }

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
        stage.setTitle("Zdjęcia serwisu");
        Scene scene = new Scene(scrollPane, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void openEditDialog(ServiceRecord rec) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edycja serwisu");
        ButtonType saveBtn = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        TextField opisField = new TextField(rec.getOpis());
        TextField kosztField = new TextField(String.valueOf(rec.getKoszt()));

        VBox box = new VBox(10,
                new Label("Opis:"), opisField,
                new Label("Koszt:"), kosztField;

        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {
                double koszt;
                try {
                    koszt = Double.parseDouble(kosztField.getText().trim());
                } catch (NumberFormatException ex) {
                    return null;
                }
                boolean ok = ApiClient.updateServiceRecord(rec.getId(), opisField.getText(), koszt);
                if (!ok) {
                    new Alert(Alert.AlertType.ERROR, "Nie udało się zaktualizować serwisu.").showAndWait();
                }
                loadServices();
            }
            return null;
        });

        dialog.showAndWait();
    }

}
