package ar.edu.itba.pod.models;

import java.io.Serializable;

public class ActiveSensor implements Serializable {
    private final String description;
    private final Long sensorId;

    public ActiveSensor(String description, Long sensorId) {
        this.description = description;
        this.sensorId = sensorId;
    }

    public String getDescription() {
        return description;
    }

    public Long getSensorId() {
        return sensorId;
    }
}
