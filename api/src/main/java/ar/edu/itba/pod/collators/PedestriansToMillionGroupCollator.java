package ar.edu.itba.pod.collators;

import com.hazelcast.mapreduce.Collator;

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PedestriansToMillionGroupCollator implements Collator<Map.Entry<String, Long>, Stream<Map.Entry<String, Long>>> {

    @Override
    public Stream<Map.Entry<String, Long>> collate(Iterable<Map.Entry<String, Long>> iterable) {
        for (Map.Entry<String, Long> reading : iterable) {
            long group = reading.getValue() / 1000000L ;
            if(group != 0)
                reading.setValue(group * 1000000L);
        }
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
