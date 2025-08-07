package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.*;

public class HistoryEntry {
    private final StringProperty dateTime;
    private final StringProperty type;
    private final StringProperty description;
    private final StringProperty changeValue;
    private final StringProperty saldoAfter;
    private final StringProperty receiptPhotoUrl;
    private final BooleanProperty photoAvailable;

    public HistoryEntry(String dateTime, String type, String description, String changeValue,
                        String saldoAfter, String receiptPhotoUrl, boolean photoAvailable) {
        this.dateTime = new SimpleStringProperty(dateTime);
        this.type = new SimpleStringProperty(type);
        this.description = new SimpleStringProperty(description);
        this.changeValue = new SimpleStringProperty(changeValue);
        this.saldoAfter = new SimpleStringProperty(saldoAfter);
        this.receiptPhotoUrl = new SimpleStringProperty(receiptPhotoUrl);
        this.photoAvailable = new SimpleBooleanProperty(photoAvailable);
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


    public String getReceiptPhotoUrl() {
        return receiptPhotoUrl.get();
    }

    public StringProperty receiptPhotoUrlProperty() {
        return receiptPhotoUrl;
    }

    public boolean isPhotoAvailable() {
        return photoAvailable.get();
    }

    public BooleanProperty photoAvailableProperty() {
        return photoAvailable;
    }
}
