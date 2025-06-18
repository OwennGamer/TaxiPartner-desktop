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

    private String fuelCostText;
    private String createdAt;
    private String vehiclePlate;

    private float fuelCostSum;   // nowa właściwość

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
            float fuelCostSum            // nowy parametr
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
        this.fuelCostSum = fuelCostSum;   // przypisanie
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

    public String getFuelCostText() { return fuelCostText; }
    public String getCreatedAt() { return createdAt; }
    public String getVehiclePlate() { return vehiclePlate; }

    public float getFuelCostSum() {      // nowy getter
        return fuelCostSum;
    }
}
