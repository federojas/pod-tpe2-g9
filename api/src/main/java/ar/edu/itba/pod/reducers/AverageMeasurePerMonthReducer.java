package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.MonthAverage;
import ar.edu.itba.pod.models.MonthReading;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.HashMap;
import java.util.Map;

public class AverageMeasurePerMonthReducer<K> implements ReducerFactory<K, MonthReading, MonthAverage> {

    @Override
    public Reducer<MonthReading, MonthAverage> newReducer(K k) {
        return new AverageMeasurePerMonthReducerImpl();
    }

    private static class AverageMeasurePerMonthReducerImpl extends Reducer<MonthReading, MonthAverage> {

        private volatile Map<String,MonthReading> monthReading;

        @Override
        public void reduce(MonthReading value) {
            monthReading.putIfAbsent(value.getMonth(), new MonthReading(value.getMonth(), 0L));
            MonthReading old = monthReading.get(value.getMonth());
            old.setReadings(old.getReadings() + value.getReadings());
        }

        @Override
        public void beginReduce() {
            monthReading = new HashMap<>(12);
        }

        @Override
        public MonthAverage finalizeReduce() {
            MonthReading maxAverage = monthReading.values().stream().max(MonthReading::compareTo)
                    .orElseThrow(IllegalStateException::new);
            return new MonthAverage(maxAverage.getMonth(), maxAverage.getMonthAverage());
        }
    }
}
