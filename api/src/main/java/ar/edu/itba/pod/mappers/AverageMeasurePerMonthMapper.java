package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.MonthReading;
import ar.edu.itba.pod.models.Query4Reading;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class AverageMeasurePerMonthMapper implements Mapper<String, Query4Reading, String, MonthReading>  {
    @Override
    public void map(String s, Query4Reading query4Reading, Context<String, MonthReading> context) {
        context.emit(query4Reading.getSensorName(), new MonthReading(query4Reading.getMonth(), query4Reading.getReadings()));
    }
}
