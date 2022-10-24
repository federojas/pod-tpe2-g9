package ar.edu.itba.pod.combiners;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.util.ArrayList;
import java.util.List;

public class SensorsPerMillionGroupCombiner<K> implements CombinerFactory<K, String, List<String>> {
    @Override
    public Combiner<String, List<String>> newCombiner(K k) {
        return new SensorsPerMillionGroupCombinerImpl();
    }

    private static class SensorsPerMillionGroupCombinerImpl extends Combiner<String, List<String>> {

        private List<String> sensors = new ArrayList<>();

        @Override
        public void combine(String s) {
            sensors.add(s);
        }

        @Override
        public List<String> finalizeChunk() {
            return sensors;
        }

        @Override
        public void reset() {
            sensors = new ArrayList<>();
        }
    }
}
