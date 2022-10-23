package ar.edu.itba.pod.models;

import java.io.Serializable;

public class SensorReading implements Serializable {
    private Sensor sensor;
    private final Long year;
    private final String month;
    private final Integer mDate;
    private final String day;
    private final String time;
    private final Long hourlyCounts;

    public SensorReading(Sensor sensor, Long year, String month, Integer mDate,
                         String day, String time, Long hourlyCounts) {
        this.sensor = sensor;
        this.year = year;
        this.month = month;
        this.mDate = mDate;
        this.day = day;
        this.time = time;
        this.hourlyCounts = hourlyCounts;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Long getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public Integer getmDate() {
        return mDate;
    }

    public String getDay() {
        return day;
    }

    public Long getHourlyCounts() {
        return hourlyCounts;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public String getTime() {
        return time;
    }
}

