package ar.edu.itba.pod.client;

import ar.edu.itba.pod.combiners.PedestriansBySensorCombiner;
import ar.edu.itba.pod.mappers.PedestriansBySensorMapper;
import ar.edu.itba.pod.models.Sensor;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.models.Status;
import ar.edu.itba.pod.reducers.PedestriansBySensorReducer;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static ar.edu.itba.pod.client.Utils.*;

public class Query1 {

    private static final Logger logger = LoggerFactory.getLogger(Query1.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        File logFile = new File(parseParameter(args, "-DoutPath")+"/time1.txt");
        logFile.createNewFile();
        FileWriter logWriter = new FileWriter(logFile);

        HazelcastInstance hz = Utils.getHazelClientInstance(args);

        IList<SensorReading> test = hz.getList("test");
        Sensor sensor = new Sensor(Status.A, "Sensor Uno", "a", 1L);
        test.add(new SensorReading(sensor, 12L, "enero", 3, "lunes", 1L));

        Sensor sensor2 = new Sensor(Status.I, "Sensor Dos", "a", 2L);
        test.add(new SensorReading(sensor2, 12L, "enero", 3, "lunes", 2L));

        Sensor sensor3 = new Sensor(Status.R, "Sensor Tres", "a", 3L);
        test.add(new SensorReading(sensor3, 12L, "enero", 3, "lunes", 3L));

        Sensor sensor4 = new Sensor(Status.A, "Sensor Uno", "a", 4L);
        test.add(new SensorReading(sensor4, 12L, "enero", 3, "lunes", 4L));

        Sensor sensor33 = new Sensor(Status.A, "Sensor Tres", "a", 3L);
        test.add(new SensorReading(sensor33, 12L, "enero", 3, "lunes", 3L));

       // final KeyValueSource<String, SensorReading> dataSource = KeyValueSource.fromList(hz.getList("g9_sensors_readings"));
        final KeyValueSource<String, SensorReading> dataSource = KeyValueSource.fromList(test);

        logWithTimeStamp(logWriter, "Inicio del trabajo map/reduce");

        JobTracker jt = hz.getJobTracker("g9_jobs");
        Job<String, SensorReading> job = jt.newJob(dataSource);

        ICompletableFuture<Stream<Map.Entry<String, Long>>> future = job
                .mapper(new PedestriansBySensorMapper())
                .combiner( new PedestriansBySensorCombiner<>() )
                .reducer( new PedestriansBySensorReducer<>() )
                .submit((values) ->
                        StreamSupport.stream(values.spliterator(), false)
                                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                                        .thenComparing(Map.Entry.comparingByKey()))
                );
        Stream<Map.Entry<String, Long>> result = future.get();

//        File csvFile = new File(parseParameter(args, "-DoutPath")+"/query1.csv");
//        csvFile.createNewFile();
//        FileWriter csvWriter = new FileWriter(csvFile);

//        csvWriter.write("NEIGHBOURHOOD;TREES\n");

        result.forEach(e -> {
            System.out.println(e.getKey() + " " + e.getValue());
//            try {
//                csvWriter.write(e.getKey() + ";" + e.getValue() + "\n");
//            } catch (IOException err) {
//                logger.error(err.getMessage());
//                HazelcastClient.shutdownAll();
//            }
        });

        logWithTimeStamp(logWriter, "Fin del trabajo map/reduce");

        logWriter.close();
//        csvWriter.close();
        HazelcastClient.shutdownAll();
    }


}
