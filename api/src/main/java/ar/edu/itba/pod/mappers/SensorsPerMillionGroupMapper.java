package ar.edu.itba.pod.mappers;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class SensorsPerMillionGroupMapper implements Mapper<String, Long, Long, String> {
    @Override
    public void map(String sensorName, Long readings, Context<Long, String> context) {
        context.emit(readings, sensorName);
    }
}
