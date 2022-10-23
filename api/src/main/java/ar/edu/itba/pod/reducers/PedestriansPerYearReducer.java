package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.YearCount;
import ar.edu.itba.pod.models.YearReading;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class PedestriansPerYearReducer<K> implements ReducerFactory<K, YearReading, YearCount> {

    @Override
    public Reducer<YearReading, YearCount> newReducer(K k) {
        return new PedestriansPerYearReducerImpl();
    }

    private static class PedestriansPerYearReducerImpl extends Reducer<YearReading, YearCount> {
        private volatile YearCount yearCount;

        @Override
        public void beginReduce() {
            yearCount = new YearCount();
        }

        @Override
        public void reduce(YearReading value) {
            if(value.isWeekend())
                yearCount.setReadingsInWeekends(value.getReadings() + yearCount.getReadingsInWeekends());
            else
                yearCount.setReadingsInWorkweeks(value.getReadings() + yearCount.getReadingsInWorkweeks());
            yearCount.setReadingsTotal(value.getReadings() + yearCount.getReadingsTotal());
        }

        @Override
        public YearCount finalizeReduce() {
            return yearCount;
        }
    }
}
