package com.partnertaxi.taxipartneradmin;

import javafx.scene.Node;

/** Helper describing a node displayed in the summary row. */
public class SummaryCell {
    private final String columnId;
    private final Node node;

    public SummaryCell(String columnId, Node node) {
        this.columnId = columnId;
        this.node = node;
    }

    public String getColumnId() {
        return columnId;
    }

    public Node getNode() {
        return node;
    }
}
