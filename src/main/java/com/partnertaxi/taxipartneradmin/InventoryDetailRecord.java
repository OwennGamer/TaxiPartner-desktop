package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.*;

public class InventoryDetailRecord {
    private final IntegerProperty id;
    private final StringProperty rejestracja;
    private final IntegerProperty przebieg;
    private final BooleanProperty czysteWewnatrz;
    private final StringProperty dataDodania;
    private final StringProperty photoFront;
    private final StringProperty photoBack;
    private final StringProperty photoLeft;
    private final StringProperty photoRight;
    private final IntegerProperty kamizelkiQty;
    private final StringProperty photoDirt1;
    private final StringProperty photoDirt2;
    private final StringProperty photoDirt3;
    private final StringProperty photoDirt4;
    private final BooleanProperty licencja;
    private final BooleanProperty legalizacja;
    private final BooleanProperty dowod;
    private final BooleanProperty ubezpieczenie;
    private final BooleanProperty kartaLotniskowa;
    private final BooleanProperty gasnica;
    private final BooleanProperty lewarek;
    private final BooleanProperty trojkat;
    private final BooleanProperty kamizelka;
    private final StringProperty uwagi;

    public InventoryDetailRecord(int id, String rejestracja, int przebieg,
                                 boolean czysteWewnatrz, String dataDodania,
                                 String photoFront, String photoBack,
                                 String photoLeft, String photoRight,
                                 Integer kamizelkiQty,
                                 String photoDirt1, String photoDirt2,
                                 String photoDirt3, String photoDirt4,
                                 boolean licencja, boolean legalizacja,
                                 boolean dowod, boolean ubezpieczenie,
                                 boolean kartaLotniskowa, boolean gasnica,
                                 boolean lewarek, boolean trojkat,
                                 boolean kamizelka, String uwagi) {
        this.id = new SimpleIntegerProperty(id);
        this.rejestracja = new SimpleStringProperty(rejestracja);
        this.przebieg = new SimpleIntegerProperty(przebieg);
        this.czysteWewnatrz = new SimpleBooleanProperty(czysteWewnatrz);
        this.dataDodania = new SimpleStringProperty(dataDodania);
        this.photoFront = new SimpleStringProperty(photoFront);
        this.photoBack = new SimpleStringProperty(photoBack);
        this.photoLeft = new SimpleStringProperty(photoLeft);
        this.photoRight = new SimpleStringProperty(photoRight);
        this.kamizelkiQty = new SimpleIntegerProperty(kamizelkiQty == null ? 0 : kamizelkiQty);
        this.photoDirt1 = new SimpleStringProperty(photoDirt1);
        this.photoDirt2 = new SimpleStringProperty(photoDirt2);
        this.photoDirt3 = new SimpleStringProperty(photoDirt3);
        this.photoDirt4 = new SimpleStringProperty(photoDirt4);
        this.licencja = new SimpleBooleanProperty(licencja);
        this.legalizacja = new SimpleBooleanProperty(legalizacja);
        this.dowod = new SimpleBooleanProperty(dowod);
        this.ubezpieczenie = new SimpleBooleanProperty(ubezpieczenie);
        this.kartaLotniskowa = new SimpleBooleanProperty(kartaLotniskowa);
        this.gasnica = new SimpleBooleanProperty(gasnica);
        this.lewarek = new SimpleBooleanProperty(lewarek);
        this.trojkat = new SimpleBooleanProperty(trojkat);
        this.kamizelka = new SimpleBooleanProperty(kamizelka);
        this.uwagi = new SimpleStringProperty(uwagi);
    }

    // gettery i property dla każdego pola:
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getRejestracja() { return rejestracja.get(); }
    public StringProperty rejestracjaProperty() { return rejestracja; }

    public int getPrzebieg() { return przebieg.get(); }
    public IntegerProperty przebiegProperty() { return przebieg; }

    public boolean isCzysteWewnatrz() { return czysteWewnatrz.get(); }
    public BooleanProperty czysteWewnatrzProperty() { return czysteWewnatrz; }

    public String getDataDodania() { return dataDodania.get(); }
    public StringProperty dataDodaniaProperty() { return dataDodania; }

    public String getPhotoFront() { return photoFront.get(); }
    public StringProperty photoFrontProperty() { return photoFront; }

    public String getPhotoBack() { return photoBack.get(); }
    public StringProperty photoBackProperty() { return photoBack; }

    public String getPhotoLeft() { return photoLeft.get(); }
    public StringProperty photoLeftProperty() { return photoLeft; }

    public String getPhotoRight() { return photoRight.get(); }
    public StringProperty photoRightProperty() { return photoRight; }

    public int getKamizelkiQty() { return kamizelkiQty.get(); }
    public IntegerProperty kamizelkiQtyProperty() { return kamizelkiQty; }

    public String getPhotoDirt1() { return photoDirt1.get(); }
    public StringProperty photoDirt1Property() { return photoDirt1; }

    public String getPhotoDirt2() { return photoDirt2.get(); }
    public StringProperty photoDirt2Property() { return photoDirt2; }

    public String getPhotoDirt3() { return photoDirt3.get(); }
    public StringProperty photoDirt3Property() { return photoDirt3; }

    public String getPhotoDirt4() { return photoDirt4.get(); }
    public StringProperty photoDirt4Property() { return photoDirt4; }

    public boolean isLicencja() { return licencja.get(); }
    public BooleanProperty licencjaProperty() { return licencja; }

    public boolean isLegalizacja() { return legalizacja.get(); }
    public BooleanProperty legalizacjaProperty() { return legalizacja; }

    public boolean isDowod() { return dowod.get(); }
    public BooleanProperty dowodProperty() { return dowod; }

    public boolean isUbezpieczenie() { return ubezpieczenie.get(); }
    public BooleanProperty ubezpieczenieProperty() { return ubezpieczenie; }

    public boolean isKartaLotniskowa() { return kartaLotniskowa.get(); }
    public BooleanProperty kartaLotniskowaProperty() { return kartaLotniskowa; }

    public boolean isGasnica() { return gasnica.get(); }
    public BooleanProperty gasnicaProperty() { return gasnica; }

    public boolean isLewarek() { return lewarek.get(); }
    public BooleanProperty lewarekProperty() { return lewarek; }

    public boolean isTrojkat() { return trojkat.get(); }
    public BooleanProperty trojkatProperty() { return trojkat; }

    public boolean isKamizelka() { return kamizelka.get(); }
    public BooleanProperty kamizelkaProperty() { return kamizelka; }

    public String getUwagi() { return uwagi.get(); }
    public StringProperty uwagiProperty() { return uwagi; }
}
