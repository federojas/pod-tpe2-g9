package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.MonthReading;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class AverageMeasurePerMonthReducer<K> implements ReducerFactory<K, MonthReading, MonthReading> {

    @Override
    public Reducer<MonthReading, MonthReading> newReducer(K k) {
        return new AverageMeasurePerMonthReducerImpl();
    }

    private static class AverageMeasurePerMonthReducerImpl extends Reducer<MonthReading, MonthReading> {

        private volatile MonthReading monthReading;

        @Override
        public void reduce(MonthReading value) {
            if(value.getMonth().equals(monthReading.getMonth()))
                monthReading.setReadings(monthReading.getReadings() + value.getReadings());
        }

        @Override
        public void beginReduce() {
            monthReading = new MonthReading();
        }

        @Override
        public MonthReading finalizeReduce() {
            return monthReading;
        }
    }
}
