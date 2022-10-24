package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.YearCount;
import com.hazelcast.mapreduce.Collator;

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PedestriansPerYearCollator implements Collator<Map.Entry<Long, YearCount>, Stream<Map.Entry<Long, YearCount>>> {

    @Override
    public Stream<Map.Entry<Long, YearCount>> collate(Iterable<Map.Entry<Long, YearCount>> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .sorted(Map.Entry.<Long, YearCount>comparingByKey().reversed());
    }
}
