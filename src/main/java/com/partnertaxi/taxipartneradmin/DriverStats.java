package com.partnertaxi.taxipartneradmin;

public class DriverStats {
    private final float voucher;
    private final float card;
    private final float cash;
    private final float lot;
    private final float turnover;
    private final float kilometers;
    private final float fuelSum;
    private final boolean missingMileage;

    public DriverStats(float voucher, float card, float cash,
                       float lot, float turnover, float kilometers, float fuelSum,
                       boolean missingMileage) {
        this.voucher = voucher;
        this.card = card;
        this.cash = cash;
        this.lot = lot;
        this.turnover = turnover;
        this.kilometers = kilometers;
        this.fuelSum = fuelSum;
        this.missingMileage = missingMileage;
    }

    public float getVoucher() { return voucher; }
    public float getCard() { return card; }
    public float getCash() { return cash; }
    public float getLot() { return lot; }
    public float getTurnover() { return turnover; }
    public float getKilometers() { return kilometers; }
    public float getFuelSum() { return fuelSum; }
    public boolean isMissingMileage() { return missingMileage; }
}
