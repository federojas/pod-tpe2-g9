package ar.edu.itba.pod.models;

import java.io.Serializable;

public class DateTimeReading implements Serializable {
    private final Long readings;
    private final Long year;
    private final String month;
    private final Integer day;
    private final Integer time;
    private final String sensorName;

    public DateTimeReading(Long readings, Long year, String month, Integer day, Integer time, String sensorName) {
        this.readings = readings;
        this.year = year;
        this.month = month;
        this.day = day;
        this.time = time;
        this.sensorName = sensorName;
    }

    public Long getReadings() {
        return readings;
    }

    public Long getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getTime() {
        return time;
    }

    public String getSensorName() {
        return sensorName;
    }
}
