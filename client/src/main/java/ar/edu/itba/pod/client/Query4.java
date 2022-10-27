package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.AverageMeasurePerMonthCollator;
import ar.edu.itba.pod.mappers.AverageMeasurePerMonthMapper;
import ar.edu.itba.pod.models.ActiveSensor;
import ar.edu.itba.pod.models.MonthAverage;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.reducers.AverageMeasurePerMonthReducer;
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

public class Query4 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        FileWriter logWriter = createFileWriter(parseParameter(args, "-DoutPath")+"/time4.txt");

        Job<String, SensorReading> job = prepareJob(new Loader(), logWriter, args);

        logWithTimeStamp(logWriter, MAP_REDUCE_START);
        ICompletableFuture<Stream<Map.Entry<String, MonthAverage>>> future = job
                .mapper(new AverageMeasurePerMonthMapper())
                .reducer( new AverageMeasurePerMonthReducer<>() )
                .submit(new AverageMeasurePerMonthCollator(Long.parseLong(parseParameter(args,"-Dn"))));
        Stream<Map.Entry<String, MonthAverage>> result = future.get();
        logWithTimeStamp(logWriter, MAP_REDUCE_END);

        FileWriter csvWriter = createFileWriter(parseParameter(args, "-DoutPath")+"/query4.csv");

        csvWriter.write("Sensor;Month;Max_Monthly_Avg\n");
        result.forEach(r -> {
            try {
                csvWriter.write(r.getKey() + ";" + r.getValue().getMonth() + ";"
                        + String.format("%.2f", r.getValue().getAverage()) + "\n");
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
            Map<Long, ActiveSensor> sensorMap = getActiveSensors(dir);
            List<String> lines = prepareCSVLoad(READINGS_FILE_NAME, dir);
            IList<SensorReading> readingIList = hz.getList("g9_sensors_readings");
            readingIList.clear();

            List<SensorReading> chunk = new ArrayList<>(CHUNK_SIZE);
            for(String line : lines) {
                String[] values = line.split(";");
                if(sensorMap.containsKey(Long.parseLong(values[7]))
                        && Long.parseLong(values[2]) == Long.parseLong(parseParameter(args, "-Dyear"))) {
                    SensorReading sr = new SensorReading.SensorReadingBuilder(Long.parseLong(values[9]))
                            .month(values[3])
                            .sensorName(sensorMap.get(Long.parseLong(values[7])).getDescription())
                            .build();
                    chunk.add(sr);
                    if(chunk.size() == CHUNK_SIZE) {
                        readingIList.addAll(chunk);
                        chunk.clear();
                    }
                }
            }
            if(!chunk.isEmpty())
                readingIList.addAll(chunk);
        }
    }
}
