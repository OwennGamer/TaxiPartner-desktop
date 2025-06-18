package com.partnertaxi.taxipartneradmin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TablePosition;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

public class DriversController {

    private static final String PREF_KEY_COLUMNS_ORDER = "driversTable.columnsOrder";
    private final Preferences prefs = Preferences.userNodeForPackage(DriversController.class);

    @FXML private TableView<Driver> driversTable;
    @FXML private TableColumn<Driver, String>  idColumn;
    @FXML private TableColumn<Driver, String>  nameColumn;
    @FXML private TableColumn<Driver, String>  saldoColumn;
    @FXML private TableColumn<Driver, String>  statusColumn;
    @FXML private TableColumn<Driver, String>  fuelCostColumn;
    @FXML private TableColumn<Driver, Float>   fuelCostSumColumn;
    @FXML private TableColumn<Driver, String>  createdAtColumn;
    @FXML private TableColumn<Driver, Float>   percentTurnoverColumn;
    @FXML private TableColumn<Driver, Float>   cardCommissionColumn;
    @FXML private TableColumn<Driver, Float>   partnerCommissionColumn;
    @FXML private TableColumn<Driver, Float>   boltCommissionColumn;
    @FXML private TableColumn<Driver, Float>   settlementLimitColumn;
    @FXML private TableColumn<Driver, String>  vehiclePlateColumn;

    @FXML
    public void initialize() {
        // 1) Nadajemy ID kolumnom
        idColumn.setId("idColumn");
        nameColumn.setId("nameColumn");
        saldoColumn.setId("saldoColumn");
        statusColumn.setId("statusColumn");
        vehiclePlateColumn.setId("vehiclePlateColumn");
        fuelCostColumn.setId("fuelCostColumn");
        fuelCostSumColumn.setId("fuelCostSumColumn");
        percentTurnoverColumn.setId("percentTurnoverColumn");
        cardCommissionColumn.setId("cardCommissionColumn");
        partnerCommissionColumn.setId("partnerCommissionColumn");
        boltCommissionColumn.setId("boltCommissionColumn");
        settlementLimitColumn.setId("settlementLimitColumn");
        createdAtColumn.setId("createdAtColumn");

        // 2) Formatter liczb z przecinkiem
        final NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(false);

        // 3) Tekstowe kolumny: PropertyValueFactory + własny cellFactory z highlight’em
        setupTextColumn(idColumn, "id");
        setupTextColumn(nameColumn, "name");
        setupTextColumn(statusColumn, "status");
        setupTextColumn(vehiclePlateColumn, "vehiclePlate");
        setupTextColumn(fuelCostColumn, "fuelCostText");
        setupTextColumn(createdAtColumn, "createdAt");

        // 4) Saldo: parsujemy raw string i formatujemy
        saldoColumn.setCellValueFactory(data -> {
            String raw = data.getValue().getSaldo();
            try {
                float v = Float.parseFloat(raw.replace(',', '.'));
                return new SimpleStringProperty(nf.format(v));
            } catch (Exception e) {
                return new SimpleStringProperty(raw);
            }
        });
        saldoColumn.setCellFactory(col -> {
            TableCell<Driver, String> cell = new TableCell<>() {
                @Override protected void updateItem(String text, boolean empty) {
                    super.updateItem(text, empty);
                    setText(empty ? null : text);
                }
            };
            addFocusHighlight(cell);
            return cell;
        });

        // 5) Kolumny numeryczne: PropertyValueFactory + formatting + highlight
        setupFloatColumn(fuelCostSumColumn,      nf);
        setupFloatColumn(percentTurnoverColumn,  nf);
        setupFloatColumn(cardCommissionColumn,   nf);
        setupFloatColumn(partnerCommissionColumn, nf);
        setupFloatColumn(boltCommissionColumn,   nf);
        setupFloatColumn(settlementLimitColumn,  nf);

        // 6) Wczytanie danych i przywrócenie układu kolumn
        loadDrivers();
        restoreColumnsOrder();

        // 7) Przywracamy zaznaczanie całych wierszy
        driversTable.getSelectionModel().setCellSelectionEnabled(false);
        driversTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // 8) Ctrl+C: kopiujemy wartość focusowanej komórki, w formacie z przecinkiem
        driversTable.setOnKeyPressed(evt -> {
            if (evt.isControlDown() && evt.getCode() == KeyCode.C) {
                TablePosition<Driver, ?> pos = driversTable.getFocusModel().getFocusedCell();
                if (pos != null && pos.getRow() >= 0) {
                    Object cellValue = driversTable
                            .getColumns()
                            .get(pos.getColumn())
                            .getCellData(pos.getRow());
                    String str;
                    if (cellValue instanceof Number) {
                        str = nf.format(((Number) cellValue).doubleValue());
                    } else {
                        str = cellValue == null ? "" : cellValue.toString();
                    }
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(str);
                    clipboard.setContent(content);
                }
            }
        });

        // 9) Słuchamy permutacji/replacement kolumn, by zapisać układ
        driversTable.getColumns().addListener((ListChangeListener<TableColumn<Driver, ?>>) change -> {
            while (change.next()) {
                if (change.wasPermutated() || change.wasReplaced()) {
                    saveColumnsOrder();
                }
            }
        });
    }

