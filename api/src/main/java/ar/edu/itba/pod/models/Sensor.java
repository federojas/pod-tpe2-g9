package ar.edu.itba.pod.models;

public class Sensor {
    private Status status;
    private String sensorName;
    private String description;
    private Long sensorId;

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
