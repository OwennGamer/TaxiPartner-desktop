package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.CheckBox;
import java.util.function.Function;
import java.util.Objects;

import com.partnertaxi.taxipartneradmin.TableUtils;
import com.partnertaxi.taxipartneradmin.EditVehicleController;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.time.LocalDate;
import java.text.NumberFormat;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class FleetController {

    private static final String PREF_KEY_COLUMNS_ORDER = "fleetTable.columnsOrder";

    @FXML
    private TableView<Vehicle> vehicleTable;
    @FXML
    private TableColumn<Vehicle, Integer> colId;
    @FXML
    private TableColumn<Vehicle, String> colRejestracja;
    @FXML
    private TableColumn<Vehicle, String> colMarka;
    @FXML
    private TableColumn<Vehicle, String> colModel;
    @FXML
    private TableColumn<Vehicle, Integer> colPrzebieg;
    @FXML
    private TableColumn<Vehicle, String> colUbezpieczenie;
    @FXML
    private TableColumn<Vehicle, String> colPrzeglad;
    @FXML
    private TableColumn<Vehicle, Boolean> colAktywny;
    @FXML
    private TableColumn<Vehicle, Boolean> colInpost;
    @FXML
    private TableColumn<Vehicle, Boolean> colTaxi;
    @FXML
    private TableColumn<Vehicle, Boolean> colTaksometr;
    @FXML
    private TableColumn<Vehicle, String> colLegalizacjaTaksometruDo;
    @FXML
    private TableColumn<Vehicle, Boolean> colGaz;
    @FXML
    private TableColumn<Vehicle, String> colHomologacjaLpgDo;
    @FXML
    private TableColumn<Vehicle, String> colFirma;
    @FXML
    private TableColumn<Vehicle, String> colFormaWlasnosci;
    @FXML
    private TableColumn<Vehicle, String> colNumerPolisy;
    @FXML
    private TableColumn<Vehicle, String> colWymianaOlejuData;
    @FXML
    private TableColumn<Vehicle, Integer> colWymianaOlejuPrzebieg;
    @FXML
    private TableColumn<Vehicle, String> colOstatniaInwentaryzacja;
    @FXML
    private Button btnHistory;
    @FXML
    private Button btnService;
    @FXML
    private Button btnDamages;
    @FXML
    private Button btnTurnoverDetails;
    @FXML
    private CheckBox chkShowInactive;
    @FXML
    private TableColumn<Vehicle, Float> colObrot;
    @FXML
    private DatePicker turnoverStartDate;
    @FXML
    private DatePicker turnoverEndDate;

    private ObservableList<Vehicle> allVehicles;
    private FilteredList<Vehicle> filteredVehicles;
    private SortedList<Vehicle> sortedVehicles;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colRejestracja.setCellValueFactory(new PropertyValueFactory<>("rejestracja"));
        colMarka.setCellValueFactory(new PropertyValueFactory<>("marka"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colPrzebieg.setCellValueFactory(new PropertyValueFactory<>("przebieg"));
        colUbezpieczenie.setCellValueFactory(new PropertyValueFactory<>("ubezpieczenieDo"));
        colPrzeglad.setCellValueFactory(new PropertyValueFactory<>("przegladDo"));
        setupCheckBoxColumn(colAktywny, Vehicle::aktywnyProperty);
        setupCheckBoxColumn(colInpost, Vehicle::inpostProperty);
        setupCheckBoxColumn(colTaxi, Vehicle::taxiProperty);
        setupCheckBoxColumn(colTaksometr, Vehicle::taksometrProperty);
        colLegalizacjaTaksometruDo.setCellValueFactory(new PropertyValueFactory<>("legalizacjaTaksometruDo"));
        setupCheckBoxColumn(colGaz, Vehicle::gazProperty);
        colHomologacjaLpgDo.setCellValueFactory(new PropertyValueFactory<>("homologacjaLpgDo"));
        colFirma.setCellValueFactory(new PropertyValueFactory<>("firma"));
        colFormaWlasnosci.setCellValueFactory(new PropertyValueFactory<>("formaWlasnosci"));
        colNumerPolisy.setCellValueFactory(new PropertyValueFactory<>("numerPolisy"));
        colWymianaOlejuData.setCellValueFactory(new PropertyValueFactory<>("wymianaOlejuData"));
        colWymianaOlejuPrzebieg.setCellValueFactory(new PropertyValueFactory<>("wymianaOlejuPrzebieg"));
        colOstatniaInwentaryzacja.setCellValueFactory(new PropertyValueFactory<>("ostatniaInwentaryzacja"));
        colObrot.setCellValueFactory(new PropertyValueFactory<>("obrot"));
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setGroupingUsed(false);
        colObrot.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : currencyFormat.format(item));
            }
        });

        LocalDate now = LocalDate.now();
        turnoverStartDate.setValue(now.withDayOfMonth(1));
        turnoverEndDate.setValue(now);

        List<Vehicle> vehicles = ApiClient.getVehicles();
        allVehicles = FXCollections.observableArrayList(vehicles);
        filteredVehicles = new FilteredList<>(allVehicles);
        sortedVehicles = new SortedList<>(filteredVehicles);
        sortedVehicles.comparatorProperty().bind(vehicleTable.comparatorProperty());
        vehicleTable.setItems(sortedVehicles);
        vehicleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean disable = newSelection == null;
                    btnHistory.setDisable(disable);
                    btnService.setDisable(disable);
                    btnDamages.setDisable(disable);
                    btnTurnoverDetails.setDisable(disable);
                }
        );
        applyFilter();
        applyTurnover();
        chkShowInactive.selectedProperty().addListener((obs, o, n) -> applyFilter());
        turnoverStartDate.valueProperty().addListener((obs, o, n) -> applyTurnover());
        turnoverEndDate.valueProperty().addListener((obs, o, n) -> applyTurnover());

        TableUtils.enableCopyOnCtrlC(vehicleTable);
        TableUtils.enableColumnsOrderPersistence(vehicleTable, FleetController.class, PREF_KEY_COLUMNS_ORDER);
    }

    @FXML
    public void onAddVehicle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/partnertaxi/taxipartneradmin/add-vehicle-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Dodaj pojazd");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.showAndWait();

            reloadVehicles();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Nie udało się otworzyć formularza.");
            alert.showAndWait();
        }
    }

    @FXML
    public void onShowHistory(ActionEvent event) {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Wybierz pojazd z listy.").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/partnertaxi/taxipartneradmin/vehicle_inventory_history.fxml"));
            Parent root = loader.load();

            VehicleInventoryHistoryController ctrl = loader.getController();
            ctrl.setRejestracja(selected.getRejestracja());
            ctrl.loadHistory();

            Stage stage = new Stage();
            stage.setTitle("Historia inwentaryzacji: " + selected.getRejestracja());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Nie można otworzyć historii pojazdu.").showAndWait();
        }
    }

    @FXML
    public void onShowService(ActionEvent event) {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Wybierz pojazd z listy.").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/partnertaxi/taxipartneradmin/vehicle_service_view.fxml"));
            Parent root = loader.load();

            VehicleServiceController ctrl = loader.getController();
            ctrl.setRejestracja(selected.getRejestracja());

            Stage stage = new Stage();
            stage.setTitle("Serwis pojazdu: " + selected.getRejestracja());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Nie można otworzyć serwisu pojazdu.").showAndWait();
        }
    }

    @FXML
    public void onShowDamages(ActionEvent event) {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Wybierz pojazd z listy.").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/partnertaxi/taxipartneradmin/vehicle_damage_view.fxml"));
            Parent root = loader.load();

            VehicleDamageController ctrl = loader.getController();
            ctrl.setRejestracja(selected.getRejestracja());

            Stage stage = new Stage();
            stage.setTitle("Szkody pojazdu: " + selected.getRejestracja());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Nie można otworzyć szkód pojazdu.").showAndWait();
        }
    }


    @FXML
    public void onShowTurnoverDetails(ActionEvent event) {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Wybierz pojazd z listy.").showAndWait();
            return;
        }

        LocalDate start = turnoverStartDate.getValue();
        LocalDate end = turnoverEndDate.getValue();
        if (start == null || end == null) {
            new Alert(Alert.AlertType.WARNING, "Ustaw zakres dat obrotu.").showAndWait();
            return;
        }

        ApiClient.VehicleTurnoverDetailsResult result = ApiClient.getVehicleTurnoverDetails(
                start.toString(),
                end.toString(),
                selected.getRejestracja()
        );

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setGroupingUsed(false);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Szczegóły obrotu pojazdu");
        dialog.setHeaderText("Pojazd: " + selected.getRejestracja() +
                " | okres: " + start + " - " + end +
                " | kursy: " + result.getCount() +
                " | suma: " + currencyFormat.format(result.getSum()));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        TableView<ApiClient.VehicleTurnoverDetailRecord> detailsTable = new TableView<>();
        TableColumn<ApiClient.VehicleTurnoverDetailRecord, Integer> colRideId = new TableColumn<>("ID kursu");
        colRideId.setCellValueFactory(new PropertyValueFactory<>("rideId"));

        TableColumn<ApiClient.VehicleTurnoverDetailRecord, String> colDate = new TableColumn<>("Data");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<ApiClient.VehicleTurnoverDetailRecord, String> colDriver = new TableColumn<>("Kierowca");
        colDriver.setCellValueFactory(new PropertyValueFactory<>("driverId"));

        TableColumn<ApiClient.VehicleTurnoverDetailRecord, String> colPaymentType = new TableColumn<>("Płatność");
        colPaymentType.setCellValueFactory(new PropertyValueFactory<>("paymentType"));

        TableColumn<ApiClient.VehicleTurnoverDetailRecord, String> colType = new TableColumn<>("Typ");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<ApiClient.VehicleTurnoverDetailRecord, Float> colAmount = new TableColumn<>("Kwota");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : currencyFormat.format(item));
            }
        });

        TableColumn<ApiClient.VehicleTurnoverDetailRecord, String> colSessionStart = new TableColumn<>("Start zmiany");
        colSessionStart.setCellValueFactory(new PropertyValueFactory<>("sessionStart"));

        TableColumn<ApiClient.VehicleTurnoverDetailRecord, String> colSessionEnd = new TableColumn<>("Koniec zmiany");
        colSessionEnd.setCellValueFactory(new PropertyValueFactory<>("sessionEnd"));

        detailsTable.getColumns().addAll(colRideId, colDate, colDriver, colPaymentType, colType, colAmount, colSessionStart, colSessionEnd);
        detailsTable.setItems(FXCollections.observableArrayList(result.getRecords()));

        Label info = new Label("Wiersze pokazują kursy wliczone do sumy obrotu dla wybranego pojazdu.");
        VBox box = new VBox(10, info, detailsTable);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefSize(1100, 650);
        dialog.showAndWait();
    }
    @FXML
    public void onEditVehicle(ActionEvent event) {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Wybierz pojazd z listy.").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/partnertaxi/taxipartneradmin/edit-vehicle-view.fxml"));
            Parent root = loader.load();
            EditVehicleController ctrl = loader.getController();
            ctrl.setVehicle(selected);
            Stage stage = new Stage();
            stage.setTitle("Edytuj pojazd");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.showAndWait();

            reloadVehicles();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Nie można otworzyć formularza edycji.").showAndWait();
        }
    }

    @FXML
    public void onDeleteVehicle(ActionEvent event) {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Wybierz pojazd z listy.").showAndWait();
            return;
        }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "Usunąć pojazd " + selected.getRejestracja() + "?", ButtonType.OK, ButtonType.CANCEL);
        conf.setHeaderText(null);
        conf.showAndWait().ifPresent(b -> {
            if (b == ButtonType.OK) {
                ApiClient.deleteVehicle(selected.getId());
                reloadVehicles();
            }
        });
    }

    @FXML
    public void onRefresh(ActionEvent event) {
        reloadVehicles();
    }

