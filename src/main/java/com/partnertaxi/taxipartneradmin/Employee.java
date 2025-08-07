package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.*;

public class Employee {
    private final StringProperty id;
    private final StringProperty name;

    private final StringProperty firma;
    private final StringProperty rodzajUmowy;
    private final StringProperty dataUmowy;

    private final BooleanProperty dowod;
    private final BooleanProperty prawoJazdy;
    private final BooleanProperty niekaralnosc;
    private final BooleanProperty orzeczeniePsychologiczne;
    private final StringProperty dataBadaniaPsychologicznego;
    private final BooleanProperty orzeczenieLekarskie;
    private final StringProperty dataBadanLekarskich;
    private final BooleanProperty informacjaPpk;
    private final BooleanProperty rezygnacjaPpk;
    private final StringProperty formaWyplaty;
    private final BooleanProperty wynagrodzenieDoRakWlasnych;
    private final BooleanProperty zgodaNaPrzelew;
    private final BooleanProperty ryzykoZawodowe;
    private final BooleanProperty oswiadczenieZUS;
    private final BooleanProperty bhp;
    private final BooleanProperty regulaminPracy;
    private final BooleanProperty zasadyEwidencjiKasa;
    private final BooleanProperty pit2;
    private final BooleanProperty oswiadczenieArt188KP;
    private final BooleanProperty rodo;
    private final BooleanProperty poraNocna;
    private final StringProperty pitEmail;
    private final StringProperty osobaKontaktowa;
    private final StringProperty numerPrywatny;
    private final StringProperty numerSluzbowy;
    private final StringProperty modelTelefonuSluzbowego;
    private final StringProperty operator;
    private final StringProperty waznoscWizy;

