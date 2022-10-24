package ar.edu.itba.pod.models;

import java.io.Serializable;

public class DayReading implements Serializable {
    private final Long year;
    private final String day;
    private final Long readings;


    public DayReading(Long year, String day, Long readings) {
        this.year = year;
        this.day = day;
        this.readings = readings;
    }

    public Long getYear() {
        return year;
    }

    public String getDay() {
        return day;
    }

    public Long getReadings() {
        return readings;
    }
}
