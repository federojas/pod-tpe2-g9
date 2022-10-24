package ar.edu.itba.pod.models;

import java.io.Serializable;

public class Query3Reading implements Serializable {
    private final String sensorName;
    private final Long readings;
    private final String dateTime;

    public Query3Reading(String sensorName, Long readings, String dateTime) {
        this.sensorName = sensorName;
        this.readings = readings;
        this.dateTime = dateTime;
    }

    public String getSensorName() {
        return sensorName;
    }

    public Long getReadings() {
        return readings;
    }

    public String getDateTime() {
        return dateTime;
    }
}
