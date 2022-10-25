package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.MonthReading;
import ar.edu.itba.pod.models.SensorReading;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class AverageMeasurePerMonthMapper implements Mapper<String, SensorReading, String, MonthReading>  {
    @Override
    public void map(String s, SensorReading sensorReading, Context<String, MonthReading> context) {
        context.emit(sensorReading.getSensorName(), new MonthReading(sensorReading.getMonth(), sensorReading.getReadings()));
    }
}
