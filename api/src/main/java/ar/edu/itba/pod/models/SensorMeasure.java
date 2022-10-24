package ar.edu.itba.pod.models;

import java.io.Serializable;

public class SensorMeasure implements Serializable, Comparable<SensorMeasure> {
    private final Long count;
    private final String dateTime;

    public SensorMeasure(Long count, String dateTime) {
        this.count = count;
        this.dateTime = dateTime;
    }

    public Long getCount() {
        return count;
    }

    public String getDateTime() {
        return dateTime;
    }

    @Override
    public int compareTo(SensorMeasure sensorMeasure) {
       return this.getCount().compareTo(sensorMeasure.getCount());
    }
}
