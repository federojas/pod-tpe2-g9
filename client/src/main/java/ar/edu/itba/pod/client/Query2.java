package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.PedestriansPerYearCollator;
import ar.edu.itba.pod.mappers.PedestriansPerYearMapper;
import ar.edu.itba.pod.models.ActiveSensor;
import ar.edu.itba.pod.models.DayReading;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.models.YearCount;
import ar.edu.itba.pod.reducers.PedestriansPerYearReducer;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static ar.edu.itba.pod.client.QueryUtils.*;
import static ar.edu.itba.pod.client.QueryUtils.READINGS_FILE_NAME;

public class Query2 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        FileWriter logWriter = QueryUtils.createFileWriter(
                parseParameter(args, "-DoutPath")+"/time2.txt");
        Job<String, DayReading> job = QueryUtils.prepareJob(new Loader(), logWriter, args);

        logWithTimeStamp(logWriter, "Inicio del trabajo map/reduce");
        ICompletableFuture<Stream<Map.Entry<Long, YearCount>>> future = job
                .mapper(new PedestriansPerYearMapper())
                .reducer( new PedestriansPerYearReducer<>() )
                .submit(new PedestriansPerYearCollator());
        Stream<Map.Entry<Long, YearCount>> result = future.get();
        logWithTimeStamp(logWriter, "Fin del trabajo map/reduce");

        FileWriter csvWriter = QueryUtils.createFileWriter(
                parseParameter(args, "-DoutPath")+"/query2.csv");

        csvWriter.write("Year;Weekdays_Count;Weekends_Count;Total_Count\n");
        result.forEach(r -> {
            try {
               csvWriter.write(r.getKey() + ";" + r.getValue().getReadingsInWorkweeks() + ";"
                       + r.getValue().getReadingsInWeekends() + ";" + r.getValue().getReadingsTotal() + "\n");
            } catch (IOException exception) {
                HazelcastClient.shutdownAll();
            }
        });

        logWriter.close();
        csvWriter.close();
        HazelcastClient.shutdownAll();
    }

    public static class Loader implements CsvLoader {
        @Override
        public void loadReadingsFromCsv(String[] args, HazelcastInstance hz, FileWriter timestampWriter) throws IOException {
            String dir = parseParameter(args, "-DinPath");
            List<String> lines = prepareCSVLoad(READINGS_FILE_NAME, dir);

            IList<DayReading> readingIList = hz.getList("g9_sensors_readings");
            readingIList.clear();

            for(String line : lines) {
                String[] values = line.split(";");
                DayReading sr = new DayReading(Long.parseLong(values[2]), values[5], Long.parseLong(values[9]));
                readingIList.add(sr);
            }
        }
    }
}
