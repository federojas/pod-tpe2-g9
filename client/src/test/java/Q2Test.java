import ar.edu.itba.pod.collators.PedestriansPerYearCollator;
import ar.edu.itba.pod.mappers.PedestriansPerYearMapper;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.models.YearCount;
import ar.edu.itba.pod.reducers.PedestriansPerYearReducer;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;

//TOTAL DE PEATONES POR MOMENTO DE LA SEMANA C/ANIO
// Year;Weekdays_Count;Weekends_Count;Total_Count
public class Q2Test extends QueryTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        List<SensorReading> dayReadings = SensorFactory.getDayReadingList();
        String queryName = "g9-Q2";
        String queryJob = "g9-Q2-Job";

        List<YearCount> expected = getExpectedResult(dayReadings);

        IList<SensorReading> readingIList = client.getList(queryName);
        readingIList.addAll(dayReadings);

        final KeyValueSource<String, SensorReading> source =
                KeyValueSource.fromList(client.getList(queryName));

        JobTracker jt = client.getJobTracker(queryJob);
        Job<String, SensorReading> job = jt.newJob(source);

        ICompletableFuture<Stream<Map.Entry<Long, YearCount>>> future = job
                .mapper(new PedestriansPerYearMapper())
                .reducer( new PedestriansPerYearReducer<>() )
                .submit(new PedestriansPerYearCollator());

        Stream<Map.Entry<Long, YearCount>> result = future.get();
        List<Map.Entry<Long, YearCount>> l = result.collect(Collectors.toList());
        int i = 0;
        Long year = 2021L;
        for(Map.Entry<Long, YearCount> e : l){
            assertEquals(year, e.getKey());
            assertEquals(expected.get(i).getReadingsTotal(), e.getValue().getReadingsTotal());
            assertEquals(expected.get(i).getReadingsInWorkweeks(), e.getValue().getReadingsInWorkweeks());
            assertEquals(expected.get(i).getReadingsInWeekends(), e.getValue().getReadingsInWeekends());
            i++; year--;
        }
    }

    private List<YearCount> getExpectedResult(List<SensorReading> dayReadings) {
         List <YearCount> toReturn = new ArrayList<>();
        for(int i = 0 ; i < 3 ; i++) {
            Long year = 2021L - i;
            YearCount yearCount = new YearCount();
            yearCount.setReadingsInWeekends(dayReadings.stream().filter(x -> Objects.equals(x
                    .getYear(), year) && (Objects.equals(x.getWeekDay(), "Saturday") || Objects.equals(x.getWeekDay(), "Sunday"))).mapToLong(SensorReading::getReadings).sum());
            yearCount.setReadingsInWorkweeks(dayReadings.stream().filter(x -> Objects.equals(x.
                    getYear(), year) && !(Objects.equals(x.getWeekDay(), "Saturday") || Objects.equals(x.getWeekDay(), "Sunday"))).mapToLong(SensorReading::getReadings).sum());
            yearCount.setReadingsTotal(yearCount.getReadingsInWeekends() + yearCount.getReadingsInWorkweeks());
            toReturn.add(yearCount);
        }
        return toReturn;
    }
}
