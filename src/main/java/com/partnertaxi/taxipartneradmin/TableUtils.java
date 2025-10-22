package com.partnertaxi.taxipartneradmin;

import javafx.scene.control.TableView;
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
}
