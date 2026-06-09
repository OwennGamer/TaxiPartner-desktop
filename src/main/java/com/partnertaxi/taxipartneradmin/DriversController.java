package com.partnertaxi.taxipartneradmin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.beans.property.SimpleFloatProperty;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.time.temporal.TemporalAdjusters;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class DriversController {

    private static final String PREF_KEY_COLUMNS_ORDER = "driversTable.columnsOrder";

    @FXML private TableView<Driver> driversTable;
    @FXML private TableColumn<Driver, String>  idColumn;
    @FXML private TableColumn<Driver, String>  nameColumn;
    @FXML private TableColumn<Driver, Float>   saldoColumn;
    @FXML private TableColumn<Driver, String>  statusColumn;
    @FXML private TableColumn<Driver, String>  fuelCostColumn;
    @FXML private TableColumn<Driver, Float>   fuelCostSumColumn;
    @FXML private TableColumn<Driver, String>  createdAtColumn;
    @FXML private TableColumn<Driver, Float>   percentTurnoverColumn;
    @FXML private TableColumn<Driver, Float>   cardCommissionColumn;
    @FXML private TableColumn<Driver, Float>   partnerCommissionColumn;
    @FXML private TableColumn<Driver, Float>   boltCommissionColumn;
    @FXML private TableColumn<Driver, Float>   settlementLimitColumn;
    @FXML private TableColumn<Driver, Float>   fixedCostsColumn;
    @FXML private TableColumn<Driver, String>  vehiclePlateColumn;
    @FXML private TableColumn<Driver, Float>   voucherCurrentColumn;
    @FXML private TableColumn<Driver, Float>   voucherPreviousColumn;
    @FXML private TableColumn<Driver, Float>   voucherColumn;
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
    @FXML private Pane                         summaryRowViewport;
    @FXML private HBox                         summaryRow;
    @FXML private Label                        saldoSumLabel;
    @FXML private Label                        fuelSumLabel;
    @FXML private Label                        fixedCostsSumLabel;
    @FXML private Label                        percentTurnoverAvgLabel;
    @FXML private Label                        cardCommissionSumLabel;
    @FXML private Label                        partnerCommissionSumLabel;
    @FXML private Label                        boltCommissionSumLabel;
    @FXML private Label                        settlementLimitSumLabel;
    @FXML private Label                        voucherCurrentSumLabel;
    @FXML private Label                        voucherPreviousSumLabel;
    @FXML private Label                        voucherSumLabel;
    @FXML private Label                        cardSumLabel;
    @FXML private Label                        cashSumLabel;
    @FXML private Label                        lotSumLabel;
    @FXML private Label                        turnoverSumLabel;
    @FXML private Label                        zlPerKmAvgLabel;
    @FXML private Label                        fuelPerTurnoverAvgLabel;


    private LocalDate statsStartDate;
    private LocalDate statsEndDate;
    private DateFilterOption currentDateFilter = DateFilterOption.CURRENT_MONTH;
    private LocalDate customStartDate;
    private LocalDate customEndDate;
    private boolean updatingCustomRange;
    private ListChangeListener<Driver> summaryListener;
    private boolean updatingSummaryRow;

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
        fixedCostsColumn.setId("fixedCostsColumn");
        createdAtColumn.setId("createdAtColumn");
        voucherCurrentColumn.setId("voucherCurrentColumn");
        voucherPreviousColumn.setId("voucherPreviousColumn");
        voucherColumn.setId("voucherColumn");
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
        saldoColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getSaldoValue()).asObject());
        saldoColumn.setCellFactory(col -> {
            TableCell<Driver, Float> cell = new TableCell<>() {
                @Override protected void updateItem(Float value, boolean empty) {
                    super.updateItem(value, empty);
                    setText((empty || value == null) ? null : nf.format(value));
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
        setupFloatColumn(fixedCostsColumn,       nf);
        setupFloatColumn(voucherCurrentColumn,   nf);
        setupFloatColumn(voucherPreviousColumn, nf);
        setupFloatColumn(voucherColumn,         nf);
        setupFloatColumn(cardColumn,             nf);
        setupFloatColumn(cashColumn,             nf);
        setupFloatColumn(lotColumn,              nf);
        setupFloatColumn(turnoverColumn,         nf);
        setupFloatColumn(zlPerKmColumn,          nf);
        setupFloatColumn(fuelPerTurnoverColumn,  nf);

        // 6) Sortowanie dotyczy wyłącznie kierowców. Wiersz SUMA jest przypięty pod tabelą.
        driversTable.setSortPolicy(table -> {
            var items = table.getItems();
            if (items == null || items.isEmpty()) {
                updatePinnedSummaryFromVisibleRows();
                return true;
            }

            removeSummaryRow(items);

            if (table.getComparator() != null) {
                FXCollections.sort(items, table.getComparator());
            }

            updatePinnedSummaryFromVisibleRows();

            return true;
        });
        driversTable.getSortOrder().addListener((ListChangeListener<TableColumn<Driver, ?>>) change -> updatePinnedSummaryFromVisibleRows());
        driversTable.comparatorProperty().addListener((obs, oldComparator, newComparator) -> updatePinnedSummaryFromVisibleRows());

        // 7) Konfiguracja filtrów dat

        // 8) Przywracamy zaznaczanie całych wierszy
        driversTable.getSelectionModel().setCellSelectionEnabled(false);
        driversTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

// 9) Ctrl+C: kopiujemy wartość focusowanej komórki
        TableUtils.enableCopyOnCtrlC(driversTable);

        // 10) Zapamiętujemy i odtwarzamy kolejność kolumn
        TableUtils.enableColumnsOrderPersistence(driversTable, DriversController.class, PREF_KEY_COLUMNS_ORDER);
        setupPinnedSummaryRow();
        TableUtils.enableExcelFilterAndExport(driversTable, "Drivers");
        installFilteredSummarySupport();

// 11) Row factory to style legacy summary rows if old data is ever present
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

        setupDateFilters();
    }

    private void installFilteredSummarySupport() {
        Platform.runLater(() -> {
            driversTable.itemsProperty().addListener((obs, oldItems, newItems) -> {
                if (summaryListener != null && oldItems != null) {
                    oldItems.removeListener(summaryListener);
                }
                if (summaryListener != null && newItems != null) {
                    newItems.addListener(summaryListener);
                }
                refreshSummaryFromVisibleRows();
            });
            summaryListener = change -> {
                if (!updatingSummaryRow) {
                    refreshSummaryFromVisibleRows();
                } else {
                    keepSummaryRowLastLater();
                }
            };
            if (driversTable.getItems() != null) {
                driversTable.getItems().addListener(summaryListener);
            }
            refreshSummaryFromVisibleRows();
        });
    }

    private void refreshSummaryFromVisibleRows() {
        updatingSummaryRow = true;
        try {
            var items = driversTable.getItems();
            if (items != null) {
                removeSummaryRow(items);
            }
            updatePinnedSummaryFromVisibleRows();
        } finally {
            updatingSummaryRow = false;
        }
    }

    private void setupPinnedSummaryRow() {
        if (summaryRow == null) {
            return;
        }

        setupSummaryRowViewport();

        Map<String, Region> summaryCells = new LinkedHashMap<>();
        for (javafx.scene.Node node : summaryRow.getChildren()) {
            if (node instanceof Region region && node.getId() != null) {
                summaryCells.put(node.getId(), region);
            }
        }

        for (TableColumn<Driver, ?> column : driversTable.getColumns()) {
            Region cell = summaryCells.get(summaryCellIdForColumn(column.getId()));
            if (cell == null) {
                continue;
            }
            cell.minWidthProperty().bind(column.widthProperty());
            cell.prefWidthProperty().bind(column.widthProperty());
            cell.maxWidthProperty().bind(column.widthProperty());
            cell.visibleProperty().bind(column.visibleProperty());
            cell.managedProperty().bind(column.visibleProperty());
        }

        Runnable reorderSummaryCells = () -> {
            if (summaryRow == null) {
                return;
            }
            var orderedCells = FXCollections.<javafx.scene.Node>observableArrayList();
            for (TableColumn<Driver, ?> column : driversTable.getColumns()) {
                Region cell = summaryCells.get(summaryCellIdForColumn(column.getId()));
                if (cell != null) {
                    orderedCells.add(cell);
                }
            }
            summaryRow.getChildren().setAll(orderedCells);
        };
        reorderSummaryCells.run();
        driversTable.getColumns().addListener((ListChangeListener<TableColumn<Driver, ?>>) change -> reorderSummaryCells.run());
        TableUtils.bindHorizontalScroll(driversTable, summaryRow);
    }

    private void setupSummaryRowViewport() {
        if (summaryRowViewport == null) {
            return;
        }

        summaryRow.setManaged(false);
        summaryRow.setLayoutX(0);
        summaryRow.setLayoutY(0);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(summaryRowViewport.widthProperty());
        clip.heightProperty().bind(summaryRowViewport.heightProperty());
        summaryRowViewport.setClip(clip);
    }

    private String summaryCellIdForColumn(String columnId) {
        return switch (columnId) {
            case "idColumn" -> "idPlaceholder";
            case "nameColumn" -> "namePlaceholder";
            case "saldoColumn" -> "saldoSumLabel";
            case "statusColumn" -> "statusPlaceholder";
            case "vehiclePlateColumn" -> "vehiclePlatePlaceholder";
            case "fuelCostColumn" -> "fuelCostPlaceholder";
            case "fuelCostSumColumn" -> "fuelSumLabel";
            case "percentTurnoverColumn" -> "percentTurnoverAvgLabel";
            case "cardCommissionColumn" -> "cardCommissionSumLabel";
            case "partnerCommissionColumn" -> "partnerCommissionSumLabel";
            case "boltCommissionColumn" -> "boltCommissionSumLabel";
            case "settlementLimitColumn" -> "settlementLimitSumLabel";
            case "fixedCostsColumn" -> "fixedCostsSumLabel";
            case "voucherCurrentColumn" -> "voucherCurrentSumLabel";
            case "voucherPreviousColumn" -> "voucherPreviousSumLabel";
            case "voucherColumn" -> "voucherSumLabel";
            case "cardColumn" -> "cardSumLabel";
            case "cashColumn" -> "cashSumLabel";
            case "lotColumn" -> "lotSumLabel";
            case "turnoverColumn" -> "turnoverSumLabel";
            case "zlPerKmColumn" -> "zlPerKmAvgLabel";
            case "fuelPerTurnoverColumn" -> "fuelPerTurnoverAvgLabel";
            case "createdAtColumn" -> "createdAtPlaceholder";
            default -> columnId;
        };
    }

    private void updatePinnedSummaryFromVisibleRows() {
        var items = driversTable.getItems();
        float saldo = 0f, turnover = 0f, voucherCurrent = 0f, voucherPrevious = 0f, voucher = 0f, card = 0f, cash = 0f, lot = 0f, fuel = 0f, zlPerKm = 0f, fuelPerTurnover = 0f;
        float fixedCosts = 0f, percentTurnover = 0f, cardCommission = 0f, partnerCommission = 0f, boltCommission = 0f, settlementLimit = 0f;
        int count = 0;
        if (items != null) {
            for (Driver d : items) {
                if (d == null || d.isSummary()) continue;
                saldo += d.getSaldoValue();
                turnover += d.getTurnover();
                voucherCurrent += d.getVoucherCurrent();
                voucherPrevious += d.getVoucherPrevious();
                voucher += d.getVoucher();
                card += d.getCard();
                cash += d.getCash();
                lot += d.getLot();
                fuel += d.getFuelCostSum();
                fixedCosts += d.getFixedCosts();
                percentTurnover += d.getPercentTurnover();
                cardCommission += d.getCardCommission();
                partnerCommission += d.getPartnerCommission();
                boltCommission += d.getBoltCommission();
                settlementLimit += d.getSettlementLimit();
                zlPerKm += d.getZlPerKm();
                fuelPerTurnover += d.getFuelPerTurnover();
                count++;
            }
        }

        NumberFormat format = createNumberFormat();
        setSummaryLabel(saldoSumLabel, format.format(saldo));
        setSummaryLabel(fuelSumLabel, format.format(fuel));
        setSummaryLabel(fixedCostsSumLabel, format.format(fixedCosts));
        setSummaryLabel(percentTurnoverAvgLabel, format.format(count > 0 ? percentTurnover / count : 0f));
        setSummaryLabel(cardCommissionSumLabel, format.format(cardCommission));
        setSummaryLabel(partnerCommissionSumLabel, format.format(partnerCommission));
        setSummaryLabel(boltCommissionSumLabel, format.format(boltCommission));
        setSummaryLabel(settlementLimitSumLabel, format.format(settlementLimit));
        setSummaryLabel(voucherCurrentSumLabel, format.format(voucherCurrent));
        setSummaryLabel(voucherPreviousSumLabel, format.format(voucherPrevious));
        setSummaryLabel(voucherSumLabel, format.format(voucher));
        setSummaryLabel(cardSumLabel, format.format(card));
        setSummaryLabel(cashSumLabel, format.format(cash));
        setSummaryLabel(lotSumLabel, format.format(lot));
        setSummaryLabel(turnoverSumLabel, format.format(turnover));
        setSummaryLabel(zlPerKmAvgLabel, format.format(count > 0 ? zlPerKm / count : 0f));
        setSummaryLabel(fuelPerTurnoverAvgLabel, format.format(count > 0 ? fuelPerTurnover / count : 0f));
    }

    private void setSummaryLabel(Label label, String text) {
        if (label != null) {
            label.setText(text);
        }
    }

    private Driver removeSummaryRow(ObservableList<Driver> items) {
        for (int i = 0; i < items.size(); i++) {
            Driver driver = items.get(i);
            if (driver != null && driver.isSummary()) {
                return items.remove(i);
            }
        }
        return null;
    }

    private void keepSummaryRowLastLater() {
        if (updatingSummaryRow) {
            return;
        }
        Platform.runLater(this::keepSummaryRowLast);
    }

    private void keepSummaryRowLast() {
        if (updatingSummaryRow) {
            return;
        }
        var items = driversTable.getItems();
        if (items == null || items.size() < 2) {
            return;
        }
        updatingSummaryRow = true;
        try {
            Driver summary = removeSummaryRow(items);
            if (summary != null) {
                items.add(summary);
            }
        } finally {
            updatingSummaryRow = false;
        }
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
            ApiClient.ApiResponse response = ApiClient.sendGet("get_drivers.php");
            if (response.isNetworkError()) {
                showError("Błąd połączenia", "Nie można połączyć się z serwerem. Sprawdź połączenie z internetem lub adres API.");
                return;
            }

            String resp = response.getBody();
            if (resp == null || resp.isBlank()) {
                showError("Błąd serwera", "Serwer zwrócił pustą odpowiedź (HTTP " + response.getCode() + ").");
                return;
            }

            JsonObject json;
            try {
                json = JsonParser.parseString(resp).getAsJsonObject();
            } catch (RuntimeException parseError) {
                RemoteLogService.logHandledException("Nieprawidłowa odpowiedź API podczas odświeżania kierowców", parseError);
                showError("Błąd serwera", buildUnexpectedResponseMessage(response));
                return;
            }

            if (!"success".equals(getString(json, "status"))) {
                showError("Błąd", getString(json, "message", "Nie udało się pobrać listy kierowców (HTTP " + response.getCode() + ")."));
                return;
            }
            JsonArray arr = json.getAsJsonArray("drivers");
            if (arr == null) {
                showError("Błąd serwera", "Odpowiedź API nie zawiera listy kierowców.");
                return;
            }
            driversTable.getItems().clear();


            LocalDate effectiveStart = statsStartDate != null ? statsStartDate : LocalDate.of(1970, 1, 1);
            LocalDate effectiveEnd = statsEndDate != null ? statsEndDate : LocalDate.now();
            if (effectiveEnd.isBefore(effectiveStart)) {
                showError("Błędny zakres dat", "Data końcowa nie może być wcześniejsza niż początkowa.");
                return;
            }
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDate = effectiveStart.format(fmt);
            String endDate = effectiveEnd.plusDays(1).format(fmt);

            for (int i = 0; i < arr.size(); i++) {
                JsonObject o = arr.get(i).getAsJsonObject();
                String id = getString(o, "id");
                String fullName = (getString(o, "imie") + " " + getString(o, "nazwisko")).trim();
                String saldo = getString(o, "saldo", "0");
                String status = getString(o, "status");
                String role = getString(o, "rola");
                String createdAt = getString(o, "created_at");
                String plate = getString(o, "vehiclePlate");

                float percentTurnover = getFloat(o, "percentTurnover");
                float fuelCostFlag = 0f;
                if (o.has("fuelCost") && !o.get("fuelCost").isJsonNull()) {
                    try {
                        fuelCostFlag = o.get("fuelCost").getAsFloat();
                    } catch (RuntimeException ex) {
                        String raw = o.get("fuelCost").getAsString();
                        fuelCostFlag = "kierowca".equalsIgnoreCase(raw.trim()) ? 1f : 0f;
                    }
                } else if (o.has("koszt_paliwa") && !o.get("koszt_paliwa").isJsonNull()) {
                    String raw = o.get("koszt_paliwa").getAsString();
                    fuelCostFlag = "kierowca".equalsIgnoreCase(raw.trim()) ? 1f : 0f;
                }
                float fuelCostSum = o.has("fuelCostSum") && !o.get("fuelCostSum").isJsonNull()
                        ? o.get("fuelCostSum").getAsFloat() : 0f;
                float cardComm = getFloat(o, "cardCommission");
                float partComm = getFloat(o, "partnerCommission");
                float boltComm = getFloat(o, "boltCommission");
                float settLimit = getFloat(o, "settlementLimit");
                float fixedCosts = getFloat(o, "fixedCosts");
                float voucherCurrent = getFloat(o, "voucher_current_amount");
                float voucherPrevious = getFloat(o, "voucher_previous_amount");

                // fetch statistics for current month
                DriverStats stats = ApiClient.getDriverStats(id, startDate, endDate);

                float cardVal = 0f;
                float cashVal = 0f;
                float lotVal = 0f;
                float turnover = 0f;
                float zlPerKm = 0f;
                float fuelPerTurnover = 0f;
                float fuelSum = 0f;
                float voucherTotal = 0f;

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
                    voucherTotal = stats.getVoucher();
                }

                driversTable.getItems().add(new Driver(
                        id, fullName, saldo, status, role,
                        percentTurnover, fuelCostFlag, cardComm, partComm,
                        boltComm, settLimit, fixedCosts, createdAt, plate, fuelSum,
                        voucherCurrent, voucherPrevious, voucherTotal,
                        cardVal, cashVal, lotVal, turnover, zlPerKm, fuelPerTurnover
                ));

            }

            refreshSummaryFromVisibleRows();


        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Błąd połączenia", "Nie można się połączyć z serwerem.");

        }
    }

    private String getString(JsonObject object, String field) {
        return getString(object, field, "");
    }

    private String getString(JsonObject object, String field, String defaultValue) {
        JsonElement element = object.get(field);
        return element == null || element.isJsonNull() ? defaultValue : element.getAsString();
    }

    private String buildUnexpectedResponseMessage(ApiClient.ApiResponse response) {
        String preview = response.getBody() == null ? "" : response.getBody().trim();
        if (preview.length() > 120) {
            preview = preview.substring(0, 120) + "...";
        }
        if (preview.isBlank()) {
            return "Serwer zwrócił nieprawidłową odpowiedź (HTTP " + response.getCode() + ").";
        }
        return "Serwer zwrócił nieprawidłową odpowiedź (HTTP " + response.getCode() + "): " + preview;
    }


    private float getFloat(JsonObject object, String field) {
        JsonElement element = object.get(field);
        if (element == null || element.isJsonNull()) {
            return 0f;
        }
        try {
            return element.getAsFloat();
        } catch (RuntimeException ex) {
            String text = element.getAsString();
            if (text == null || text.isBlank()) {
                return 0f;
            }
            try {
                return Float.parseFloat(text.replace(',', '.'));
            } catch (NumberFormatException ignored) {
                return 0f;
            }
        }
    }

    @FXML
    public void handleAddDriver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-driver-view.fxml"));
            Parent root = loader.load();
            AddDriverController ctrl = loader.getController();
            Stage st = new Stage();
            st.setTitle("Dodaj kierowcę");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            st.setScene(scene);
            st.setMaximized(true);
            st.showAndWait();
            if (ctrl.wasSaved()) {
                loadDrivers();
            }
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
            if (ctrl.wasSaved()) {
                loadDrivers();
            }
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
