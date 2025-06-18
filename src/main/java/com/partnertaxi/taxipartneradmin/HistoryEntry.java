package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.*;

public class HistoryEntry {
    private final StringProperty dateTime;
    private final StringProperty type;
    private final StringProperty description;
    private final StringProperty changeValue;
    private final StringProperty saldoAfter;

    public HistoryEntry(String dateTime, String type, String description, String changeValue, String saldoAfter) {
        this.dateTime = new SimpleStringProperty(dateTime);
        this.type = new SimpleStringProperty(type);
        this.description = new SimpleStringProperty(description);
        this.changeValue = new SimpleStringProperty(changeValue);
        this.saldoAfter = new SimpleStringProperty(saldoAfter);
    }

    public StringProperty dateTimeProperty() {
        return dateTime;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty changeValueProperty() {
        return changeValue;
    }

    public StringProperty saldoAfterProperty() {
        return saldoAfter;
    }
}
