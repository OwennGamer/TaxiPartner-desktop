package com.partnertaxi.taxipartneradmin;

import javafx.scene.control.TableView;
import javafx.scene.control.TablePosition;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import java.text.NumberFormat;
import java.util.Locale;

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
}

