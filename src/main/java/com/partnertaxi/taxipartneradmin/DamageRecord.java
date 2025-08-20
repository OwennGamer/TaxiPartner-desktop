package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.*;

public class DamageRecord {
    private final IntegerProperty id;
    private final StringProperty date;
    private final StringProperty description;
    private final DoubleProperty cost;

    public DamageRecord(int id, String date, String description, double cost) {
        this.id = new SimpleIntegerProperty(id);
        this.date = new SimpleStringProperty(date);
        this.description = new SimpleStringProperty(description);
        this.cost = new SimpleDoubleProperty(cost);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public double getCost() {
        return cost.get();
    }

    public DoubleProperty costProperty() {
        return cost;
    }
}
