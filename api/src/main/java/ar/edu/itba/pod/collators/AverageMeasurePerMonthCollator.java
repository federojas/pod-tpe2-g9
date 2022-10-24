package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.MonthAverage;
import com.hazelcast.mapreduce.Collator;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AverageMeasurePerMonthCollator implements Collator<Map.Entry<String, MonthAverage>, Stream<Map.Entry<String, MonthAverage>>> {
    private final Long top;

    public AverageMeasurePerMonthCollator(Long top) {
        this.top = top;
    }

    @Override
    public Stream<Map.Entry<String, MonthAverage>> collate(Iterable<Map.Entry<String, MonthAverage>> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .sorted(Map.Entry.<String, MonthAverage>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey())).limit(top);
    }
}
