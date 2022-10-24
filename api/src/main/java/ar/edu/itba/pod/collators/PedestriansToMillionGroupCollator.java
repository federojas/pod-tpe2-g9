package ar.edu.itba.pod.collators;

import com.hazelcast.mapreduce.Collator;

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PedestriansToMillionGroupCollator implements Collator<Map.Entry<String, Long>, Stream<Map.Entry<String, Long>>> {

    @Override
    public Stream<Map.Entry<String, Long>> collate(Iterable<Map.Entry<String, Long>> iterable) {
        for (Map.Entry<String, Long> reading : iterable) {
            reading.setValue((reading.getValue() % 1000000) * 1000000);
        }
        return StreamSupport.stream(iterable.spliterator(), false)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()));
    }
}
