package ar.edu.itba.pod.models;

import java.io.Serializable;

public class Query1Reading implements Serializable {
    private String sensorName;
    private Long readings;

    public Query1Reading(String sensorName, Long readings) {
        this.sensorName = sensorName;
        this.readings = readings;
    }

    public String getSensorName() {
        return sensorName;
    }

    public Long getReadings() {
        return readings;
    }
}
