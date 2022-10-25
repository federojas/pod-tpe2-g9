package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.AverageMeasurePerMonthCollator;
import ar.edu.itba.pod.mappers.AverageMeasurePerMonthMapper;
import ar.edu.itba.pod.models.ActiveSensor;
import ar.edu.itba.pod.models.MonthAverage;
import ar.edu.itba.pod.models.SensorMonthReading;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.reducers.AverageMeasurePerMonthReducer;
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

public class Query4 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        FileWriter logWriter = createFileWriter(parseParameter(args, "-DoutPath")+"/time4.txt");

        Job<String, SensorMonthReading> job = prepareJob(new Loader(), logWriter, args);

        logWithTimeStamp(logWriter, "Inicio del trabajo map/reduce");
        ICompletableFuture<Stream<Map.Entry<String, MonthAverage>>> future = job
                .mapper(new AverageMeasurePerMonthMapper())
                .reducer( new AverageMeasurePerMonthReducer<>() )
                .submit(new AverageMeasurePerMonthCollator(Long.parseLong(parseParameter(args,"-Dn"))));
        Stream<Map.Entry<String, MonthAverage>> result = future.get();
        logWithTimeStamp(logWriter, "Fin del trabajo map/reduce");

        FileWriter csvWriter = createFileWriter(parseParameter(args, "-DoutPath")+"/query4.csv");

        csvWriter.write("Sensor;Month,Max_Monthly_Avg\n");
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
            List<String> sensorLines = prepareCSVLoad(SENSORS_FILE_NAME, dir);

            Map<Long, ActiveSensor> sensorMap = getActiveSensors(sensorLines);
            List<String> lines = prepareCSVLoad(READINGS_FILE_NAME, dir);

            IList<SensorMonthReading> readingIList = hz.getList("g9_sensors_readings");
            readingIList.clear();

            for(String line : lines) {
                String[] values = line.split(";");
                if(sensorMap.containsKey(Long.parseLong(values[7]))
                        && Long.parseLong(values[2]) == Long.parseLong(parseParameter(args, "-Dyear"))) {
                    SensorMonthReading sr = new SensorMonthReading(Long.parseLong(values[9]),
                            values[3],
                            sensorMap.get(Long.parseLong(values[7])).getDescription()
                    );
                    readingIList.add(sr);
                }
            }
            logWithTimeStamp(timestampWriter, "Fin de la lectura del archivo");
        }
    }
}