    // Helper dla tekstowych kolumn
    private <T> void setupTextColumn(TableColumn<Driver, T> col, String propertyName) {
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        col.setCellFactory(c -> {
            TableCell<Driver, T> cell = new TableCell<>() {
                @Override protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                }
            };
            addFocusHighlight(cell);
            return cell;
        });
    }

    // Helper dla numerycznych kolumn z formatowaniem
    private void setupFloatColumn(TableColumn<Driver, Float> col, NumberFormat nf) {
        col.setCellValueFactory(new PropertyValueFactory<>(col.getId().replace("Column", "")));
        col.setCellFactory(c -> {
            TableCell<Driver, Float> cell = new TableCell<>() {
                @Override protected void updateItem(Float val, boolean empty) {
                    super.updateItem(val, empty);
                    setText((empty || val == null) ? null : nf.format(val));
                }
            };
            addFocusHighlight(cell);
            return cell;
        });
    }

    // Dodaje listener, który podświetli komórkę na żółto, gdy jest focusowana
    private void addFocusHighlight(TableCell<?, ?> cell) {
        cell.focusedProperty().addListener((obs, was, isNow) -> {
            if (isNow) {
                cell.setStyle("-fx-background-color: lightyellow;");
            } else {
                cell.setStyle("");
            }
        });
    }

    private void saveColumnsOrder() {
        StringBuilder sb = new StringBuilder();
        for (TableColumn<Driver, ?> col : driversTable.getColumns()) {
            String id = col.getId();
            if (id != null) {
                if (sb.length() > 0) sb.append(",");
                sb.append(id);
            }
        }
        prefs.put(PREF_KEY_COLUMNS_ORDER, sb.toString());
    }

    private void restoreColumnsOrder() {
        String order = prefs.get(PREF_KEY_COLUMNS_ORDER, null);
        if (order == null) return;
        String[] ids = order.split(",");
        ObservableList<TableColumn<Driver, ?>> current = driversTable.getColumns();
        List<TableColumn<Driver, ?>> newOrder = new ArrayList<>();
        for (String id : ids) {
            current.stream()
                    .filter(c -> id.equals(c.getId()))
                    .findFirst()
                    .ifPresent(newOrder::add);
        }
        for (TableColumn<Driver, ?> col : current) {
            if (!newOrder.contains(col)) newOrder.add(col);
        }
        driversTable.getColumns().setAll(newOrder);
    }

    private void loadDrivers() {
        try {
            String resp = ApiClient.sendGetRequest("get_drivers.php");
            JsonObject json = JsonParser.parseString(resp).getAsJsonObject();
            if (!"success".equals(json.get("status").getAsString())) {
                showError("Błąd", json.get("message").getAsString());
                return;
            }
            JsonArray arr = json.getAsJsonArray("drivers");
            driversTable.getItems().clear();
            for (int i = 0; i < arr.size(); i++) {
                JsonObject o = arr.get(i).getAsJsonObject();
                String id        = o.get("id").getAsString();
                String fullName  = o.get("imie").getAsString() + " " + o.get("nazwisko").getAsString();
                String saldo     = o.get("saldo").getAsString();
                String status    = o.has("status") ? o.get("status").getAsString() : "";
                String createdAt = o.has("created_at") ? o.get("created_at").getAsString() : "";
                String plate     = o.has("vehiclePlate") && !o.get("vehiclePlate").isJsonNull()
                        ? o.get("vehiclePlate").getAsString() : "";

                float percentTurnover = o.has("percentTurnover")   ? o.get("percentTurnover").getAsFloat()   : 0f;
                float fuelCost        = o.has("fuelCost")          ? o.get("fuelCost").getAsFloat()          : 0f;
                float fuelCostSum     = o.has("fuelCostSum") && !o.get("fuelCostSum").isJsonNull()
                        ? o.get("fuelCostSum").getAsFloat() : 0f;
                float cardComm        = o.has("cardCommission")    ? o.get("cardCommission").getAsFloat()    : 0f;
                float partComm        = o.has("partnerCommission") ? o.get("partnerCommission").getAsFloat() : 0f;
                float boltComm        = o.has("boltCommission")    ? o.get("boltCommission").getAsFloat()    : 0f;
                float settLimit       = o.has("settlementLimit")   ? o.get("settlementLimit").getAsFloat()   : 0f;

                driversTable.getItems().add(new Driver(
                        id, fullName, saldo, status, "",
                        percentTurnover, fuelCost, cardComm, partComm,
                        boltComm, settLimit, createdAt, plate, fuelCostSum
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Błąd połączenia", "Nie można się połączyć z serwerem.");
        }
    }

    @FXML
    public void handleAddDriver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-driver-view.fxml"));
            Parent root = loader.load();
            Stage st = new Stage();
            st.setTitle("Dodaj kierowcę");
            st.setScene(new Scene(root));
            st.showAndWait();
            loadDrivers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditDriver(ActionEvent event) {
        Driver sel = driversTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Błąd", "Wybierz kierowcę");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-driver-view.fxml"));
            Parent root = loader.load();
            EditDriverController ctrl = loader.getController();
            ctrl.setDriver(sel);
            Stage st = new Stage();
            st.setTitle("Edytuj kierowcę");
            st.setScene(new Scene(root));
            st.showAndWait();
            loadDrivers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteDriver(ActionEvent event) {
        Driver sel = driversTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Błąd", "Wybierz kierowcę");
            return;
        }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Usunąć kierowcę " + sel.getName() + "?", ButtonType.OK, ButtonType.CANCEL);
        c.setHeaderText(null);
        c.showAndWait().ifPresent(b -> {
            if (b == ButtonType.OK) {
                ApiClient.deleteDriver(sel.getId());
                loadDrivers();
            }
        });
    }

    @FXML
    public void handleChangeSaldo(ActionEvent event) {
        Driver sel = driversTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Błąd", "Wybierz kierowcę");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("change-saldo-view.fxml"));
            Parent root = loader.load();
            ChangeSaldoController ctrl = loader.getController();
            ctrl.setDriverId(sel.getId());
            ctrl.setOnSuccess(this::loadDrivers);
            Stage st = new Stage();
            st.setTitle("Zmień saldo");
            st.setScene(new Scene(root));
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleShowHistory(ActionEvent event) {
        Driver sel = driversTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Błąd", "Wybierz kierowcę");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("history-view.fxml"));
            Parent root = loader.load();
            HistoryController ctrl = loader.getController();
            ctrl.setDriverId(sel.getId());
            Stage st = new Stage();
            st.setTitle("Historia: " + sel.getName());
            st.setScene(new Scene(root));
            st.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleShowRefuels(ActionEvent event) {
        Driver sel = driversTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Błąd", "Wybierz kierowcę");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("refuels-history-view.fxml"));
            Parent root = loader.load();
            RefuelsHistoryController ctrl = loader.getController();
            ctrl.setDriverId(sel.getId());
            Stage st = new Stage();
            st.setTitle("Tankowania: " + sel.getName());
            st.setScene(new Scene(root));
            st.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRefreshDrivers(ActionEvent event) {
        loadDrivers();
    }

    @FXML
    public void openFleetView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fleet-view.fxml"));
            Parent root = loader.load();
            Stage st = new Stage();
            st.setTitle("Flota pojazdów");
            st.setScene(new Scene(root));
            st.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
