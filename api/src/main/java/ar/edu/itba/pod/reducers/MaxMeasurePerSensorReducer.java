package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.SensorMeasure;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class MaxMeasurePerSensorReducer <K> implements ReducerFactory<K, SensorMeasure, SensorMeasure> {
    @Override
    public Reducer<SensorMeasure, SensorMeasure> newReducer(K key) {
        return new MaxMeasurePerSensorReducerImp();
    }

    private static class MaxMeasurePerSensorReducerImp extends Reducer<SensorMeasure, SensorMeasure> {
        private volatile SensorMeasure sensorMeasure;

        @Override
        public void beginReduce() {
            sensorMeasure = new SensorMeasure(0L,"-");
        }

        @Override
        public void reduce(SensorMeasure value) {
            // TODO: Desempate por date time
            if(value.getCount() > sensorMeasure.getCount())
                sensorMeasure = value;
        }

        @Override
        public SensorMeasure finalizeReduce() {
            return sensorMeasure;
        }
    }
}
