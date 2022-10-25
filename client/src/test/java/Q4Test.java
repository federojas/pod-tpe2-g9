import ar.edu.itba.pod.collators.AverageMeasurePerMonthCollator;
import ar.edu.itba.pod.mappers.AverageMeasurePerMonthMapper;
import ar.edu.itba.pod.models.*;
import ar.edu.itba.pod.reducers.AverageMeasurePerMonthReducer;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;


public class Q4Test extends QueryTest {

        @Test
        public void test() throws ExecutionException, InterruptedException {
            List<SensorMonthReading> sensorMonthReadings = SensorFactory.getSensorMonthReadingsList();
            String queryName = "Q4_G9";
            String queryJob = "Q4_G9_Job";

            List<Pair<String, MonthAverage>> expected = getExpectedResult();

            IList<SensorMonthReading> readingIList = client.getList(queryName);
            readingIList.addAll(sensorMonthReadings);

            final KeyValueSource<String, SensorMonthReading> source =
                    KeyValueSource.fromList(client.getList(queryName));

            JobTracker jt = client.getJobTracker(queryJob);
            Job<String, SensorMonthReading> job = jt.newJob(source);

            ICompletableFuture<Stream<Map.Entry<String, MonthAverage>>> future = job
                    .mapper(new AverageMeasurePerMonthMapper())
                    .reducer(new AverageMeasurePerMonthReducer<>())
                    .submit(new AverageMeasurePerMonthCollator(10L));

            Stream<Map.Entry<String, MonthAverage>> result = future.get();
            List<Map.Entry<String, MonthAverage>> l = result.collect(Collectors.toList());
            int i = 0;
            for(Map.Entry<String, MonthAverage> k : l){
                assertEquals(expected.get(i).getKey(), k.getKey());
                assertEquals(expected.get(i).getValue().getAverage(), k.getValue().getAverage());
                assertEquals(expected.get(i).getValue().getMonth(), k.getValue().getMonth());
                i++;
            }


        }

        private List<Pair<String,MonthAverage>> getExpectedResult(){
            List<Pair<String, MonthAverage>> toReturn = new ArrayList<>();
            toReturn.add(new Pair<>("Sensor Villa Devoto",new MonthAverage("April", 400/30.)));
            toReturn.add(new Pair<>("Sensor Villa Luro", new MonthAverage("March", 300/31.)));
            toReturn.add(new Pair<>("Sensor Villa Pueyrredon", new MonthAverage("March", 300/31.)));
            toReturn.add(new Pair<>("Sensor Villa Urquiza", new MonthAverage("May", 200/31.)));
            return toReturn;
        }

        class Pair<K,V> {
            private K key;
            private V value;

            public Pair(K key, V value) {
                this.key = key;
                this.value = value;
            }

            public K getKey() {
                return key;
            }

            public V getValue() {
                return value;
            }
        }
}
