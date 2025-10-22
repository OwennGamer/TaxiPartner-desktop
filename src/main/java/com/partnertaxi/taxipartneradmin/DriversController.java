package com.partnertaxi.taxipartneradmin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.beans.property.SimpleStringProperty;
import com.partnertaxi.taxipartneradmin.TableUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;


public class DriversController {

    private static final String PREF_KEY_COLUMNS_ORDER = "driversTable.columnsOrder";

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
    @FXML private TableColumn<Driver, Float>   voucherColumn;
    @FXML private TableColumn<Driver, Float>   cardColumn;
    @FXML private TableColumn<Driver, Float>   cashColumn;
    @FXML private TableColumn<Driver, Float>   lotColumn;
    @FXML private TableColumn<Driver, Float>   turnoverColumn;
    @FXML private TableColumn<Driver, Float>   zlPerKmColumn;
    @FXML private TableColumn<Driver, Float>   fuelPerTurnoverColumn;
    @FXML private TableColumn<Driver, Void>    logoutColumn;


    @FXML
    public void initialize() {
        // allow horizontal scrolling for columns
        driversTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
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
        voucherColumn.setId("voucherColumn");
        cardColumn.setId("cardColumn");
        cashColumn.setId("cashColumn");
        lotColumn.setId("lotColumn");
        turnoverColumn.setId("turnoverColumn");
        zlPerKmColumn.setId("zlPerKmColumn");
        fuelPerTurnoverColumn.setId("fuelPerTurnoverColumn");
        logoutColumn.setId("logoutColumn");

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
        // Logout button column
        logoutColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Wyloguj");
            {
                btn.setOnAction(evt -> {
                    Driver d = getTableView().getItems().get(getIndex());
                    if (d != null && !d.isSummary()) {
                        handleRemoteLogout(d.getId());
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Driver d = getTableView().getItems().get(getIndex());
                    setGraphic(d != null && !d.isSummary() ? btn : null);
                }
            }
        });

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
        setupFloatColumn(voucherColumn,          nf);
        setupFloatColumn(cardColumn,             nf);
        setupFloatColumn(cashColumn,             nf);
        setupFloatColumn(lotColumn,              nf);
        setupFloatColumn(turnoverColumn,         nf);
        setupFloatColumn(zlPerKmColumn,          nf);
        setupFloatColumn(fuelPerTurnoverColumn,  nf);

        // 6) Sortowanie z podsumowaniem zawsze na dole
        driversTable.setSortPolicy(table -> {
            var items = table.getItems();
            if (items == null || items.isEmpty()) {
                return true;
            }

            Driver summary = null;
            for (int i = 0; i < items.size(); i++) {
                Driver driver = items.get(i);
                if (driver != null && driver.isSummary()) {
                    summary = items.remove(i);
                    break;
                }
            }

            if (table.getComparator() != null) {
                FXCollections.sort(items, table.getComparator());
            }

            if (summary != null) {
                items.add(summary);
            }

            return true;
        });

        // 7) Wczytanie danych
        loadDrivers();

        // 8) Przywracamy zaznaczanie całych wierszy
        driversTable.getSelectionModel().setCellSelectionEnabled(false);
        driversTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

// 9) Ctrl+C: kopiujemy wartość focusowanej komórki
        TableUtils.enableCopyOnCtrlC(driversTable);

        // 10) Zapamiętujemy i odtwarzamy kolejność kolumn
        TableUtils.enableColumnsOrderPersistence(driversTable, DriversController.class, PREF_KEY_COLUMNS_ORDER);

// 11) Row factory to style summary row
        driversTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Driver item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null && item.isSummary()) {
                    getStyleClass().add("table-row-summary");
                    setMouseTransparent(true);
                } else {
                    getStyleClass().remove("table-row-summary");
                    setMouseTransparent(false);
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

            // Totals for summary row
            float sumSaldo = 0f;
            float sumTurnover = 0f;
            float sumVoucher = 0f;
            float sumLot = 0f;
            float sumCash = 0f;
            float sumCard = 0f;
            float sumFuel = 0f;
            float totalZlPerKm = 0f;
            float totalFuelPerTurnover = 0f;
            int driverCount = 0;

            // default stats range: all available history up through today
            LocalDate start = LocalDate.of(1970, 1, 1); // include all rides
            LocalDate end = LocalDate.now().plusDays(1); // include today's rides
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDate = start.format(fmt);
            String endDate = end.format(fmt);

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

                // fetch statistics for current month
                DriverStats stats = ApiClient.getDriverStats(id, startDate, endDate);

                float voucher = 0f;
                float cardVal = 0f;
                float cashVal = 0f;
                float lotVal = 0f;
                float turnover = 0f;
                float zlPerKm = 0f;
                float fuelPerTurnover = 0f;

                if (stats != null) {
                    voucher = stats.getVoucher();
                    cardVal = stats.getCard();
                    cashVal = stats.getCash();
                    lotVal = stats.getLot();
                    turnover = stats.getTurnover();
                    if (stats.getKilometers() > 0) {
                        zlPerKm = turnover / stats.getKilometers();
                    }
                    if (turnover > 0) {
                        fuelPerTurnover = stats.getFuelSum() / turnover;
                    }
                }

                driversTable.getItems().add(new Driver(
                        id, fullName, saldo, status, "",
                        percentTurnover, fuelCost, cardComm, partComm,
                        boltComm, settLimit, createdAt, plate, fuelCostSum,
                        voucher, cardVal, cashVal, lotVal, turnover, zlPerKm, fuelPerTurnover
                ));


                driverCount++;
                try {
                    sumSaldo += Float.parseFloat(saldo.replace(',', '.'));
                } catch (Exception ignore) {}
                sumTurnover += turnover;
                sumVoucher  += voucher;
                sumLot      += lotVal;
                sumCash     += cashVal;
                sumCard     += cardVal;
                sumFuel     += fuelCostSum;
                totalZlPerKm += zlPerKm;
                totalFuelPerTurnover += fuelPerTurnover;
            }

