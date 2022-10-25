package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.PedestriansPerSensorCollator;
import ar.edu.itba.pod.combiners.PedestriansBySensorCombiner;
import ar.edu.itba.pod.mappers.PedestriansBySensorMapper;
import ar.edu.itba.pod.models.ActiveSensor;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.reducers.PedestriansBySensorReducer;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.Job;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static ar.edu.itba.pod.client.QueryUtils.*;

public class Query1 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        FileWriter logWriter = QueryUtils.createFileWriter(
                parseParameter(args, "-DoutPath") + "/time1.txt");

        Job<String, SensorReading> job = QueryUtils.prepareJob(new Loader(), logWriter, args);

        logWithTimeStamp(logWriter, MAP_REDUCE_START);
        ICompletableFuture<Stream<Map.Entry<String, Long>>> future = job
                .mapper(new PedestriansBySensorMapper())
                .combiner( new PedestriansBySensorCombiner<>() )
                .reducer( new PedestriansBySensorReducer<>() )
                .submit(new PedestriansPerSensorCollator());

        Stream<Map.Entry<String, Long>> result = future.get();
        logWithTimeStamp(logWriter, MAP_REDUCE_END);

        FileWriter csvWriter = QueryUtils.createFileWriter(
                parseParameter(args, "-DoutPath")+"/query1.csv");

        csvWriter.write("Sensor;Total_Count\n");

        result.forEach(r -> {
            try {
                csvWriter.write(r.getKey() + ";" + r.getValue() + "\n");
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

            for(String line : lines) {
                String[] values = line.split(";");
                if(sensorMap.containsKey(Long.parseLong(values[7]))) {
                    SensorReading sr = new SensorReading.SensorReadingBuilder(Long.parseLong(values[9]))
                            .sensorName(sensorMap.get(Long.parseLong(values[7])).getDescription()).build();
                    readingIList.add(sr);
                }
            }
        }
    }
}
