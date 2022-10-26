package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.PedestriansPerYearCollator;
import ar.edu.itba.pod.mappers.PedestriansPerYearMapper;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.models.YearCount;
import ar.edu.itba.pod.reducers.PedestriansPerYearReducer;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.Job;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        Job<String, SensorReading> job = QueryUtils.prepareJob(new Loader(), logWriter, args);

        logWithTimeStamp(logWriter, MAP_REDUCE_START);
        ICompletableFuture<Stream<Map.Entry<Long, YearCount>>> future = job
                .mapper(new PedestriansPerYearMapper())
                .reducer( new PedestriansPerYearReducer<>() )
                .submit(new PedestriansPerYearCollator());
        Stream<Map.Entry<Long, YearCount>> result = future.get();
        logWithTimeStamp(logWriter, MAP_REDUCE_END);

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
            IList<SensorReading> readingIList = hz.getList("g9_sensors_readings");
            readingIList.clear();

            List<SensorReading> chunk = new ArrayList<>(CHUNK_SIZE);
            for(String line : lines) {
                String[] values = line.split(";");
                SensorReading sr = new SensorReading.SensorReadingBuilder(Long.parseLong(values[9]))
                        .year(Long.parseLong(values[2])).weekDay(values[5]).build();
                chunk.add(sr);
                if(chunk.size() == CHUNK_SIZE) {
                    readingIList.addAll(chunk);
                    chunk.clear();
                }
            }
            if(!chunk.isEmpty())
                readingIList.addAll(chunk);
        }
    }
}
