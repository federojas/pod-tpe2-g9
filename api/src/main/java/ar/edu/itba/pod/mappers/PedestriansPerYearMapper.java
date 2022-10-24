package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.DayReading;
import ar.edu.itba.pod.models.YearReading;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class PedestriansPerYearMapper implements Mapper<String, DayReading, Long, YearReading> {
    @Override
    public void map(String key, DayReading reading, Context<Long, YearReading> context) {
        YearReading yearReading = new YearReading(reading.getReadings());
        yearReading.isWeekend(reading.getDay().equals("Saturday")
                || reading.getDay().equals("Sunday"));
        context.emit(reading.getYear(), yearReading);
    }
}
