package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.*;

import java.util.List;

/**
 * Model rekordu szkody pojazdu.
 */

public class DamageRecord {
    private final IntegerProperty id;
    private final StringProperty rejestracja;
    private final StringProperty nrSzkody;
    private final StringProperty opis;
    private final StringProperty status;
    private final StringProperty data;
    private final List<String> zdjecia;

    public DamageRecord(int id,
                        String rejestracja,
                        String nrSzkody,
                        String opis,
                        String status,
                        String data,
                        List<String> zdjecia) {
        this.id = new SimpleIntegerProperty(id);
        this.rejestracja = new SimpleStringProperty(rejestracja);
        this.nrSzkody = new SimpleStringProperty(nrSzkody);
        this.opis = new SimpleStringProperty(opis);
        this.status = new SimpleStringProperty(status);
        this.data = new SimpleStringProperty(data);
        this.zdjecia = zdjecia;
    }

    public int getId() { return id.get(); }

    public IntegerProperty idProperty() { return id; }

    public String getRejestracja() { return rejestracja.get(); }

    public StringProperty rejestracjaProperty() { return rejestracja; }

    public String getNrSzkody() { return nrSzkody.get(); }

    public StringProperty nrSzkodyProperty() { return nrSzkody; }

    public String getOpis() { return opis.get(); }

    public StringProperty opisProperty() { return opis; }

    public String getStatus() { return status.get(); }

    public StringProperty statusProperty() { return status; }

    public String getData() { return data.get(); }

    public StringProperty dataProperty() { return data; }

    public List<String> getZdjecia() { return zdjecia; }
}
