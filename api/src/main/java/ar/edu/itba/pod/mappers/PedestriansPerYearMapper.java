package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.models.YearReading;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class PedestriansPerYearMapper implements Mapper<Long, SensorReading, Long, YearReading> {
    @Override
    public void map(Long aLong, SensorReading sensorReading, Context<Long, YearReading> context) {
        YearReading yearReading = new YearReading(sensorReading.getHourlyCounts());
        yearReading.isWeekend(sensorReading.getDay().equals("Saturday")
                || sensorReading.getDay().equals("Sunday"));
        context.emit(sensorReading.getYear(), yearReading);
    }
}
