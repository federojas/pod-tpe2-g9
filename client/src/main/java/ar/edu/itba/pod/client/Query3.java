package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.MaxMeasurePerSensorCollator;
import ar.edu.itba.pod.mappers.MaxMeasurePerSensorMapper;
import ar.edu.itba.pod.models.ActiveSensor;
import ar.edu.itba.pod.models.DateTimeReading;
import ar.edu.itba.pod.models.FeasibleMaxMeasure;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.reducers.MaxMeasurePerSensorReducer;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static ar.edu.itba.pod.client.QueryUtils.*;

public class Query3 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        FileWriter logWriter = QueryUtils.createFileWriter(
                parseParameter(args, "-DoutPath")+"/time3.txt");
        Job<String, DateTimeReading> job = prepareJob(new Loader(),logWriter,args);

        logWithTimeStamp(logWriter, "Inicio del trabajo map/reduce");
        ICompletableFuture<Stream<Map.Entry<String, FeasibleMaxMeasure>>> future = job
                .mapper(new MaxMeasurePerSensorMapper())
                .reducer( new MaxMeasurePerSensorReducer<>() )
                .submit(new MaxMeasurePerSensorCollator());
        Stream<Map.Entry<String, FeasibleMaxMeasure>> result = future.get();
        logWithTimeStamp(logWriter, "Fin del trabajo map/reduce");

        FileWriter csvWriter = createFileWriter(
                parseParameter(args, "-DoutPath")+"/query3.csv");

        csvWriter.write("Sensor;Max_Reading_Count;Max_Reading_DateTime\n");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:00");

        result.forEach(r -> {
            try {
                csvWriter.write(r.getKey() + ";" + r.getValue().getCount() + ";"
                        + format.format(r.getValue().getDate().getTime()) + "\n");
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
            List<String> sensorLines = prepareCSVLoad(SENSORS_FILE_NAME, dir);

            Map<Long, ActiveSensor> sensorMap = getActiveSensors(sensorLines);

            List<String> lines = prepareCSVLoad(READINGS_FILE_NAME, dir);

            IList<DateTimeReading> readingIList = hz.getList("g9_sensors_readings");
            readingIList.clear();

            for(String line : lines) {
                String[] values = line.split(";");
                if(sensorMap.containsKey(Long.parseLong(values[7]))
                        && Long.parseLong(values[9]) > Long.parseLong(parseParameter(args, "-Dmin"))) {
                    DateTimeReading sr = new DateTimeReading(Long.parseLong(values[9]),
                            Long.parseLong(values[2]),
                            values[3],
                            Integer.parseInt(values[4]),
                            Integer.parseInt(values[6]),
                            sensorMap.get(Long.parseLong(values[7])).getDescription()
                    );
                    readingIList.add(sr);
                }
            }
        }
    }
}
