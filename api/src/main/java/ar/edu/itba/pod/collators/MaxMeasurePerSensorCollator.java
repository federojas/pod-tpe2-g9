package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.FeasibleMaxMeasure;
import com.hazelcast.mapreduce.Collator;

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MaxMeasurePerSensorCollator implements Collator<Map.Entry<String, FeasibleMaxMeasure>, Stream<Map.Entry<String, FeasibleMaxMeasure>>> {
    @Override
    public Stream<Map.Entry<String, FeasibleMaxMeasure>> collate(Iterable<Map.Entry<String, FeasibleMaxMeasure>> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .sorted(Map.Entry.<String, FeasibleMaxMeasure>comparingByValue().reversed()
                                .thenComparing(Map.Entry.comparingByKey()));
    }
}
