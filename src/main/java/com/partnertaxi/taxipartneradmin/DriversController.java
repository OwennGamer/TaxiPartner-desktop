package com.partnertaxi.taxipartneradmin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.beans.property.SimpleStringProperty;
import com.partnertaxi.taxipartneradmin.TableUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Map;
import java.util.List;
import java.time.temporal.TemporalAdjusters;


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
    @FXML private TableColumn<Driver, Float>   voucherCurrentColumn;
    @FXML private TableColumn<Driver, Float>   voucherPreviousColumn;
    @FXML private TableColumn<Driver, Float>   cardColumn;
    @FXML private TableColumn<Driver, Float>   cashColumn;
    @FXML private TableColumn<Driver, Float>   lotColumn;
    @FXML private TableColumn<Driver, Float>   turnoverColumn;
    @FXML private TableColumn<Driver, Float>   zlPerKmColumn;
    @FXML private TableColumn<Driver, Float>   fuelPerTurnoverColumn;
    @FXML private TableColumn<Driver, Void>    logoutColumn;
    @FXML private ComboBox<DateFilterOption>   dateFilterCombo;
    @FXML private HBox                         customRangeBox;
    @FXML private DatePicker                   startDatePicker;
    @FXML private DatePicker                   endDatePicker;
    @FXML private HBox                         summaryRow;
    @FXML private StackPane                    idPlaceholder;
    @FXML private StackPane                    namePlaceholder;
    @FXML private Label                        nameSummaryLabel;
    @FXML private StackPane                    saldoSumContainer;
    @FXML private Label                        saldoSumValue;
    @FXML private StackPane                    statusPlaceholder;
    @FXML private StackPane                    vehiclePlatePlaceholder;
    @FXML private StackPane                    fuelCostPlaceholder;
    @FXML private StackPane                    fuelSumContainer;
    @FXML private Label                        fuelSumValue;
    @FXML private StackPane                    percentTurnoverPlaceholder;
    @FXML private StackPane                    cardCommissionPlaceholder;
    @FXML private StackPane                    partnerCommissionPlaceholder;
    @FXML private StackPane                    boltCommissionPlaceholder;
    @FXML private StackPane                    settlementLimitPlaceholder;
    @FXML private StackPane                    voucherCurrentContainer;
    @FXML private Label                        voucherCurrentSumValue;
    @FXML private StackPane                    voucherPreviousContainer;
    @FXML private Label                        voucherPreviousSumValue;
    @FXML private StackPane                    cardSumContainer;
    @FXML private Label                        cardSumValue;
    @FXML private StackPane                    cashSumContainer;
    @FXML private Label                        cashSumValue;
    @FXML private StackPane                    lotSumContainer;
    @FXML private Label                        lotSumValue;
    @FXML private StackPane                    turnoverSumContainer;
    @FXML private Label                        turnoverSumValue;
    @FXML private StackPane                    zlPerKmAvgContainer;
    @FXML private Label                        zlPerKmAvgValue;
    @FXML private StackPane                    fuelPerTurnoverAvgContainer;
    @FXML private Label                        fuelPerTurnoverAvgValue;
    @FXML private StackPane                    createdAtPlaceholder;
    @FXML private StackPane                    logoutPlaceholder;

    private LocalDate statsStartDate;
    private LocalDate statsEndDate;
    private DateFilterOption currentDateFilter = DateFilterOption.CURRENT_MONTH;
    private LocalDate customStartDate;
    private LocalDate customEndDate;
    private boolean updatingCustomRange;
    private final Map<String, Region> summaryNodes = new HashMap<>();
    private final List<Region> summaryNodesDefaultOrder = new ArrayList<>();

    private enum DateFilterOption {
        PREVIOUS_YEAR,
        PREVIOUS_MONTH,
        CURRENT_YEAR,
        CURRENT_MONTH,
        CUSTOM
    }


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
        voucherCurrentColumn.setId("voucherCurrentColumn");
        voucherPreviousColumn.setId("voucherPreviousColumn");
        cardColumn.setId("cardColumn");
        cashColumn.setId("cashColumn");
        lotColumn.setId("lotColumn");
        turnoverColumn.setId("turnoverColumn");
        zlPerKmColumn.setId("zlPerKmColumn");
        fuelPerTurnoverColumn.setId("fuelPerTurnoverColumn");
        logoutColumn.setId("logoutColumn");

        // 2) Formatter liczb z przecinkiem
        final NumberFormat nf = createNumberFormat();

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
        setupFloatColumn(voucherCurrentColumn,   nf);
        setupFloatColumn(voucherPreviousColumn, nf);
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

        // 7) Konfiguracja filtrów dat

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


        configureSummaryRow();
        setupDateFilters();
    }



    // Helper dla tekstowych kolumn
    private <T> void setupTextColumn(TableColumn<Driver, T> col, String propertyName) {
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        if ("id".equals(propertyName)) {
            @SuppressWarnings("unchecked")
            TableColumn<Driver, String> idCol = (TableColumn<Driver, String>) col;
            idCol.setComparator(TableUtils.naturalIdComparator());
        }
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

    private NumberFormat createNumberFormat() {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(false);
        return nf;
    }

    private void configureSummaryRow() {
        if (summaryRow == null) {
            return;
        }

        summaryNodes.clear();
        summaryNodesDefaultOrder.clear();

        registerSummaryNode("idColumn", idPlaceholder);
        registerSummaryNode("nameColumn", namePlaceholder);
        registerSummaryNode("saldoColumn", saldoSumContainer);
        registerSummaryNode("statusColumn", statusPlaceholder);
        registerSummaryNode("vehiclePlateColumn", vehiclePlatePlaceholder);
        registerSummaryNode("fuelCostColumn", fuelCostPlaceholder);
        registerSummaryNode("fuelCostSumColumn", fuelSumContainer);
        registerSummaryNode("percentTurnoverColumn", percentTurnoverPlaceholder);
        registerSummaryNode("cardCommissionColumn", cardCommissionPlaceholder);
        registerSummaryNode("partnerCommissionColumn", partnerCommissionPlaceholder);
        registerSummaryNode("boltCommissionColumn", boltCommissionPlaceholder);
        registerSummaryNode("settlementLimitColumn", settlementLimitPlaceholder);
        registerSummaryNode("voucherCurrentColumn", voucherCurrentContainer);
        registerSummaryNode("voucherPreviousColumn", voucherPreviousContainer);
        registerSummaryNode("cardColumn", cardSumContainer);
        registerSummaryNode("cashColumn", cashSumContainer);
        registerSummaryNode("lotColumn", lotSumContainer);
        registerSummaryNode("turnoverColumn", turnoverSumContainer);
        registerSummaryNode("zlPerKmColumn", zlPerKmAvgContainer);
        registerSummaryNode("fuelPerTurnoverColumn", fuelPerTurnoverAvgContainer);
        registerSummaryNode("createdAtColumn", createdAtPlaceholder);
        registerSummaryNode("logoutColumn", logoutPlaceholder);

        bindColumnWidth(idPlaceholder, idColumn);
        bindColumnWidth(namePlaceholder, nameColumn);
        bindColumnWidth(saldoSumContainer, saldoColumn);
        bindColumnWidth(statusPlaceholder, statusColumn);
        bindColumnWidth(vehiclePlatePlaceholder, vehiclePlateColumn);
        bindColumnWidth(fuelCostPlaceholder, fuelCostColumn);
        bindColumnWidth(fuelSumContainer, fuelCostSumColumn);
        bindColumnWidth(percentTurnoverPlaceholder, percentTurnoverColumn);
        bindColumnWidth(cardCommissionPlaceholder, cardCommissionColumn);
        bindColumnWidth(partnerCommissionPlaceholder, partnerCommissionColumn);
        bindColumnWidth(boltCommissionPlaceholder, boltCommissionColumn);
        bindColumnWidth(settlementLimitPlaceholder, settlementLimitColumn);
        bindColumnWidth(voucherCurrentContainer, voucherCurrentColumn);
        bindColumnWidth(voucherPreviousContainer, voucherPreviousColumn);
        bindColumnWidth(cardSumContainer, cardColumn);
        bindColumnWidth(cashSumContainer, cashColumn);
        bindColumnWidth(lotSumContainer, lotColumn);
        bindColumnWidth(turnoverSumContainer, turnoverColumn);
        bindColumnWidth(zlPerKmAvgContainer, zlPerKmColumn);
        bindColumnWidth(fuelPerTurnoverAvgContainer, fuelPerTurnoverColumn);
        bindColumnWidth(createdAtPlaceholder, createdAtColumn);
        bindColumnWidth(logoutPlaceholder, logoutColumn);

        TableUtils.bindHorizontalScroll(driversTable, summaryRow);
        updateSummaryRowOrder();

        driversTable.getColumns().addListener((ListChangeListener<TableColumn<Driver, ?>>) change -> {
            while (change.next()) {
                updateSummaryRowOrder();
            }
        });
    }

    private void registerSummaryNode(String columnId, Region node) {
        if (columnId == null || node == null) {
            return;
        }
        summaryNodes.put(columnId, node);
        if (!summaryNodesDefaultOrder.contains(node)) {
            summaryNodesDefaultOrder.add(node);
        }
    }

    private void bindColumnWidth(Region node, TableColumn<Driver, ?> column) {
        if (node == null || column == null) {
            return;
        }
        node.minWidthProperty().bind(column.widthProperty());
        node.prefWidthProperty().bind(column.widthProperty());
        node.maxWidthProperty().bind(column.widthProperty());
    }

    private void updateSummaryRowOrder() {
        if (summaryRow == null) {
            return;
        }
        List<Region> ordered = new ArrayList<>();
        for (TableColumn<Driver, ?> column : driversTable.getColumns()) {
            Region node = summaryNodes.get(column.getId());
            if (node != null && !ordered.contains(node)) {
                ordered.add(node);
            }
        }
        for (Region node : summaryNodesDefaultOrder) {
            if (!ordered.contains(node)) {
                ordered.add(node);
            }
        }
        summaryRow.getChildren().setAll(ordered);
    }

    private void updateSummaryRowValues(NumberFormat format,
                                        float sumSaldo,
                                        float sumFuel,
                                        float sumVoucherCurrent,
                                        float sumVoucherPrevious,
                                        float sumCard,
                                        float sumCash,
                                        float sumLot,
                                        float sumTurnover,
                                        float avgZlPerKm,
                                        float avgFuelPerTurnover) {
        if (nameSummaryLabel != null) {
            nameSummaryLabel.setText("SUMA");
        }
        if (saldoSumValue != null) {
            saldoSumValue.setText(format.format(sumSaldo));
        }
        if (fuelSumValue != null) {
            fuelSumValue.setText(format.format(sumFuel));
        }
        if (voucherCurrentSumValue != null) {
            voucherCurrentSumValue.setText(format.format(sumVoucherCurrent));
        }
        if (voucherPreviousSumValue != null) {
            voucherPreviousSumValue.setText(format.format(sumVoucherPrevious));
        }
        if (cardSumValue != null) {
            cardSumValue.setText(format.format(sumCard));
        }
        if (cashSumValue != null) {
            cashSumValue.setText(format.format(sumCash));
        }
        if (lotSumValue != null) {
            lotSumValue.setText(format.format(sumLot));
        }
        if (turnoverSumValue != null) {
            turnoverSumValue.setText(format.format(sumTurnover));
        }
        if (zlPerKmAvgValue != null) {
            zlPerKmAvgValue.setText(format.format(avgZlPerKm));
        }
        if (fuelPerTurnoverAvgValue != null) {
            fuelPerTurnoverAvgValue.setText(format.format(avgFuelPerTurnover));
        }
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


    private void setupDateFilters() {
        if (dateFilterCombo == null) {
            loadDrivers();
            return;
        }

        dateFilterCombo.setItems(FXCollections.observableArrayList(DateFilterOption.values()));
        dateFilterCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(DateFilterOption option) {
                if (option == null) {
                    return "";
                }
                return switch (option) {
                    case PREVIOUS_YEAR -> "Poprzedni rok";
                    case PREVIOUS_MONTH -> "Poprzedni miesiąc";
                    case CURRENT_YEAR -> "Aktualny rok";
                    case CURRENT_MONTH -> "Aktualny miesiąc";
                    case CUSTOM -> "Wybierz zakres dat";
                };
            }

            @Override
            public DateFilterOption fromString(String string) {
                return null;
            }
        });

        if (customRangeBox != null) {
            customRangeBox.setVisible(false);
            customRangeBox.setManaged(false);
        }

        if (startDatePicker != null) {
            startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> handleCustomRangeChange());
        }
        if (endDatePicker != null) {
            endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> handleCustomRangeChange());
        }

        dateFilterCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldOpt, newOpt) -> {
            if (newOpt != null) {
                applyDateFilter(newOpt);
            }
        });

        dateFilterCombo.getSelectionModel().select(currentDateFilter);
    }

    private void applyDateFilter(DateFilterOption option) {
        currentDateFilter = option;
        switch (option) {
            case PREVIOUS_YEAR -> {
                setCustomRangeVisible(false);
                LocalDate now = LocalDate.now().minusYears(1);
                LocalDate start = now.with(TemporalAdjusters.firstDayOfYear());
                LocalDate end = now.with(TemporalAdjusters.lastDayOfYear());
                updateStatsRange(start, end);
            }
            case PREVIOUS_MONTH -> {
                setCustomRangeVisible(false);
                LocalDate month = LocalDate.now().minusMonths(1);
                LocalDate start = month.withDayOfMonth(1);
                LocalDate end = month.withDayOfMonth(month.lengthOfMonth());
                updateStatsRange(start, end);
            }
            case CURRENT_YEAR -> {
                setCustomRangeVisible(false);
                LocalDate now = LocalDate.now();
                LocalDate start = now.with(TemporalAdjusters.firstDayOfYear());
                updateStatsRange(start, now);
            }
            case CURRENT_MONTH -> {
                setCustomRangeVisible(false);
                LocalDate now = LocalDate.now();
                LocalDate start = now.withDayOfMonth(1);
                updateStatsRange(start, now);
            }
            case CUSTOM -> {
                setCustomRangeVisible(true);
                if (customStartDate == null || customEndDate == null) {
                    LocalDate now = LocalDate.now();
                    customStartDate = now.withDayOfMonth(1);
                    customEndDate = now;
                }
                setCustomPickerValues(customStartDate, customEndDate);
                updateStatsRange(customStartDate, customEndDate);
            }
        }
    }

    private void setCustomRangeVisible(boolean visible) {
        if (customRangeBox != null) {
            customRangeBox.setVisible(visible);
            customRangeBox.setManaged(visible);
        }
    }

    private void setCustomPickerValues(LocalDate start, LocalDate end) {
        if (startDatePicker == null || endDatePicker == null) {
            return;
        }
        updatingCustomRange = true;
        startDatePicker.setValue(start);
        endDatePicker.setValue(end);
        updatingCustomRange = false;
    }

    private void handleCustomRangeChange() {
        if (updatingCustomRange || currentDateFilter != DateFilterOption.CUSTOM) {
            return;
        }
        LocalDate start = startDatePicker != null ? startDatePicker.getValue() : null;
        LocalDate end = endDatePicker != null ? endDatePicker.getValue() : null;
        if (start == null || end == null) {
            return;
        }
        if (end.isBefore(start)) {
            showError("Błędny zakres dat", "Data końcowa nie może być wcześniejsza niż początkowa.");
            updatingCustomRange = true;
            startDatePicker.setValue(customStartDate);
            endDatePicker.setValue(customEndDate);
            updatingCustomRange = false;
            return;
        }
        customStartDate = start;
        customEndDate = end;
        updateStatsRange(start, end);
    }

    private void updateStatsRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return;
        }
        if (end.isBefore(start)) {
            showError("Błędny zakres dat", "Data końcowa nie może być wcześniejsza niż początkowa.");
            return;
        }
        if (start.equals(statsStartDate) && end.equals(statsEndDate)) {
            return;
        }
        statsStartDate = start;
        statsEndDate = end;
        loadDrivers();
    }



    private void loadDrivers() {
        try {
            String resp = ApiClient.sendGetRequest("get_drivers.php");
            JsonObject json = JsonParser.parseString(resp).getAsJsonObject();
            if (!"success".equals(json.get("status").getAsString())) {
                showError("Błąd", json.get("message").getAsString());
                updateSummaryRowValues(createNumberFormat(), 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
                return;
            }
            JsonArray arr = json.getAsJsonArray("drivers");
            driversTable.getItems().clear();

            // Totals for summary row
            float sumSaldo = 0f;
            float sumTurnover = 0f;
            float sumVoucherCurrent = 0f;
            float sumVoucherPrevious = 0f;
            float sumLot = 0f;
            float sumCash = 0f;
            float sumCard = 0f;
            float sumFuel = 0f;
            float totalZlPerKm = 0f;
            float totalFuelPerTurnover = 0f;
            int driverCount = 0;

            LocalDate effectiveStart = statsStartDate != null ? statsStartDate : LocalDate.of(1970, 1, 1);
            LocalDate effectiveEnd = statsEndDate != null ? statsEndDate : LocalDate.now();
            if (effectiveEnd.isBefore(effectiveStart)) {
                showError("Błędny zakres dat", "Data końcowa nie może być wcześniejsza niż początkowa.");
                updateSummaryRowValues(createNumberFormat(), 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
                return;
            }
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDate = effectiveStart.format(fmt);
            String endDate = effectiveEnd.plusDays(1).format(fmt);

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
                float fuelCostSum     = o.has("fuelCostSum") && !o.get("fuelCostSum").isJsonNull()
                        ? o.get("fuelCostSum").getAsFloat() : 0f;
                float cardComm        = o.has("cardCommission")    ? o.get("cardCommission").getAsFloat()    : 0f;
                float partComm        = o.has("partnerCommission") ? o.get("partnerCommission").getAsFloat() : 0f;
                float boltComm        = o.has("boltCommission")    ? o.get("boltCommission").getAsFloat()    : 0f;
                float settLimit       = o.has("settlementLimit")   ? o.get("settlementLimit").getAsFloat()   : 0f;
                float voucherCurrent  = o.has("voucher_current_amount") && !o.get("voucher_current_amount").isJsonNull()
                        ? o.get("voucher_current_amount").getAsFloat() : 0f;
                float voucherPrevious = o.has("voucher_previous_amount") && !o.get("voucher_previous_amount").isJsonNull()
                        ? o.get("voucher_previous_amount").getAsFloat() : 0f;

                // fetch statistics for current month
                DriverStats stats = ApiClient.getDriverStats(id, startDate, endDate);

                float cardVal = 0f;
                float cashVal = 0f;
                float lotVal = 0f;
                float turnover = 0f;
                float zlPerKm = 0f;
                float fuelPerTurnover = 0f;
                float fuelSum = 0f;

                if (stats != null) {
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
                    fuelSum = stats.getFuelSum();
                }

                driversTable.getItems().add(new Driver(
                        id, fullName, saldo, status, "",
                        percentTurnover, fuelCostSum, cardComm, partComm,
                        boltComm, settLimit, createdAt, plate, fuelSum,
                        voucherCurrent, voucherPrevious, cardVal, cashVal, lotVal, turnover, zlPerKm, fuelPerTurnover
                ));


                driverCount++;
                try {
                    sumSaldo += Float.parseFloat(saldo.replace(',', '.'));
                } catch (Exception ignore) {}
                sumTurnover += turnover;
                sumVoucherCurrent  += voucherCurrent;
                sumVoucherPrevious += voucherPrevious;
                sumLot      += lotVal;
                sumCash     += cashVal;
                sumCard     += cardVal;
                sumFuel     += fuelSum;
                totalZlPerKm += zlPerKm;
                totalFuelPerTurnover += fuelPerTurnover;
            }

            float avgZlPerKm = driverCount > 0 ? totalZlPerKm / driverCount : 0f;
            float avgFuelPerTurnover = driverCount > 0 ? totalFuelPerTurnover / driverCount : 0f;

            NumberFormat format = createNumberFormat();

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
                    sumVoucherCurrent,
                    sumVoucherPrevious,
                    sumCard,
                    sumCash,
                    sumLot,
                    sumTurnover,
                    avgZlPerKm,
                    avgFuelPerTurnover,
                    true
            );
            driversTable.getItems().add(summary);
            updateSummaryRowValues(format,
                    sumSaldo,
                    sumFuel,
                    sumVoucherCurrent,
                    sumVoucherPrevious,
                    sumCard,
                    sumCash,
                    sumLot,
                    sumTurnover,
                    avgZlPerKm,
                    avgFuelPerTurnover);

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Błąd połączenia", "Nie można się połączyć z serwerem.");
            updateSummaryRowValues(createNumberFormat(), 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
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
