import ar.edu.itba.pod.collators.PedestriansToMillionGroupCollator;
import ar.edu.itba.pod.collators.SensorsPerMillionGroupCollator;
import ar.edu.itba.pod.combiners.PedestriansBySensorCombiner;
import ar.edu.itba.pod.combiners.SensorsPerMillionGroupCombiner;
import ar.edu.itba.pod.mappers.PedestriansBySensorMapper;
import ar.edu.itba.pod.mappers.SensorsPerMillionGroupMapper;
import ar.edu.itba.pod.models.PairedSensors;
import ar.edu.itba.pod.models.SensorMonthReading;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            System.out.println(key + ": " + value);
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

        result2.forEach((k, v) -> {
            if(v.size() > 0) {
                v.forEach(x -> System.out.print(k + ": " + x.getSensorA() + ", " + x.getSensorB() + "\n"));
            }
        });


    }
}
