package com.partnertaxi.taxipartneradmin;

import javafx.scene.control.TableView;
import javafx.scene.control.TablePosition;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

public class TableUtils {

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
}
