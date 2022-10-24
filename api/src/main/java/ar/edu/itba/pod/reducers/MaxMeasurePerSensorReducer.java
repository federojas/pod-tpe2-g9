package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.FeasibleMaxMeasure;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MaxMeasurePerSensorReducer <K> implements ReducerFactory<K, FeasibleMaxMeasure, FeasibleMaxMeasure> {
    @Override
    public Reducer<FeasibleMaxMeasure, FeasibleMaxMeasure> newReducer(K key) {
        return new MaxMeasurePerSensorReducerImp();
    }

    private static class MaxMeasurePerSensorReducerImp extends Reducer<FeasibleMaxMeasure, FeasibleMaxMeasure> {
        private volatile FeasibleMaxMeasure feasibleMaxMeasure;

        @Override
        public void beginReduce() {
            feasibleMaxMeasure = new FeasibleMaxMeasure();
        }

        @Override
        public void reduce(FeasibleMaxMeasure value) {
            if(value.getCount() > feasibleMaxMeasure.getCount())
                feasibleMaxMeasure = value;
            else if(value.getCount().equals(feasibleMaxMeasure.getCount())) {
                if(value.getDate().after(feasibleMaxMeasure.getDate()))
                    feasibleMaxMeasure = value;
            }
        }

        @Override
        public FeasibleMaxMeasure finalizeReduce() {
            return feasibleMaxMeasure;
        }


    }
}
