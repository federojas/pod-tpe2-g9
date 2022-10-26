package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.MaxMeasurePerSensorCollator;
import ar.edu.itba.pod.mappers.MaxMeasurePerSensorMapper;
import ar.edu.itba.pod.models.ActiveSensor;
import ar.edu.itba.pod.models.FeasibleMaxMeasure;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.reducers.MaxMeasurePerSensorReducer;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.Job;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static ar.edu.itba.pod.client.QueryUtils.*;

public class Query3 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        FileWriter logWriter = QueryUtils.createFileWriter(
                parseParameter(args, "-DoutPath")+"/time3.txt");
        Job<String, SensorReading> job = prepareJob(new Loader(),logWriter,args);

        logWithTimeStamp(logWriter, MAP_REDUCE_START);
        ICompletableFuture<Stream<Map.Entry<String, FeasibleMaxMeasure>>> future = job
                .mapper(new MaxMeasurePerSensorMapper())
                .reducer( new MaxMeasurePerSensorReducer<>() )
                .submit(new MaxMeasurePerSensorCollator());
        Stream<Map.Entry<String, FeasibleMaxMeasure>> result = future.get();
        logWithTimeStamp(logWriter, MAP_REDUCE_END);

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
            Map<Long, ActiveSensor> sensorMap = getActiveSensors(dir);
            List<String> lines = prepareCSVLoad(READINGS_FILE_NAME, dir);
            IList<SensorReading> readingIList = hz.getList("g9_sensors_readings");
            readingIList.clear();

            List<SensorReading> chunk = new ArrayList<>(CHUNK_SIZE);
            for(String line : lines) {
                String[] values = line.split(";");
                if(sensorMap.containsKey(Long.parseLong(values[7]))
                        && Long.parseLong(values[9]) > Long.parseLong(parseParameter(args, "-Dmin"))) {
                    SensorReading sr = new SensorReading.SensorReadingBuilder(Long.parseLong(values[9]))
                            .year(Long.parseLong(values[2])).month(values[3]).day( Integer.parseInt(values[4]))
                            .time( Integer.parseInt(values[6]))
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
