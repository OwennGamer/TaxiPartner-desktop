package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.*;

import java.util.List;

public class HistoryEntry {
    private final StringProperty dateTime;
    private final StringProperty type;
    private final StringProperty source;
    private final StringProperty paymentMethod;
    private final StringProperty rideAmount;
    private final StringProperty changeValue;
    private final StringProperty saldoAfter;
    private final StringProperty receiptPhotoUrl;
    private final ObjectProperty<List<String>> receiptPhotoUrls;
    private final BooleanProperty photoAvailable;

    public HistoryEntry(String dateTime, String type, String source, String paymentMethod, String rideAmount, String changeValue,
                        String saldoAfter, String receiptPhotoUrl, List<String> receiptPhotoUrls, boolean photoAvailable) {
        this.dateTime = new SimpleStringProperty(dateTime);
        this.type = new SimpleStringProperty(type);
        this.source = new SimpleStringProperty(source);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
        this.rideAmount = new SimpleStringProperty(rideAmount);
        this.changeValue = new SimpleStringProperty(changeValue);
        this.saldoAfter = new SimpleStringProperty(saldoAfter);
        this.receiptPhotoUrl = new SimpleStringProperty(receiptPhotoUrl);
        this.receiptPhotoUrls = new SimpleObjectProperty<>(receiptPhotoUrls);
        this.photoAvailable = new SimpleBooleanProperty(photoAvailable);
    }

    public StringProperty dateTimeProperty() {
        return dateTime;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty sourceProperty() {
        return source;
    }

    public StringProperty paymentMethodProperty() {
        return paymentMethod;
    }

    public StringProperty rideAmountProperty() {
        return rideAmount;

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

    public List<String> getReceiptPhotoUrls() {
        return receiptPhotoUrls.get();
    }

    public ObjectProperty<List<String>> receiptPhotoUrlsProperty() {
        return receiptPhotoUrls;
    }

    public boolean isPhotoAvailable() {
        return photoAvailable.get();
    }

    public BooleanProperty photoAvailableProperty() {
        return photoAvailable;
    }
}
