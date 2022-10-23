package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Query1Reading;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;


public class PedestriansBySensorMapper implements Mapper<String, Query1Reading, String, Long> {
    @Override
    public void map(String key, Query1Reading reading, Context<String, Long> context) {
            context.emit(reading.getSensorName(), reading.getReadings());
    }
}
