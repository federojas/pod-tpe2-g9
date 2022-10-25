package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.FeasibleMaxMeasure;
import ar.edu.itba.pod.models.SensorReading;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class MaxMeasurePerSensorMapper implements Mapper<String, SensorReading, String, FeasibleMaxMeasure> {
    @Override
    public void map(String s, SensorReading dateTimeReading, Context<String, FeasibleMaxMeasure> context) {
        context.emit(dateTimeReading.getSensorName(),
                new FeasibleMaxMeasure(
                        dateTimeReading.getReadings(),
                        dateTimeReading.getYear(),
                        dateTimeReading.getMonth(),
                        dateTimeReading.getDay(),
                        dateTimeReading.getTime()));
    }
}
