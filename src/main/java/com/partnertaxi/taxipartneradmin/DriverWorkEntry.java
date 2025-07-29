package com.partnertaxi.taxipartneradmin;

public class DriverWorkEntry {
    private final String date;
    private final float hours;
    private final int kilometers;

    public DriverWorkEntry(String date, float hours, int kilometers) {
        this.date = date;
        this.hours = hours;
        this.kilometers = kilometers;
    }

    public String getDate() { return date; }
    public float getHours() { return hours; }
    public int getKilometers() { return kilometers; }
}
