package ar.edu.itba.pod.models;

import java.io.Serializable;

public class Sensor implements Serializable {
    private final Status status;
    private final String description;
    private final Long sensorId;

    public Sensor(Status status, String description, Long sensorId) {
        this.status = status;
        this.description = description;
        this.sensorId = sensorId;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public Long getSensorId() {
        return sensorId;
    }
}
