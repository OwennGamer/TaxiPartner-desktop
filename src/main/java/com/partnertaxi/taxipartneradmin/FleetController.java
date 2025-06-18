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
        colAktywny.setCellValueFactory(new PropertyValueFactory<>("aktywny"));

        List<Vehicle> vehicles = ApiClient.getVehicles();
        ObservableList<Vehicle> observableVehicles = FXCollections.observableArrayList(vehicles);
        vehicleTable.setItems(observableVehicles);
        // Włącz przycisk "Historia pojazdu" tylko gdy wybrano wiersz
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
            stage.showAndWait(); // ⏳ Czeka aż użytkownik zamknie okno

            // 🔄 Po zamknięciu: odśwież listę pojazdów
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

            // Przekaż rejestrację do kontrolera historii
            VehicleInventoryHistoryController ctrl = loader.getController();
            ctrl.setRejestracja(selected.getRejestracja());
            ctrl.loadHistory();  // <— TU dociągamy dane i ustawiamy w tabeli

            Stage stage = new Stage();
            stage.setTitle("Historia inwentaryzacji: " + selected.getRejestracja());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Nie można otworzyć historii pojazdu.").showAndWait();
        }
    }



}
