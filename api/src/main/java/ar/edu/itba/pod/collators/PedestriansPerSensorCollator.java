package ar.edu.itba.pod.collators;

import com.hazelcast.mapreduce.Collator;

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PedestriansPerSensorCollator implements Collator<Map.Entry<String, Long>, Stream<Map.Entry<String, Long>>> {
    @Override
    public Stream<Map.Entry<String, Long>> collate(Iterable<Map.Entry<String, Long>> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()));
    }
}
