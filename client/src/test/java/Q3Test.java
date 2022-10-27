import ar.edu.itba.pod.collators.MaxMeasurePerSensorCollator;
import ar.edu.itba.pod.collators.PedestriansPerSensorCollator;
import ar.edu.itba.pod.combiners.PedestriansBySensorCombiner;
import ar.edu.itba.pod.mappers.MaxMeasurePerSensorMapper;
import ar.edu.itba.pod.mappers.PedestriansBySensorMapper;
import ar.edu.itba.pod.models.FeasibleMaxMeasure;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.reducers.MaxMeasurePerSensorReducer;
import ar.edu.itba.pod.reducers.PedestriansBySensorReducer;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Q3Test extends QueryTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        SensorReading[] dateTimeReadings = SensorFactory.getDateTimeReadings();
        long minCount = SensorFactory.MIN_COUNT;

        String queryName = "g9-Q3";
        String queryJob = "g9-Q3-Job";

        IList<SensorReading> readingIList = client.getList(queryName);
        readingIList.clear();

        for (SensorReading dateTimeReading : dateTimeReadings) {
            if (dateTimeReading.getReadings() > minCount) {
                readingIList.add(dateTimeReading);
            }
        }

        final KeyValueSource<String, SensorReading> source =
                KeyValueSource.fromList(client.getList(queryName));

        JobTracker jt = client.getJobTracker(queryJob);
        Job<String, SensorReading> job = jt.newJob(source);

        ICompletableFuture<Stream<Map.Entry<String, FeasibleMaxMeasure>>> future = job
                .mapper(new MaxMeasurePerSensorMapper())
                .reducer( new MaxMeasurePerSensorReducer<>() )
                .submit(new MaxMeasurePerSensorCollator());

        List<Map.Entry<String, FeasibleMaxMeasure>> result = future.get().collect(Collectors.toList());

        assertNotNull(result);

        int expectedOutputLength = SensorFactory.Q3_EXPECTED_OUTPUT_LENGTH;
        String[] expectedSensorNames = SensorFactory.getQ3ValidInOrderSensorNames();
        FeasibleMaxMeasure[] expectedFeasibleMaxMeasures = SensorFactory.getQ3ValidMeasures();

        for (int i = 0; i < expectedOutputLength; i++) {
            assertTrue(expectedFeasibleMaxMeasures[i].getCount() > minCount);
            assertEquals(expectedSensorNames[i], result.get(i).getKey());
            assertEquals(expectedFeasibleMaxMeasures[i].getCount(), result.get(i).getValue().getCount());
        }
    }






}
