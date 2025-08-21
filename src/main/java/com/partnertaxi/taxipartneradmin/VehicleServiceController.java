package com.partnertaxi.taxipartneradmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.util.Objects;

import com.partnertaxi.taxipartneradmin.TableUtils;

import java.util.List;

public class VehicleServiceController {

    @FXML private TableView<ServiceRecord> serviceTable;
    @FXML private TableColumn<ServiceRecord, String> colData;
    @FXML private TableColumn<ServiceRecord, String> colOpis;
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
                } else {
                    ServiceRecord rec = getTableView().getItems().get(getIndex());
                    btn.setDisable(rec.getZdjecia() == null || rec.getZdjecia().isEmpty());
                    setGraphic(btn);
                }
            }
        });

        TableUtils.enableCopyOnCtrlC(serviceTable);
        TableUtils.enableColumnsOrderPersistence(serviceTable, VehicleServiceController.class, PREF_KEY_COLUMNS_ORDER);
    }

    private void loadServices() {
        if (rejestracja == null) return;
        List<ServiceRecord> services = ApiClient.getServiceRecords(rejestracja);
        ObservableList<ServiceRecord> items = FXCollections.observableArrayList(services);
        serviceTable.setItems(items);
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
        stage.setTitle("Zdjęcia serwisu");
        Scene scene = new Scene(scrollPane, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

}
