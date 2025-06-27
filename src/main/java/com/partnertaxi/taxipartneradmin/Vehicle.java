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
    private final BooleanProperty inpost;
    private final BooleanProperty taxi;
    private final BooleanProperty taksometr;
    private final StringProperty legalizacjaTaksometruDo;
    private final BooleanProperty gaz;
    private final StringProperty homologacjaLpgDo;
    private final StringProperty firma;
    private final StringProperty formaWlasnosci;
    private final StringProperty numerPolisy;

    public Vehicle(int id, String rejestracja, String marka, String model,
                   int przebieg, String ubezpieczenieDo, String przegladDo, boolean aktywny,
                   boolean inpost, boolean taxi, boolean taksometr, String legalizacjaTaksometruDo,
                   boolean gaz, String homologacjaLpgDo, String firma,
                   String formaWlasnosci, String numerPolisy) {
        this.id = new SimpleIntegerProperty(id);
        this.rejestracja = new SimpleStringProperty(rejestracja);
        this.marka = new SimpleStringProperty(marka);
        this.model = new SimpleStringProperty(model);
        this.przebieg = new SimpleIntegerProperty(przebieg);
        this.ubezpieczenieDo = new SimpleStringProperty(ubezpieczenieDo);
        this.przegladDo = new SimpleStringProperty(przegladDo);
        this.aktywny = new SimpleBooleanProperty(aktywny);
        this.inpost = new SimpleBooleanProperty(inpost);
        this.taxi = new SimpleBooleanProperty(taxi);
        this.taksometr = new SimpleBooleanProperty(taksometr);
        this.legalizacjaTaksometruDo = new SimpleStringProperty(legalizacjaTaksometruDo);
        this.gaz = new SimpleBooleanProperty(gaz);
        this.homologacjaLpgDo = new SimpleStringProperty(homologacjaLpgDo);
        this.firma = new SimpleStringProperty(firma);
        this.formaWlasnosci = new SimpleStringProperty(formaWlasnosci);
        this.numerPolisy = new SimpleStringProperty(numerPolisy);
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

    public boolean isInpost() {
        return inpost.get();
    }

    public BooleanProperty inpostProperty() {
        return inpost;
    }

    public boolean isTaxi() {
        return taxi.get();
    }

    public BooleanProperty taxiProperty() {
        return taxi;
    }

    public boolean isTaksometr() {
        return taksometr.get();
    }

    public BooleanProperty taksometrProperty() {
        return taksometr;
    }

    public String getLegalizacjaTaksometruDo() {
        return legalizacjaTaksometruDo.get();
    }

    public StringProperty legalizacjaTaksometruDoProperty() {
        return legalizacjaTaksometruDo;
    }

    public boolean isGaz() {
        return gaz.get();
    }

    public BooleanProperty gazProperty() {
        return gaz;
    }

    public String getHomologacjaLpgDo() {
        return homologacjaLpgDo.get();
    }

    public StringProperty homologacjaLpgDoProperty() {
        return homologacjaLpgDo;
    }

    public String getFirma() {
        return firma.get();
    }

    public StringProperty firmaProperty() {
        return firma;
    }

    public String getFormaWlasnosci() {
        return formaWlasnosci.get();
    }

    public StringProperty formaWlasnosciProperty() {
        return formaWlasnosci;
    }

    public String getNumerPolisy() {
        return numerPolisy.get();
    }

    public StringProperty numerPolisyProperty() {
        return numerPolisy;
    }
}
