package com.partnertaxi.taxipartneradmin;

import javafx.scene.control.TableView;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.table.TableFilter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TablePosition;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.geometry.Orientation;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.prefs.Preferences;

public class TableUtils {

    private static final Comparator<String> NATURAL_ID_COMPARATOR = (left, right) -> {
        if (Objects.equals(left, right)) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }

        int indexLeft = 0;
        int indexRight = 0;
        int lengthLeft = left.length();
        int lengthRight = right.length();

        while (indexLeft < lengthLeft && indexRight < lengthRight) {
            char charLeft = left.charAt(indexLeft);
            char charRight = right.charAt(indexRight);

            boolean digitLeft = Character.isDigit(charLeft);
            boolean digitRight = Character.isDigit(charRight);

            if (digitLeft && digitRight) {
                int startLeft = indexLeft;
                int startRight = indexRight;

                while (indexLeft < lengthLeft && Character.isDigit(left.charAt(indexLeft))) {
                    indexLeft++;
                }
                while (indexRight < lengthRight && Character.isDigit(right.charAt(indexRight))) {
                    indexRight++;
                }

                String numberLeft = left.substring(startLeft, indexLeft);
                String numberRight = right.substring(startRight, indexRight);

                BigInteger valueLeft = new BigInteger(numberLeft);
                BigInteger valueRight = new BigInteger(numberRight);
                int compare = valueLeft.compareTo(valueRight);
                if (compare != 0) {
                    return compare;
                }

                compare = Integer.compare(numberLeft.length(), numberRight.length());
                if (compare != 0) {
                    return compare;
                }

            } else if (digitLeft != digitRight) {
                return digitLeft ? 1 : -1;
            } else {
                int compare = Character.compare(Character.toUpperCase(charLeft), Character.toUpperCase(charRight));
                if (compare != 0) {
                    return compare;
                }
                indexLeft++;
                indexRight++;
            }
        }

        if (indexLeft < lengthLeft) {
            return 1;
        }
        if (indexRight < lengthRight) {
            return -1;
        }

