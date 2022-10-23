package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.models.Status;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;


public class PedestriansBySensorMapper implements Mapper<String, SensorReading, String, Long>{
    @Override
    public void map(String key, SensorReading reading, Context<String, Long> context) {
        if (reading.getSensor() != null && reading.getSensor().getStatus().equals(Status.A))
            context.emit(reading.getSensor().getDescription(), reading.getHourlyCounts());
    }
}
