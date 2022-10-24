package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.MonthAverage;
import ar.edu.itba.pod.models.MonthReading;
import com.hazelcast.mapreduce.Collator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AverageMeasurePerMonthCollator implements Collator<Map.Entry<String, MonthReading>, Stream<Map.Entry<String, MonthAverage>>> {

    private final Long top;

    public AverageMeasurePerMonthCollator(Long top) {
        this.top = top;
    }

    @Override
    public Stream<Map.Entry<String, MonthAverage>> collate(Iterable<Map.Entry<String, MonthReading>> iterable) {
        Map<String, MonthAverage> averageMap = new HashMap<>();

        for (Map.Entry<String, MonthReading> reading : iterable) {
            averageMap.put(reading.getKey(), new MonthAverage(reading.getValue().getMonth(),
                    reading.getValue().getMonthAverage()));
        }

        return averageMap.entrySet().stream()
                .sorted(Map.Entry.<String, MonthAverage>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey())).limit(top);
    }
}
