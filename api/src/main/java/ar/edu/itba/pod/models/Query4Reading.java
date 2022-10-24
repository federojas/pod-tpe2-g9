package ar.edu.itba.pod.models;

import java.io.Serializable;

public class Query4Reading implements Serializable {
    private final Long readings;
    private final String month;
    private final String sensorName;

    public Query4Reading(Long readings, String month, String sensorName) {
        this.readings = readings;
        this.month = month;
        this.sensorName = sensorName;
    }

    public Long getReadings() {
        return readings;
    }

    public String getMonth() {
        return month;
    }

    public String getSensorName() {
        return sensorName;
    }
}
