package ar.edu.itba.pod.mappers;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;
import models.SensorReading;
import models.Status;

public class PedestriansBySensorMapper implements Mapper<Long, SensorReading, String, Long>{
    @Override
    public void map(Long aLong, SensorReading reading, Context<String, Long> context) {
        if (reading.getSensor().getStatus().equals(Status.A))
            context.emit(reading.getSensor().getSensorName(), reading.getHourlyCounts());
    }
}
