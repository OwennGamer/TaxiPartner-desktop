package com.partnertaxi.taxipartneradmin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;

import com.partnertaxi.taxipartneradmin.TableUtils;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class RefuelsHistoryController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private TableView<Refuel> refuelsTable;
    @FXML private TableColumn<Refuel, String>  idColumn;
    @FXML private TableColumn<Refuel, String>  dateColumn;
    @FXML private TableColumn<Refuel, Float>   fuelAmountColumn;
    @FXML private TableColumn<Refuel, Float>   costColumn;
    @FXML private TableColumn<Refuel, Integer> odometerColumn;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;

    private static final String PREF_KEY_COLUMNS_ORDER = "refuelsHistoryTable.columnsOrder";

    private String driverId;

    /** Ustawia ID kierowcy i odświeża tabelę */
    public void setDriverId(String driverId) {
        this.driverId = driverId;
        titleLabel.setText("Historia tankowań: " + driverId);
        loadRefuels();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // podstawowe kolumny
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("refuelDate"));
        fuelAmountColumn.setCellValueFactory(new PropertyValueFactory<>("fuelAmount"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        odometerColumn.setCellValueFactory(new PropertyValueFactory<>("odometer"));

        // przygotuj formatter z przecinkiem jako separatorem dziesiętnym
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setGroupingUsed(false);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        // formatowanie kolumny fuelAmount
        fuelAmountColumn.setCellFactory(col -> new TableCell<Refuel, Float>() {
            @Override
            protected void updateItem(Float val, boolean empty) {
                super.updateItem(val, empty);
                setText((empty || val == null) ? null : nf.format(val));
            }
        });

        // formatowanie kolumny cost
        costColumn.setCellFactory(col -> new TableCell<Refuel, Float>() {
            @Override
            protected void updateItem(Float val, boolean empty) {
                super.updateItem(val, empty);
                setText((empty || val == null) ? null : nf.format(val));
            }
        });

        TableUtils.enableCopyOnCtrlC(refuelsTable);
        TableUtils.enableColumnsOrderPersistence(refuelsTable, RefuelsHistoryController.class, PREF_KEY_COLUMNS_ORDER);
    }

    /** Odświeża listę tankowań */
    @FXML
    private void handleRefresh() {
        loadRefuels();
    }

    /** Usuwa zaznaczone tankowanie */
    @FXML
    private void handleDeleteRefuel() {
        Refuel selected = refuelsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Brak wyboru", "Zaznacz wpis, który chcesz usunąć.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Usunąć wpis ID " + selected.getId() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                ApiClient.sendGetRequest("delete_refuel.php?id=" + selected.getId());
                loadRefuels();
            }
        });
    }

    /** Ładuje listę tankowań z API */
    private void loadRefuels() {
        try {
            String response = ApiClient.sendGetRequest("get_refuels.php?driver_id=" + driverId);
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            if ("success".equals(json.get("status").getAsString())) {
                JsonArray array = json.getAsJsonArray("refuels");
                refuelsTable.getItems().clear();
                for (int i = 0; i < array.size(); i++) {
                    JsonObject obj = array.get(i).getAsJsonObject();
                    String id         = obj.get("id").getAsString();
                    String date       = obj.get("refuel_date").getAsString();
                    float liters      = obj.get("fuel_amount").getAsFloat();
                    float cost        = obj.get("cost").getAsFloat();
                    int odometer      = obj.get("odometer").getAsInt();
                    refuelsTable.getItems().add(new Refuel(id, date, liters, cost, odometer));
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", json.get("message").getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd połączenia", "Nie można pobrać historii tankowań.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String text) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }
}
