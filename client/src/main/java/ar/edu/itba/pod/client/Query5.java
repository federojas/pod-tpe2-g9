package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.PedestriansToMillionGroupCollator;
import ar.edu.itba.pod.collators.SensorsPerMillionGroupCollator;
import ar.edu.itba.pod.combiners.PedestriansBySensorCombiner;
import ar.edu.itba.pod.combiners.SensorsPerMillionGroupCombiner;
import ar.edu.itba.pod.mappers.PedestriansBySensorMapper;
import ar.edu.itba.pod.mappers.SensorsPerMillionGroupMapper;
import ar.edu.itba.pod.models.PairedSensors;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.reducers.PedestriansBySensorReducer;
import ar.edu.itba.pod.reducers.SensorsPerMillionGroupReducer;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static ar.edu.itba.pod.client.QueryUtils.*;

public class Query5 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        FileWriter logWriter = createFileWriter(
                parseParameter(args, "-DoutPath") + "/time5.txt");
        Job<String, SensorReading> job = QueryUtils.prepareJob(new Query1.Loader(),logWriter,args);

        logWithTimeStamp(logWriter, MAP_REDUCE_START);
        ICompletableFuture<Stream<Map.Entry<String, Long>>> future = job
                .mapper(new PedestriansBySensorMapper())
                .combiner(new PedestriansBySensorCombiner<>())
                .reducer(new PedestriansBySensorReducer<>())
                .submit(new PedestriansToMillionGroupCollator());

        Stream<Map.Entry<String, Long>> result = future.get();

        HazelcastInstance hz = QueryUtils.getHazelClientInstance(args);
        final IMap<String, Long> pedestriansPerSensorMap = hz.getMap("g9_pedestriansPerSensor");
        pedestriansPerSensorMap.clear();
        result.forEach(r -> {
            String key = r.getKey();
            Long value = r.getValue();
            pedestriansPerSensorMap.put(key, value);
        });

        KeyValueSource<String, Long> dataSource2 = KeyValueSource.fromMap(pedestriansPerSensorMap);

        JobTracker jt = hz.getJobTracker("g9_jobs");
        Job<String, Long> job2 = jt.newJob(dataSource2);

        ICompletableFuture<Map<Long, Set<PairedSensors>>> future2 = job2
                .mapper(new SensorsPerMillionGroupMapper())
                .combiner( new SensorsPerMillionGroupCombiner<>() )
                .reducer( new SensorsPerMillionGroupReducer<>() )
                .submit(new SensorsPerMillionGroupCollator());

        Map<Long, Set<PairedSensors>> result2 = future2.get();
        logWithTimeStamp(logWriter, MAP_REDUCE_END);

        File csvFile = new File(parseParameter(args, "-DoutPath")+"/query5.csv");
        csvFile.createNewFile();
        FileWriter csvWriter = new FileWriter(csvFile);

        csvWriter.write("Group;Sensor A;Sensor B\n");
        result2.keySet().forEach(group -> {
            for (PairedSensors pair : result2.get(group)) {
                try {
                    DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
                    String formattedGroup = decimalFormat.format(group);
                    formattedGroup = formattedGroup.replace(',','.');
                    csvWriter.write(formattedGroup + ";" + pair.getSensorA() + ";"
                            + pair.getSensorB() + "\n");
                } catch (IOException exception) {
                    HazelcastClient.shutdownAll();
                }
            }
        });

        logWriter.close();
        csvWriter.close();
        HazelcastClient.shutdownAll();
    }
}
