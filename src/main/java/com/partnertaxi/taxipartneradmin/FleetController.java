package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.CheckBox;
import java.util.function.Function;
import java.util.Objects;

import com.partnertaxi.taxipartneradmin.TableUtils;
import com.partnertaxi.taxipartneradmin.EditVehicleController;

import java.util.List;

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
    private Button btnHistory;
    @FXML
    private Button btnService;
    @FXML
    private Button btnDamages;
    @FXML
    private CheckBox chkShowInactive;

    private ObservableList<Vehicle> allVehicles;
    private FilteredList<Vehicle> filteredVehicles;

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

        List<Vehicle> vehicles = ApiClient.getVehicles();
        allVehicles = FXCollections.observableArrayList(vehicles);
        filteredVehicles = new FilteredList<>(allVehicles);
        vehicleTable.setItems(filteredVehicles);
        vehicleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean disable = newSelection == null;
                    btnHistory.setDisable(disable);
                    btnService.setDisable(disable);
                    btnDamages.setDisable(disable);
                }
        );
        applyFilter();
        chkShowInactive.selectedProperty().addListener((obs, o, n) -> applyFilter());

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

            List<Vehicle> updatedList = ApiClient.getVehicles();
            allVehicles.setAll(updatedList);
            applyFilter();

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

            List<Vehicle> updatedList = ApiClient.getVehicles();
            allVehicles.setAll(updatedList);
            applyFilter();
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
                List<Vehicle> updatedList = ApiClient.getVehicles();
                allVehicles.setAll(updatedList);
                applyFilter();
            }
        });
    }

    @FXML
    public void onToggleShowInactive(ActionEvent event) {
        applyFilter();
    }

    private void applyFilter() {
        if (filteredVehicles == null) return;
        if (chkShowInactive.isSelected()) {
            filteredVehicles.setPredicate(v -> true);
        } else {
            filteredVehicles.setPredicate(Vehicle::isAktywny);
        }
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
