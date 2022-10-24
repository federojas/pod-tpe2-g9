package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Query3Reading;
import ar.edu.itba.pod.models.FeasibleMaxMeasure;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class MaxMeasurePerSensorMapper implements Mapper<String, Query3Reading, String, FeasibleMaxMeasure> {
    @Override
    public void map(String s, Query3Reading query3Reading, Context<String, FeasibleMaxMeasure> context) {
        context.emit(query3Reading.getSensorName(),
                new FeasibleMaxMeasure(
                        query3Reading.getReadings(),
                        query3Reading.getYear(),
                        query3Reading.getMonth(),
                        query3Reading.getDay(),
                        query3Reading.getTime()));
    }
}