        return 0;
    };

    public static Comparator<String> naturalIdComparator() {
        return NATURAL_ID_COMPARATOR;
    }

    public static <T> void enableCopyOnCtrlC(TableView<T> table) {
        table.setOnKeyPressed(evt -> {
            if (evt.isControlDown() && evt.getCode() == KeyCode.C) {
                TablePosition<T, ?> pos = table.getFocusModel().getFocusedCell();
                if (pos != null && pos.getRow() >= 0) {
                    Object cellValue = table.getColumns().get(pos.getColumn()).getCellData(pos.getRow());
                    String str;
                    if (cellValue instanceof Number) {
                        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
                        nf.setGroupingUsed(false);
                        nf.setMinimumFractionDigits(2);
                        nf.setMaximumFractionDigits(2);
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
    }

    public static void saveColumnsOrder(TableView<?> table, Preferences prefs, String key) {
        StringBuilder sb = new StringBuilder();
        for (TableColumn<?, ?> col : table.getColumns()) {
            String id = col.getId();
            if (id != null) {
                if (sb.length() > 0) sb.append(',');
                sb.append(id);
            }
        }
        prefs.put(key, sb.toString());
    }

    public static void restoreColumnsOrder(TableView<?> table, Preferences prefs, String key) {
        String order = prefs.get(key, null);
        if (order == null) return;
        String[] ids = order.split(",");
        ObservableList<TableColumn<?, ?>> current = (ObservableList<TableColumn<?, ?>>)(ObservableList<?>) table.getColumns();
        List<TableColumn<?, ?>> newOrder = new ArrayList<>();
        for (String id : ids) {
            current.stream()
                    .filter(c -> id.equals(c.getId()))
                    .findFirst()
                    .ifPresent(newOrder::add);
        }
        for (TableColumn<?, ?> col : current) {
            if (!newOrder.contains(col)) newOrder.add(col);
        }
        current.setAll(newOrder);
    }

    public static void enableColumnsOrderPersistence(TableView<?> table, Class<?> prefClass, String key) {
        Preferences prefs = Preferences.userNodeForPackage(prefClass);
        restoreColumnsOrder(table, prefs, key);
        table.getColumns().addListener((ListChangeListener<TableColumn<?, ?>>) change -> {
            while (change.next()) {
                if (change.wasPermutated() || change.wasReplaced()) {
                    saveColumnsOrder(table, prefs, key);
                }
            }
        });
    }

    /**
     * Keeps the given node aligned with the horizontal scroll of the table.
     * The node's translateX is set to the negative scrollbar value so it moves
     * in sync with the table contents.
     */
    public static void bindHorizontalScroll(TableView<?> table, Node node) {
        Runnable attach = () -> {
            ScrollBar hBar = null;
            for (Node n : table.lookupAll(".scroll-bar")) {
                if (n instanceof ScrollBar sb && sb.getOrientation() == Orientation.HORIZONTAL) {
                    hBar = sb;
                    break;
                }
            }
            if (hBar != null) {
                // Bind the node's translateX to the negative scroll value.
                // Only the node property is bound so the scrollbar remains
                // fully interactive for the user.
                node.translateXProperty().unbind();
                node.translateXProperty().bind(hBar.valueProperty().multiply(-1));
            }

        };

        if (table.getSkin() != null) {
            attach.run();
        }

        table.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                attach.run();
            }
        });
    }
    public static <T> void enableExcelFilterAndExport(TableView<T> table, String exportPrefix) {
        Platform.runLater(() -> {
            TableFilter.forTableView(table).lazy(true).apply();
            ensureExportButton(table, exportPrefix);
        });
    }

    private static void ensureExportButton(TableView<?> table, String exportPrefix) {
        if (table.getScene() == null || table.getProperties().containsKey("excelExportInstalled")) {
            return;
        }
        table.getProperties().put("excelExportInstalled", Boolean.TRUE);

        Button exportBtn = new Button("Eksportuj widok do Excel");
        exportBtn.setTooltip(new Tooltip("Eksportuje aktualnie widoczne wiersze i kolumny (z filtrami)."));
        exportBtn.setOnAction(event -> exportTableToExcel(table, exportPrefix));

        if (table.getParent() instanceof BorderPane bp) {
            bp.setBottom(new HBox(exportBtn));
            return;
        }
        if (table.getParent() instanceof javafx.scene.layout.VBox vb) {
            HBox wrapper = new HBox(exportBtn);
            wrapper.setStyle("-fx-padding: 8 0 0 0;");
            vb.getChildren().add(wrapper);
            return;
        }

        table.setPlaceholder(new javafx.scene.control.Label("Brak danych. Eksport dostępny po załadowaniu tabeli."));
    }

    private static void exportTableToExcel(TableView<?> table, String exportPrefix) {
        Window window = table.getScene() != null ? table.getScene().getWindow() : null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz eksport Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        fileChooser.setInitialFileName(exportPrefix + "_" + timestamp + ".xlsx");
        var saveFile = fileChooser.showSaveDialog(window);
        if (saveFile == null) {
            return;
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(saveFile)) {
            XSSFSheet sheet = workbook.createSheet("Dane");
            Row header = sheet.createRow(0);
            for (int c = 0; c < table.getColumns().size(); c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(table.getColumns().get(c).getText());
            }

            int rowNo = 1;
            for (Object item : table.getItems()) {
                Row row = sheet.createRow(rowNo++);
                for (int c = 0; c < table.getColumns().size(); c++) {
                    @SuppressWarnings("unchecked")
                    TableColumn<Object, ?> column = (TableColumn<Object, ?>) table.getColumns().get(c);
                    ObservableValue<?> observableValue = column.getCellObservableValue(item);
                    Object value = observableValue != null ? observableValue.getValue() : null;
                    row.createCell(c).setCellValue(value == null ? "" : value.toString());
                }
            }
            for (int c = 0; c < table.getColumns().size(); c++) {
                sheet.autoSizeColumn(c);
            }
            workbook.write(out);
        } catch (IOException ex) {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Nie udało się zapisać pliku Excel: " + ex.getMessage()).showAndWait();
        }
    }
}