//    @FXML
    public void onToggleShowInactive(ActionEvent event) {
        applyFilter();
    }

    private void reloadVehicles() {
        List<Vehicle> updatedList = ApiClient.getVehicles();
        allVehicles.setAll(updatedList);
        applyFilter();
        applyTurnover();
    }

    private void applyFilter() {
        if (filteredVehicles == null) return;
        if (chkShowInactive.isSelected()) {
            filteredVehicles.setPredicate(v -> true);
        } else {
            filteredVehicles.setPredicate(Vehicle::isAktywny);
        }
    }

    private void applyTurnover() {
        if (allVehicles == null) {
            return;
        }

        LocalDate start = turnoverStartDate.getValue();
        LocalDate end = turnoverEndDate.getValue();
        if (start == null || end == null) {
            return;
        }

        if (start.isAfter(end)) {
            LocalDate correctedEnd = start;
            turnoverEndDate.setValue(correctedEnd);
            end = correctedEnd;
        }

        Map<String, Float> turnoverByVehicle = ApiClient.getVehicleTurnover(start.toString(), end.toString());
        for (Vehicle vehicle : allVehicles) {
            String plateKey = vehicle.getRejestracja() == null ? "" : vehicle.getRejestracja().trim().toUpperCase(Locale.ROOT);
            vehicle.setObrot(turnoverByVehicle.getOrDefault(plateKey, 0f));
        }
        vehicleTable.refresh();
    }

    private void setupCheckBoxColumn(TableColumn<Vehicle, Boolean> column,
                                     Function<Vehicle, BooleanProperty> extractor) {
        column.setCellValueFactory(cellData -> extractor.apply(cellData.getValue()));
        column.setCellFactory(col -> new TableCell<>() {
            private final CheckBox box = new CheckBox();
            {
                box.setDisable(true);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    box.setSelected(Boolean.TRUE.equals(item));
                    setGraphic(box);
                }
            }
        });
    }
}
