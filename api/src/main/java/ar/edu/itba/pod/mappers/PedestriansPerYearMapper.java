package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.models.YearReading;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class PedestriansPerYearMapper implements Mapper<String, SensorReading, Long, YearReading> {
    @Override
    public void map(String key, SensorReading reading, Context<Long, YearReading> context) {
        YearReading yearReading = new YearReading(reading.getReadings());
        yearReading.isWeekend(reading.getWeekDay().equals("Saturday")
                || reading.getWeekDay().equals("Sunday"));
        context.emit(reading.getYear(), yearReading);
    }
}
