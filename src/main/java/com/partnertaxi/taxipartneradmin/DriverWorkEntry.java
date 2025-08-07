package com.partnertaxi.taxipartneradmin;

public class DriverWorkEntry {
    private final String date;
    private final float hours;
    private final int kilometers;
    private final String startTime;
    private final String endTime;

    public DriverWorkEntry(String date, float hours, int kilometers) {
        this(date, hours, kilometers, null, null);
    }

    public DriverWorkEntry(String date, float hours, int kilometers, String startTime, String endTime) {
        this.date = date;
        this.hours = hours;
        this.kilometers = kilometers;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDate() { return date; }
    public float getHours() { return hours; }
    public int getKilometers() { return kilometers; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}
