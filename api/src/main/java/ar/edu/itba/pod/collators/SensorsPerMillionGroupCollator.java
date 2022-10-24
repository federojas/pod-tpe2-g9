package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.PairedSensors;
import com.hazelcast.mapreduce.Collator;

import java.util.*;

public class SensorsPerMillionGroupCollator implements Collator<Map.Entry<Long, List<String>>, Map<Long, Set<PairedSensors>>> {

    @Override
    public Map<Long, Set<PairedSensors>> collate(Iterable<Map.Entry<Long, List<String>>> iterable) {
        Map<Long, Set<PairedSensors>> map = new TreeMap<>(Comparator.reverseOrder());

        for (Map.Entry<Long, List<String>> entry : iterable) {
            map.put(entry.getKey(), new TreeSet<>());
            for(String sensor : entry.getValue()) {
                for(String sensor2 : entry.getValue()) {
                    if(sensor.compareTo(sensor2) < 0)
                        map.get(entry.getKey()).add(new PairedSensors(sensor, sensor2));
                }
            }
        }
        return map;
    }
}
