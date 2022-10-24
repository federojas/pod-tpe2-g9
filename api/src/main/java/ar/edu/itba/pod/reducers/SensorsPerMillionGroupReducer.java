package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.YearCount;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.ArrayList;
import java.util.List;

public class SensorsPerMillionGroupReducer<K> implements ReducerFactory<K, List<String>, List<String>> {

    @Override
    public Reducer<List<String>, List<String>> newReducer(K k) {
        return new SensorsPerMillionGroupReducerImpl();
    }

    private static class SensorsPerMillionGroupReducerImpl extends Reducer<List<String>, List<String>> {
        private volatile List<String> sensors;

        @Override
        public void beginReduce() {
            sensors = new ArrayList<>();
        }

        @Override
        public void reduce(List<String> value) {
            sensors.addAll(value);
        }

        @Override
        public List<String> finalizeReduce() {
            return sensors;
        }
    }
}
