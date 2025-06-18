package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class InventoryHistoryRecord {
    private final IntegerProperty id;
    private final StringProperty dateTime;
    private final IntegerProperty przebieg;
    private final StringProperty driverId;         // Zmieniono na StringProperty

    public InventoryHistoryRecord(int id, String dateTime, int przebieg, String driverId) {
        this.id = new SimpleIntegerProperty(id);
        this.dateTime = new SimpleStringProperty(dateTime);
        this.przebieg = new SimpleIntegerProperty(przebieg);
        this.driverId = new SimpleStringProperty(driverId);  // Przyjmujemy String
    }

    public int getId() {
        return id.get();
    }
    public IntegerProperty idProperty() {
        return id;
    }

    public String getDateTime() {
        return dateTime.get();
    }
    public StringProperty dateTimeProperty() {
        return dateTime;
    }

    public int getPrzebieg() {
        return przebieg.get();
    }
    public IntegerProperty przebiegProperty() {
        return przebieg;
    }

    public String getDriverId() {
        return driverId.get();              // Getter zwraca String
    }
    public StringProperty driverIdProperty() {
        return driverId;
    }
}
