package ar.edu.itba.pod.models;

import java.io.Serializable;

public class PairedSensors implements Serializable {
    private final Long group;
    private final String sensorA;
    private final String sensorB;

    public PairedSensors(Long group, String sensorA, String sensorB) {
        this.group = group;
        this.sensorA = sensorA;
        this.sensorB = sensorB;
    }

    public Long getGroup() {
        return group;
    }

    public String getSensorA() {
        return sensorA;
    }

    public String getSensorB() {
        return sensorB;
    }
}
