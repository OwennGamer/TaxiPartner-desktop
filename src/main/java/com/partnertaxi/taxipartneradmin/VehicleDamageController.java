package com.partnertaxi.taxipartneradmin;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TableRow;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.partnertaxi.taxipartneradmin.TableUtils;

public class VehicleDamageController {

    @FXML private TableView<DamageRecord> damageTable;
    @FXML private TableColumn<DamageRecord, String> colNrSzkody;
    @FXML private TableColumn<DamageRecord, String> colStatus;
    @FXML private TableColumn<DamageRecord, String> colData;
    @FXML private TableColumn<DamageRecord, String> colOpis;
    @FXML private TableColumn<DamageRecord, Void> colZdjecia;
    @FXML private Button addDamageButton;
    @FXML private Button editDamageButton;

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
                    btn.setDisable(true);
                } else {
                    DamageRecord rec = getTableView().getItems().get(getIndex());
                    btn.setDisable(rec.getZdjecia() == null || rec.getZdjecia().isEmpty());
                    setGraphic(btn);
                }
            }
        });

        TableUtils.enableCopyOnCtrlC(damageTable);
        TableUtils.enableColumnsOrderPersistence(damageTable, VehicleDamageController.class, PREF_KEY_COLUMNS_ORDER);

        if (editDamageButton != null) {
            editDamageButton.disableProperty().bind(Bindings.isNull(damageTable.getSelectionModel().selectedItemProperty()));
        }
        damageTable.setRowFactory(tv -> {
            TableRow<DamageRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openDamageDialog(row.getItem());
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
        if (urls == null || urls.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Brak zdjęć").showAndWait();
            return;
        }

        VBox box = new VBox(10);
        for (String url : urls) {
            ImageView imageView = new ImageView(new Image(url, true));
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(800);
            DoubleProperty rotationAngle = new SimpleDoubleProperty(0);
            imageView.rotateProperty().bind(rotationAngle);

            Button rotateLeft = new Button("Obróć w lewo");
            rotateLeft.setOnAction(e -> rotationAngle.set((rotationAngle.get() - 90 + 360) % 360));

            Button rotateRight = new Button("Obróć w prawo");
            rotateRight.setOnAction(e -> rotationAngle.set((rotationAngle.get() + 90) % 360));

            HBox controls = new HBox(10, rotateLeft, rotateRight);
            controls.setAlignment(Pos.CENTER);
            controls.setStyle("-fx-padding: 10; -fx-background-color: #222; -fx-border-color: #444; -fx-border-width: 1 0 0 0;");

            BorderPane pane = new BorderPane();
            pane.setCenter(imageView);
            pane.setBottom(controls);
            BorderPane.setAlignment(controls, Pos.CENTER);
            pane.setStyle("-fx-padding: 10;");

            box.getChildren().add(pane);
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

    private void openDamageDialog(DamageRecord rec) {
        Dialog<Void> dialog = new Dialog<>();
        boolean editing = rec != null;
        dialog.setTitle(editing ? "Edycja szkody" : "Dodawanie szkody");
        ButtonType saveBtn = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);
        TextField nrSzkodyField = new TextField(editing ? rec.getNrSzkody() : "");
        TextField opisField = new TextField(editing ? rec.getOpis() : "");
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(Arrays.asList(
                "niezgłoszona",
                "zgłoszona",
                "czeka na wycenę",
                "czeka na naprawę",
                "czeka na rozliczenie",
                "zamknięta"
        ));
        if (editing && rec.getStatus() != null && !statusBox.getItems().contains(rec.getStatus())) {
            statusBox.getItems().add(rec.getStatus());
        }
        statusBox.setValue(editing ? rec.getStatus() : statusBox.getItems().isEmpty() ? null : statusBox.getItems().get(0));

        VBox box = new VBox(10,
                new Label("Nr szkody:"), nrSzkodyField,
                new Label("Opis:"), opisField,
                new Label("Status:"), statusBox);
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {
                if (rejestracja == null || rejestracja.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, "Brak numeru rejestracyjnego pojazdu.").showAndWait();
                    return null;
                }

                String nrSzkody = nrSzkodyField.getText().trim();
                String opis = opisField.getText().trim();
                String status = statusBox.getValue();

                boolean ok;
                if (editing) {
                    ok = ApiClient.updateDamageRecord(
                            rec.getId(),
                            rejestracja,
                            nrSzkody,
                            opis,
                            status
                    );
                } else {
                    ok = ApiClient.addDamageRecord(
                            rejestracja,
                            nrSzkody,
                            opis,
                            status
                    );
                }
                if (ok) {
                    loadDamages();
                } else {
                    String message = editing
                            ? "Nie udało się zaktualizować szkody."
                            : "Nie udało się dodać szkody.";
                    new Alert(Alert.AlertType.ERROR, message).showAndWait();
                }

            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void onAddDamage() {
        openDamageDialog(null);
    }

    @FXML
    private void onEditDamage() {
        DamageRecord selected = damageTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openDamageDialog(selected);
        }
    }
}
