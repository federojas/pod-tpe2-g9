package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Query2Reading;
import ar.edu.itba.pod.models.YearReading;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class PedestriansPerYearMapper implements Mapper<Long, Query2Reading, Long, YearReading> {
    @Override
    public void map(Long aLong, Query2Reading reading, Context<Long, YearReading> context) {
        YearReading yearReading = new YearReading(reading.getReadings());
        yearReading.isWeekend(reading.getDay().equals("Saturday")
                || reading.getDay().equals("Sunday"));
        context.emit(reading.getYear(), yearReading);
    }
}
