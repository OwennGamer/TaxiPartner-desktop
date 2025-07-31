package com.partnertaxi.taxipartneradmin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.beans.property.SimpleStringProperty;
import com.partnertaxi.taxipartneradmin.TableUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


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

    // Labels showing summary/average values
    @FXML private Label saldoSumLabel;
    @FXML private Label turnoverSumLabel;
    @FXML private Label voucherSumLabel;
    @FXML private Label lotSumLabel;
    @FXML private Label cashSumLabel;
    @FXML private Label cardSumLabel;
    @FXML private Label fuelSumLabel;
    @FXML private Label zlPerKmAvgLabel;
    @FXML private Label fuelPerTurnoverAvgLabel;

    // Placeholder regions aligning with columns without totals
    @FXML private Region idPlaceholder;
    @FXML private Region namePlaceholder;
    @FXML private Region statusPlaceholder;
    @FXML private Region vehiclePlatePlaceholder;
    @FXML private Region fuelCostPlaceholder;
    @FXML private Region percentTurnoverPlaceholder;
    @FXML private Region cardCommissionPlaceholder;
    @FXML private Region partnerCommissionPlaceholder;
    @FXML private Region boltCommissionPlaceholder;
    @FXML private Region settlementLimitPlaceholder;
    @FXML private Region createdAtPlaceholder;

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
        voucherColumn.setId("voucherColumn");
        cardColumn.setId("cardColumn");
        cashColumn.setId("cashColumn");
        lotColumn.setId("lotColumn");
        turnoverColumn.setId("turnoverColumn");
        zlPerKmColumn.setId("zlPerKmColumn");
        fuelPerTurnoverColumn.setId("fuelPerTurnoverColumn");

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
        setupFloatColumn(voucherColumn,          nf);
        setupFloatColumn(cardColumn,             nf);
        setupFloatColumn(cashColumn,             nf);
        setupFloatColumn(lotColumn,              nf);
        setupFloatColumn(turnoverColumn,         nf);
        setupFloatColumn(zlPerKmColumn,          nf);
        setupFloatColumn(fuelPerTurnoverColumn,  nf);

        // 6) Wczytanie danych
        loadDrivers();

        // 7) Przywracamy zaznaczanie całych wierszy
        driversTable.getSelectionModel().setCellSelectionEnabled(false);
        driversTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

// 8) Ctrl+C: kopiujemy wartość focusowanej komórki
        TableUtils.enableCopyOnCtrlC(driversTable);

        // 9) Zapamiętujemy i odtwarzamy kolejność kolumn
        TableUtils.enableColumnsOrderPersistence(driversTable, DriversController.class, PREF_KEY_COLUMNS_ORDER);

        // 10) Bind summary row widths to corresponding table columns
        idPlaceholder.prefWidthProperty().bind(idColumn.widthProperty());
        namePlaceholder.prefWidthProperty().bind(nameColumn.widthProperty());
        saldoSumLabel.prefWidthProperty().bind(saldoColumn.widthProperty());
        statusPlaceholder.prefWidthProperty().bind(statusColumn.widthProperty());
        vehiclePlatePlaceholder.prefWidthProperty().bind(vehiclePlateColumn.widthProperty());
        fuelCostPlaceholder.prefWidthProperty().bind(fuelCostColumn.widthProperty());
        fuelSumLabel.prefWidthProperty().bind(fuelCostSumColumn.widthProperty());
        percentTurnoverPlaceholder.prefWidthProperty().bind(percentTurnoverColumn.widthProperty());
        cardCommissionPlaceholder.prefWidthProperty().bind(cardCommissionColumn.widthProperty());
        partnerCommissionPlaceholder.prefWidthProperty().bind(partnerCommissionColumn.widthProperty());
        boltCommissionPlaceholder.prefWidthProperty().bind(boltCommissionColumn.widthProperty());
        settlementLimitPlaceholder.prefWidthProperty().bind(settlementLimitColumn.widthProperty());
        voucherSumLabel.prefWidthProperty().bind(voucherColumn.widthProperty());
        cardSumLabel.prefWidthProperty().bind(cardColumn.widthProperty());
        cashSumLabel.prefWidthProperty().bind(cashColumn.widthProperty());
        lotSumLabel.prefWidthProperty().bind(lotColumn.widthProperty());
        turnoverSumLabel.prefWidthProperty().bind(turnoverColumn.widthProperty());
        zlPerKmAvgLabel.prefWidthProperty().bind(zlPerKmColumn.widthProperty());
        fuelPerTurnoverAvgLabel.prefWidthProperty().bind(fuelPerTurnoverColumn.widthProperty());
        createdAtPlaceholder.prefWidthProperty().bind(createdAtColumn.widthProperty());

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

            saldoSumLabel.setText(format.format(sumSaldo));
            turnoverSumLabel.setText(format.format(sumTurnover));
            voucherSumLabel.setText(format.format(sumVoucher));
            lotSumLabel.setText(format.format(sumLot));
            cashSumLabel.setText(format.format(sumCash));
            cardSumLabel.setText(format.format(sumCard));
            fuelSumLabel.setText(format.format(sumFuel));
            zlPerKmAvgLabel.setText(format.format(avgZlPerKm));
            fuelPerTurnoverAvgLabel.setText(format.format(avgFuelPerTurnover));

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
            st.setScene(new Scene(root));
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
            st.setScene(new Scene(root));
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
            st.setScene(new Scene(root));
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
            st.setScene(new Scene(root));
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
            st.setScene(new Scene(root));
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
            stage.setScene(new Scene(root));
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
