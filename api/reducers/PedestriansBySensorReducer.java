package reducers;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class PedestriansBySensorReducer<K> implements ReducerFactory<K, Long, Long> {
    @Override
    public Reducer<Long, Long> newReducer(K key) {
        return new PedestriansBySensorReducerImp();
    }

    private static class PedestriansBySensorReducerImp extends Reducer<Long, Long> {
        private volatile long pedestrianCount;

        @Override
        public void beginReduce() {
            pedestrianCount = 0;
        }

        @Override
        public void reduce(Long value) {
            pedestrianCount += value;
        }

        @Override
        public Long finalizeReduce() {
            return pedestrianCount;
        }
    }
}
