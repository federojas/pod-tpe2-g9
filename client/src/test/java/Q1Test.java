import ar.edu.itba.pod.collators.PedestriansPerSensorCollator;
import ar.edu.itba.pod.combiners.PedestriansBySensorCombiner;
import ar.edu.itba.pod.mappers.PedestriansBySensorMapper;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.reducers.PedestriansBySensorReducer;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Q1Test extends QueryTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        SensorReading[] activeSensors = SensorFactory.getActiveSensors();
        int activeSensorsLength = activeSensors.length;
        long activeSensorsStep = SensorFactory.ACTIVE_SENSOR_STEP;
        String queryName = "Q1_G9";
        String queryJob = "Q1_G9_Job";

        String expectedHighestSensor = SensorFactory.activeSensorNames[activeSensorsLength - 1];
        long expectedHighestCount = activeSensorsLength * activeSensorsLength * activeSensorsStep;
        String expectedLowestSensor = SensorFactory.activeSensorNames[0];

        IList<SensorReading> readingIList = client.getList(queryName);

//        Generate lots of readings for each sensor
//        It's certain that the first one will be the one with the lowest readings
//        because only 1 reading (each one with count: activeSensorStep) is added
//        On the other hand, the last one will be the one with the highest readings
//        because is added activeSensorsLength with the value of activeSensorsStep
        for (int i = 0; i < activeSensorsLength; i++) {
            for (int j = 0; j < i+1; j++) {
                readingIList.add(activeSensors[i]);
            }
        }

        final KeyValueSource<String, SensorReading> source =
                KeyValueSource.fromList(client.getList(queryName));

        JobTracker jt = client.getJobTracker(queryJob);
        Job<String, SensorReading> job = jt.newJob(source);

        ICompletableFuture<Stream<Map.Entry<String, Long>>> future = job
                .mapper(new PedestriansBySensorMapper())
                .combiner( new PedestriansBySensorCombiner<>() )
                .reducer( new PedestriansBySensorReducer<>() )
                .submit(new PedestriansPerSensorCollator());

        List<Map.Entry<String, Long>> result = future.get().collect(Collectors.toList());

        assertNotNull(result);

        assertEquals(expectedHighestSensor, result.get(0).getKey());
        assertEquals(expectedHighestCount, result.get(0).getValue());

        assertEquals(expectedLowestSensor, result.get(result.size() - 1).getKey());
        assertEquals(activeSensorsStep, result.get(result.size() - 1).getValue());

    }


}