    public Employee(String id,
                    String name,
                    String firma,
                    String rodzajUmowy,
                    String dataUmowy,
                    boolean dowod,
                    boolean prawoJazdy,
                    boolean niekaralnosc,
                    boolean orzeczeniePsychologiczne,
                    String dataBadaniaPsychologicznego,
                    boolean orzeczenieLekarskie,
                    String dataBadanLekarskich,
                    boolean informacjaPpk,
                    boolean rezygnacjaPpk,
                    String formaWyplaty,
                    boolean wynagrodzenieDoRakWlasnych,
                    boolean zgodaNaPrzelew,
                    boolean ryzykoZawodowe,
                    boolean oswiadczenieZUS,
                    boolean bhp,
                    boolean regulaminPracy,
                    boolean zasadyEwidencjiKasa,
                    boolean pit2,
                    boolean oswiadczenieArt188KP,
                    boolean rodo,
                    boolean poraNocna,
                    String pitEmail,
                    String osobaKontaktowa,
                    String numerPrywatny,
                    String numerSluzbowy,
                    String modelTelefonuSluzbowego,
                    String operator,
                    String waznoscWizy) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);

        this.firma = new SimpleStringProperty(firma);
        this.rodzajUmowy = new SimpleStringProperty(rodzajUmowy);
        this.dataUmowy = new SimpleStringProperty(dataUmowy);

        this.dowod = new SimpleBooleanProperty(dowod);
        this.prawoJazdy = new SimpleBooleanProperty(prawoJazdy);
        this.niekaralnosc = new SimpleBooleanProperty(niekaralnosc);
        this.orzeczeniePsychologiczne = new SimpleBooleanProperty(orzeczeniePsychologiczne);
        this.dataBadaniaPsychologicznego = new SimpleStringProperty(dataBadaniaPsychologicznego);
        this.orzeczenieLekarskie = new SimpleBooleanProperty(orzeczenieLekarskie);
        this.dataBadanLekarskich = new SimpleStringProperty(dataBadanLekarskich);
        this.informacjaPpk = new SimpleBooleanProperty(informacjaPpk);
        this.rezygnacjaPpk = new SimpleBooleanProperty(rezygnacjaPpk);
        this.formaWyplaty = new SimpleStringProperty(formaWyplaty);
        this.wynagrodzenieDoRakWlasnych = new SimpleBooleanProperty(wynagrodzenieDoRakWlasnych);
        this.zgodaNaPrzelew = new SimpleBooleanProperty(zgodaNaPrzelew);
        this.ryzykoZawodowe = new SimpleBooleanProperty(ryzykoZawodowe);
        this.oswiadczenieZUS = new SimpleBooleanProperty(oswiadczenieZUS);
        this.bhp = new SimpleBooleanProperty(bhp);
        this.regulaminPracy = new SimpleBooleanProperty(regulaminPracy);
        this.zasadyEwidencjiKasa = new SimpleBooleanProperty(zasadyEwidencjiKasa);
        this.pit2 = new SimpleBooleanProperty(pit2);
        this.oswiadczenieArt188KP = new SimpleBooleanProperty(oswiadczenieArt188KP);
        this.rodo = new SimpleBooleanProperty(rodo);
        this.poraNocna = new SimpleBooleanProperty(poraNocna);
        this.pitEmail = new SimpleStringProperty(pitEmail);
        this.osobaKontaktowa = new SimpleStringProperty(osobaKontaktowa);
        this.numerPrywatny = new SimpleStringProperty(numerPrywatny);
        this.numerSluzbowy = new SimpleStringProperty(numerSluzbowy);
        this.modelTelefonuSluzbowego = new SimpleStringProperty(modelTelefonuSluzbowego);
        this.operator = new SimpleStringProperty(operator);
        this.waznoscWizy = new SimpleStringProperty(waznoscWizy);
    }

    // basic constructor for minimal use
    public Employee(String id, String name) {
        this(id, name, "", "", "", false, false, false, false, "",
                false, "", false, false, "", false, false, false,
                false, false, false, false, false, false, false,
                false, "", "", "", "", "", "", "");
    }

    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getFirma() { return firma.get(); }
    public StringProperty firmaProperty() { return firma; }

    public String getRodzajUmowy() { return rodzajUmowy.get(); }
    public StringProperty rodzajUmowyProperty() { return rodzajUmowy; }

    public String getDataUmowy() { return dataUmowy.get(); }
    public StringProperty dataUmowyProperty() { return dataUmowy; }

    public boolean isDowod() { return dowod.get(); }
    public BooleanProperty dowodProperty() { return dowod; }

    public boolean isPrawoJazdy() { return prawoJazdy.get(); }
    public BooleanProperty prawoJazdyProperty() { return prawoJazdy; }

    public boolean isNiekaralnosc() { return niekaralnosc.get(); }
    public BooleanProperty niekaralnoscProperty() { return niekaralnosc; }

    public boolean isOrzeczeniePsychologiczne() { return orzeczeniePsychologiczne.get(); }
    public BooleanProperty orzeczeniePsychologiczneProperty() { return orzeczeniePsychologiczne; }

    public String getDataBadaniaPsychologicznego() { return dataBadaniaPsychologicznego.get(); }
    public StringProperty dataBadaniaPsychologicznegoProperty() { return dataBadaniaPsychologicznego; }

    public boolean isOrzeczenieLekarskie() { return orzeczenieLekarskie.get(); }
    public BooleanProperty orzeczenieLekarskieProperty() { return orzeczenieLekarskie; }

    public String getDataBadanLekarskich() { return dataBadanLekarskich.get(); }
    public StringProperty dataBadanLekarskichProperty() { return dataBadanLekarskich; }

    public boolean isInformacjaPpk() { return informacjaPpk.get(); }
    public BooleanProperty informacjaPpkProperty() { return informacjaPpk; }

    public boolean isRezygnacjaPpk() { return rezygnacjaPpk.get(); }
    public BooleanProperty rezygnacjaPpkProperty() { return rezygnacjaPpk; }

    public String getFormaWyplaty() { return formaWyplaty.get(); }
    public StringProperty formaWyplatyProperty() { return formaWyplaty; }

    public boolean isWynagrodzenieDoRakWlasnych() { return wynagrodzenieDoRakWlasnych.get(); }
    public BooleanProperty wynagrodzenieDoRakWlasnychProperty() { return wynagrodzenieDoRakWlasnych; }

    public boolean isZgodaNaPrzelew() { return zgodaNaPrzelew.get(); }
    public BooleanProperty zgodaNaPrzelewProperty() { return zgodaNaPrzelew; }

    public boolean isRyzykoZawodowe() { return ryzykoZawodowe.get(); }
    public BooleanProperty ryzykoZawodoweProperty() { return ryzykoZawodowe; }

    public boolean isOswiadczenieZUS() { return oswiadczenieZUS.get(); }
    public BooleanProperty oswiadczenieZUSProperty() { return oswiadczenieZUS; }

    public boolean isBhp() { return bhp.get(); }
    public BooleanProperty bhpProperty() { return bhp; }

    public boolean isRegulaminPracy() { return regulaminPracy.get(); }
    public BooleanProperty regulaminPracyProperty() { return regulaminPracy; }

    public boolean isZasadyEwidencjiKasa() { return zasadyEwidencjiKasa.get(); }
    public BooleanProperty zasadyEwidencjiKasaProperty() { return zasadyEwidencjiKasa; }

    public boolean isPit2() { return pit2.get(); }
    public BooleanProperty pit2Property() { return pit2; }

    public boolean isOswiadczenieArt188KP() { return oswiadczenieArt188KP.get(); }
    public BooleanProperty oswiadczenieArt188KPProperty() { return oswiadczenieArt188KP; }

    public boolean isRodo() { return rodo.get(); }
    public BooleanProperty rodoProperty() { return rodo; }

    public boolean isPoraNocna() { return poraNocna.get(); }
    public BooleanProperty poraNocnaProperty() { return poraNocna; }

    public String getPitEmail() { return pitEmail.get(); }
    public StringProperty pitEmailProperty() { return pitEmail; }

    public String getOsobaKontaktowa() { return osobaKontaktowa.get(); }
    public StringProperty osobaKontaktowaProperty() { return osobaKontaktowa; }

    public String getNumerPrywatny() { return numerPrywatny.get(); }
    public StringProperty numerPrywatnyProperty() { return numerPrywatny; }

    public String getNumerSluzbowy() { return numerSluzbowy.get(); }
    public StringProperty numerSluzbowyProperty() { return numerSluzbowy; }

    public String getModelTelefonuSluzbowego() { return modelTelefonuSluzbowego.get(); }
    public StringProperty modelTelefonuSluzbowegoProperty() { return modelTelefonuSluzbowego; }

    public String getOperator() { return operator.get(); }
    public StringProperty operatorProperty() { return operator; }

    public String getWaznoscWizy() { return waznoscWizy.get(); }
    public StringProperty waznoscWizyProperty() { return waznoscWizy; }
}
