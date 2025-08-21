package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.*;

import java.util.List;

/**
 * Model rekordu serwisowego pojazdu.
 */

public class ServiceRecord {
    private final IntegerProperty id;
    private final StringProperty rejestracja;
    private final StringProperty opis;
    private final DoubleProperty koszt;
    private final StringProperty status;
    private final StringProperty data;
    private final List<String> zdjecia;

    public ServiceRecord(int id,
                         String rejestracja,
                         String opis,
                         double koszt,
                         String status,
                         String data,
                         List<String> zdjecia) {
        this.id = new SimpleIntegerProperty(id);
        this.rejestracja = new SimpleStringProperty(rejestracja);
        this.opis = new SimpleStringProperty(opis);
        this.koszt = new SimpleDoubleProperty(koszt);
        this.status = new SimpleStringProperty(status);
        this.data = new SimpleStringProperty(data);
        this.zdjecia = zdjecia;
    }

    public int getId() { return id.get(); }

    public IntegerProperty idProperty() { return id; }

    public String getRejestracja() { return rejestracja.get(); }

    public StringProperty rejestracjaProperty() { return rejestracja; }

    public String getOpis() { return opis.get(); }

    public StringProperty opisProperty() { return opis; }

    public String getData() { return data.get(); }

    public StringProperty dataProperty() { return data; }

    public double getKoszt() { return koszt.get(); }

    public DoubleProperty kosztProperty() { return koszt; }

    public String getStatus() { return status.get(); }

    public StringProperty statusProperty() { return status; }

    public List<String> getZdjecia() { return zdjecia; }
}
