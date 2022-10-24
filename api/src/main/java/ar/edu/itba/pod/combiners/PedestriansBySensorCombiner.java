package ar.edu.itba.pod.combiners;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class PedestriansBySensorCombiner<K> implements CombinerFactory<K, Long, Long> {

        @Override
        public Combiner<Long, Long> newCombiner(K key) {
            return new PedestriansBySensorCombinerImpl();
        }

        private static class PedestriansBySensorCombinerImpl extends Combiner<Long, Long> {
            private long pedestrianCount = 0;

            @Override
            public void combine(Long value) {
                pedestrianCount += value;
            }

            @Override
            public Long finalizeChunk() {
                return pedestrianCount;
            }

            @Override
            public void reset() {
                pedestrianCount = 0;
            }
        }
}