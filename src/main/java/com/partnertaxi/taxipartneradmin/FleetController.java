package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import com.partnertaxi.taxipartneradmin.TableUtils;

import java.util.List;

public class FleetController {

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
    private TableColumn<Vehicle, String> colFirmaInna;
    @FXML
    private TableColumn<Vehicle, String> colFormaWlasnosci;
    @FXML
    private TableColumn<Vehicle, String> colNumerPolisy;
    @FXML
    private Button btnHistory;

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
        colFirmaInna.setCellValueFactory(new PropertyValueFactory<>("firmaInna"));
        colFormaWlasnosci.setCellValueFactory(new PropertyValueFactory<>("formaWlasnosci"));
        colNumerPolisy.setCellValueFactory(new PropertyValueFactory<>("numerPolisy"));

        List<Vehicle> vehicles = ApiClient.getVehicles();
        ObservableList<Vehicle> observableVehicles = FXCollections.observableArrayList(vehicles);
        vehicleTable.setItems(observableVehicles);
        vehicleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> btnHistory.setDisable(newSelection == null)
        );

        TableUtils.enableCopyOnCtrlC(vehicleTable);
    }

    @FXML
    public void onAddVehicle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/partnertaxi/taxipartneradmin/add-vehicle-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Dodaj pojazd");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            List<Vehicle> updatedList = ApiClient.getVehicles();
            vehicleTable.setItems(FXCollections.observableArrayList(updatedList));

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
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Nie można otworzyć historii pojazdu.").showAndWait();
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
