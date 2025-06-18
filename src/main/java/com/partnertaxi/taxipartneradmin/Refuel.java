package com.partnertaxi.taxipartneradmin;

public class Refuel {
    private String id;
    private String refuelDate;
    private float fuelAmount;
    private float cost;
    private int odometer;

    public Refuel(String id, String refuelDate, float fuelAmount, float cost, int odometer) {
        this.id = id;
        this.refuelDate = refuelDate;
        this.fuelAmount = fuelAmount;
        this.cost = cost;
        this.odometer = odometer;
    }

    public String getId() {
        return id;
    }

    public String getRefuelDate() {
        return refuelDate;
    }

    public float getFuelAmount() {
        return fuelAmount;
    }

    public float getCost() {
        return cost;
    }

    public int getOdometer() {
        return odometer;
    }
}
