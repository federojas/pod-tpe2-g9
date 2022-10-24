package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Query3Reading;
import ar.edu.itba.pod.models.SensorMeasure;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class MaxMeasurePerSensorMapper implements Mapper<String, Query3Reading, String, SensorMeasure> {
    @Override
    public void map(String s, Query3Reading query3Reading, Context<String, SensorMeasure> context) {
        context.emit(query3Reading.getSensorName(),
                new SensorMeasure(query3Reading.getReadings(), query3Reading.getDateTime()));
    }
}
