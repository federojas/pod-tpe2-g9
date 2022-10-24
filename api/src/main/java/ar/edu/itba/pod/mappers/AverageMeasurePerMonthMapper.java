package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.MonthReading;
import ar.edu.itba.pod.models.SensorMonthReading;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class AverageMeasurePerMonthMapper implements Mapper<String, SensorMonthReading, String, MonthReading>  {
    @Override
    public void map(String s, SensorMonthReading sensorMonthReading, Context<String, MonthReading> context) {
        context.emit(sensorMonthReading.getSensorName(), new MonthReading(sensorMonthReading.getMonth(), sensorMonthReading.getReadings()));
    }
}
