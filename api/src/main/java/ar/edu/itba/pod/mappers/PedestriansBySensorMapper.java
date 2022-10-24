package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.SensorReading;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;


public class PedestriansBySensorMapper implements Mapper<String, SensorReading, String, Long> {
    @Override
    public void map(String key, SensorReading reading, Context<String, Long> context) {
            context.emit(reading.getSensorName(), reading.getReadings());
    }
}
