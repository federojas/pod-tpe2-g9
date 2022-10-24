package ar.edu.itba.pod.models;

import java.io.Serializable;

public class SensorReading implements Serializable {
    private final String sensorName;
    private final Long readings;

    public SensorReading(String sensorName, Long readings) {
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
