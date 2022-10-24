package ar.edu.itba.pod.models;

import java.io.Serializable;
import java.util.Objects;

public class PairedSensors implements Serializable, Comparable<PairedSensors> {
    private final String sensorA;
    private final String sensorB;

    public PairedSensors(String sensorA, String sensorB) {
        this.sensorA = sensorA;
        this.sensorB = sensorB;
    }

    public String getSensorA() {
        return sensorA;
    }

    public String getSensorB() {
        return sensorB;
    }

    @Override
    public int compareTo(PairedSensors o) {
        int result = this.sensorA.compareTo(o.sensorA);
        if(result == 0)
            result = this.sensorB.compareTo(o.sensorB);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PairedSensors that = (PairedSensors) o;
        return Objects.equals(getSensorA(), that.getSensorA()) && Objects.equals(getSensorB(), that.getSensorB());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSensorA(), getSensorB());
    }
}
