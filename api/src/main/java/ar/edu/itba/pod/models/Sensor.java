package ar.edu.itba.pod.models;

import java.io.Serializable;

public class Sensor implements Serializable {
    private final Status status;
    private final String sensorName;
    private final String description;
    private final Long sensorId;

    public Sensor(Status status, String sensorName, String description, Long sensorId) {
        this.status = status;
        this.sensorName = sensorName;
        this.description = description;
        this.sensorId = sensorId;
    }

    public Status getStatus() {
        return status;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String getDescription() {
        return description;
    }

    public Long getSensorId() {
        return sensorId;
    }
}
