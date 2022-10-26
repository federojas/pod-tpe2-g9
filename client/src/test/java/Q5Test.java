import ar.edu.itba.pod.collators.PedestriansToMillionGroupCollator;
import ar.edu.itba.pod.collators.SensorsPerMillionGroupCollator;
import ar.edu.itba.pod.combiners.PedestriansBySensorCombiner;
import ar.edu.itba.pod.combiners.SensorsPerMillionGroupCombiner;
import ar.edu.itba.pod.mappers.PedestriansBySensorMapper;
import ar.edu.itba.pod.mappers.SensorsPerMillionGroupMapper;
import ar.edu.itba.pod.models.PairedSensors;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.reducers.PedestriansBySensorReducer;
import ar.edu.itba.pod.reducers.SensorsPerMillionGroupReducer;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;

public class Q5Test extends QueryTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        List<SensorReading> sensorReadings = SensorFactory.getQ5SensorReadingsList();
        String queryName = "Q5_G9";
        String queryJob = "Q5_G9_Job";
        String mapName = "Q5_G9_Map";

        final KeyValueSource<String, SensorReading> source =
                KeyValueSource.fromList(client.getList(queryName));

        IList<SensorReading> readingIList = client.getList(queryName);
        readingIList.addAll(sensorReadings);


        JobTracker jt = client.getJobTracker(queryJob);
        Job<String, SensorReading> job = jt.newJob(source);

        ICompletableFuture<Stream<Map.Entry<String, Long>>> future = job
                .mapper(new PedestriansBySensorMapper())
                .combiner(new PedestriansBySensorCombiner<>())
                .reducer(new PedestriansBySensorReducer<>())
                .submit(new PedestriansToMillionGroupCollator());

        Stream<Map.Entry<String, Long>> result = future.get();

        final IMap<String, Long> pedestriansPerSensorMap = client.getMap(mapName);
        pedestriansPerSensorMap.clear();
        result.forEach(r -> {
            String key = r.getKey();
            Long value = r.getValue();
//            System.out.println(key + ": " + value);
            pedestriansPerSensorMap.put(key, value);
        });

        KeyValueSource<String, Long> dataSource2 = KeyValueSource.fromMap(pedestriansPerSensorMap);

        Job<String, Long> job2 = jt.newJob(dataSource2);

        ICompletableFuture<Map<Long, Set<PairedSensors>>> future2 = job2
                .mapper(new SensorsPerMillionGroupMapper())
                .combiner( new SensorsPerMillionGroupCombiner<>() )
                .reducer( new SensorsPerMillionGroupReducer<>() )
                .submit(new SensorsPerMillionGroupCollator());

        Map<Long, Set<PairedSensors>> result2 = future2.get();
        Map<Long, Set<PairedSensors>> expected = getProcessedData(sensorReadings);
        Long[] resultKeys = result2.keySet().toArray(new Long[result2.size()]);
        Long[] expectedKeys = expected.keySet().toArray(new Long[expected.size()]);
        for (int i = 0 ; i < expectedKeys.length ; i++){
            assertEquals(expectedKeys[i], resultKeys[i]);
            assertSets(expected.get(expectedKeys[i]), result2.get(resultKeys[i]));

        }
    }

    private void assertSets(Set<PairedSensors> expected, Set<PairedSensors> result){
        assertEquals(expected.size(), result.size());
        PairedSensors[] expectedArray = expected.toArray(new PairedSensors[expected.size()]);
        PairedSensors[] resultArray = result.toArray(new PairedSensors[result.size()]);
        for (int i = 0 ; i < expectedArray.length ; i++){
            assertEquals(expectedArray[i].getSensorA(), resultArray[i].getSensorA());
            assertEquals(expectedArray[i].getSensorB(), resultArray[i].getSensorB());
        }
    }


    private Map<Long, Set<PairedSensors>> getProcessedData(List<SensorReading> sensorReadings) {
        Map<Long, Set<PairedSensors>> toReturn =  new TreeMap<>(Comparator.reverseOrder());
        toReturn.put(3000000L, new TreeSet<>(Comparator.comparing(PairedSensors::getSensorA).thenComparing(PairedSensors::getSensorB)));
        toReturn.put(2000000L, new TreeSet<>(Comparator.comparing(PairedSensors::getSensorA).thenComparing(PairedSensors::getSensorB)));
        toReturn.get(3000000L).add(new PairedSensors(sensorReadings.get(2).getSensorName(), sensorReadings.get(3).getSensorName()));
        toReturn.get(2000000L).add(new PairedSensors(sensorReadings.get(0).getSensorName(), sensorReadings.get(1).getSensorName()));
        toReturn.get(2000000L).add(new PairedSensors(sensorReadings.get(0).getSensorName(), sensorReadings.get(5).getSensorName()));
        toReturn.get(2000000L).add(new PairedSensors(sensorReadings.get(1).getSensorName(), sensorReadings.get(5).getSensorName()));

        return toReturn;
    }
}