            float avgZlPerKm = driverCount > 0 ? totalZlPerKm / driverCount : 0f;
            float avgFuelPerTurnover = driverCount > 0 ? totalFuelPerTurnover / driverCount : 0f;

            NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
            format.setMinimumFractionDigits(2);
            format.setMaximumFractionDigits(2);
            format.setGroupingUsed(false);

            Driver summary = new Driver(
                    "",
                    "SUMA",
                    format.format(sumSaldo),
                    "",
                    "",
                    0f,
                    0f,
                    0f,
                    0f,
                    0f,
                    0f,
                    "",
                    "",
                    sumFuel,
                    sumVoucher,
                    sumCard,
                    sumCash,
                    sumLot,
                    sumTurnover,
                    avgZlPerKm,
                    avgFuelPerTurnover,
                    true
            );
            driversTable.getItems().add(summary);

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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            st.setScene(scene);
            st.setMaximized(true);
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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            st.setScene(scene);
            st.setMaximized(true);
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

    public void handleRemoteLogout(String driverId) {
        ButtonType yes = new ButtonType("Tak", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("Nie", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Wylogować kierowcę " + driverId + "?", yes, no);
        c.setTitle("Potwierdzenie");
        c.setHeaderText(null);
        c.showAndWait().ifPresent(b -> {
            if (b == yes) {
                boolean success = ApiClient.logoutDriver(driverId);
                if (success) {
                    loadDrivers();
                    Alert info = new Alert(Alert.AlertType.INFORMATION,
                            "Kierowca " + driverId + " wylogowany.", ButtonType.OK);
                    info.setTitle("Komunikat");
                    info.setHeaderText(null);
                    info.showAndWait();
                } else {
                    showError("Błąd", "Nie udało się wylogować kierowcy.");
                }
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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            st.setScene(scene);
            st.setMaximized(true);
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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            st.setScene(scene);
            st.setMaximized(true);
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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            st.setScene(scene);
            st.setMaximized(true);
            st.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleShowWork(ActionEvent event) {
        Driver sel = driversTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Błąd", "Wybierz kierowcę");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("driver-work-view.fxml"));
            Parent root = loader.load();
            DriverWorkController ctrl = loader.getController();
            ctrl.setDriverId(sel.getId());
            Stage st = new Stage();
            st.setTitle("Praca: " + sel.getName());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            st.setScene(scene);
            st.setMaximized(true);
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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            st.setScene(scene);
            st.setMaximized(true);
            st.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openEmployeesView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("employees-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Pracownicy");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(
                    HelloApplication.class.getResource("style.css")
            ).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
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
