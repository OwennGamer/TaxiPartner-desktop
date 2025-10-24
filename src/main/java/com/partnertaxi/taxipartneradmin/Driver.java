// Driver.java
package com.partnertaxi.taxipartneradmin;

public class Driver {
    private String id;
    private String name;
    private String saldo;
    private String status;
    private String rola;

    private float percentTurnover;
    private float fuelCost;
    private float cardCommission;
    private float partnerCommission;
    private float boltCommission;
    private float settlementLimit;

    private float voucherCurrent;
    private float voucherPrevious;
    private float voucher;
    private float card;
    private float cash;
    private float lot;
    private float turnover;
    private float zlPerKm;
    private float fuelPerTurnover;


    private String fuelCostText;
    private String createdAt;
    private String vehiclePlate;

    private float fuelCostSum;   // nowa właściwość
    private boolean summary;

    public Driver(
            String id,
            String name,
            String saldo,
            String status,
            String rola,
            float percentTurnover,
            float fuelCost,
            float cardCommission,
            float partnerCommission,
            float boltCommission,
            float settlementLimit,
            String createdAt,
            String vehiclePlate,
            float fuelCostSum,
            float voucherCurrent,
            float voucherPrevious,
            float voucher,
            float card,
            float cash,
            float lot,
            float turnover,
            float zlPerKm,
            float fuelPerTurnover,
            boolean summary
    ) {
        this.id = id;
        this.name = name;
        this.saldo = saldo;
        this.status = status;
        this.rola = rola;
        this.percentTurnover = percentTurnover;
        this.fuelCost = fuelCost;
        this.cardCommission = cardCommission;
        this.partnerCommission = partnerCommission;
        this.boltCommission = boltCommission;
        this.settlementLimit = settlementLimit;
        this.createdAt = createdAt;
        this.fuelCostText = (fuelCost == 0f) ? "firma" : "kierowca";
        this.vehiclePlate = vehiclePlate;
        this.fuelCostSum = fuelCostSum;
        this.voucherCurrent = voucherCurrent;
        this.voucherPrevious = voucherPrevious;
        this.voucher = voucher;
        this.card = card;
        this.cash = cash;
        this.lot = lot;
        this.turnover = turnover;
        this.zlPerKm = zlPerKm;
        this.fuelPerTurnover = fuelPerTurnover;
        this.summary = summary;
    }

    public Driver(
            String id,
            String name,
            String saldo,
            String status,
            String rola,
            float percentTurnover,
            float fuelCost,
            float cardCommission,
            float partnerCommission,
            float boltCommission,
            float settlementLimit,
            String createdAt,
            String vehiclePlate,
            float fuelCostSum,
            float voucherCurrent,
            float voucherPrevious,
            float voucher,
            float card,
            float cash,
            float lot,
            float turnover,
            float zlPerKm,
            float fuelPerTurnover
    ) {
        this(id, name, saldo, status, rola, percentTurnover, fuelCost, cardCommission,
                partnerCommission, boltCommission, settlementLimit, createdAt, vehiclePlate,
                fuelCostSum, voucherCurrent, voucherPrevious, voucher, card, cash, lot, turnover, zlPerKm, fuelPerTurnover, false);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSaldo() { return saldo; }
    public String getStatus() { return status; }
    public String getRola() { return rola; }

    public float getPercentTurnover() { return percentTurnover; }
    public float getFuelCost() { return fuelCost; }
    public float getCardCommission() { return cardCommission; }
    public float getPartnerCommission() { return partnerCommission; }
    public float getBoltCommission() { return boltCommission; }
    public float getSettlementLimit() { return settlementLimit; }
    public float getVoucherCurrent() { return voucherCurrent; }
    public float getVoucherPrevious() { return voucherPrevious; }
    public float getVoucher() { return voucher; }
    public float getCard() { return card; }
    public float getCash() { return cash; }
    public float getLot() { return lot; }
    public float getTurnover() { return turnover; }
    public float getZlPerKm() { return zlPerKm; }
    public float getFuelPerTurnover() { return fuelPerTurnover; }

    public String getFuelCostText() { return fuelCostText; }
    public String getCreatedAt() { return createdAt; }
    public String getVehiclePlate() { return vehiclePlate; }

    public float getFuelCostSum() {      // nowy getter
        return fuelCostSum;
    }

    public boolean isSummary() {
        return summary;
    }
}
