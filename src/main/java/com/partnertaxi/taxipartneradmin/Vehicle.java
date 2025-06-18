package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.*;

public class Vehicle {
    private final IntegerProperty id;
    private final StringProperty rejestracja;
    private final StringProperty marka;
    private final StringProperty model;
    private final IntegerProperty przebieg;
    private final StringProperty ubezpieczenieDo;
    private final StringProperty przegladDo;
    private final BooleanProperty aktywny;

    public Vehicle(int id, String rejestracja, String marka, String model,
                   int przebieg, String ubezpieczenieDo, String przegladDo, boolean aktywny) {
        this.id = new SimpleIntegerProperty(id);
        this.rejestracja = new SimpleStringProperty(rejestracja);
        this.marka = new SimpleStringProperty(marka);
        this.model = new SimpleStringProperty(model);
        this.przebieg = new SimpleIntegerProperty(przebieg);
        this.ubezpieczenieDo = new SimpleStringProperty(ubezpieczenieDo);
        this.przegladDo = new SimpleStringProperty(przegladDo);
        this.aktywny = new SimpleBooleanProperty(aktywny);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getRejestracja() {
        return rejestracja.get();
    }

    public StringProperty rejestracjaProperty() {
        return rejestracja;
    }

    public String getMarka() {
        return marka.get();
    }

    public StringProperty markaProperty() {
        return marka;
    }

    public String getModel() {
        return model.get();
    }

    public StringProperty modelProperty() {
        return model;
    }

    public int getPrzebieg() {
        return przebieg.get();
    }

    public IntegerProperty przebiegProperty() {
        return przebieg;
    }

    public String getUbezpieczenieDo() {
        return ubezpieczenieDo.get();
    }

    public StringProperty ubezpieczenieDoProperty() {
        return ubezpieczenieDo;
    }

    public String getPrzegladDo() {
        return przegladDo.get();
    }

    public StringProperty przegladDoProperty() {
        return przegladDo;
    }

    public boolean isAktywny() {
        return aktywny.get();
    }

    public BooleanProperty aktywnyProperty() {
        return aktywny;
    }
}
