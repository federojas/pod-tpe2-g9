package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.PedestriansPerSensorCollator;
import ar.edu.itba.pod.combiners.PedestriansBySensorCombiner;
import ar.edu.itba.pod.mappers.PedestriansBySensorMapper;
import ar.edu.itba.pod.models.Query1Reading;
import ar.edu.itba.pod.reducers.PedestriansBySensorReducer;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static ar.edu.itba.pod.client.Utils.*;

public class Query1 {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        File logFile = new File(parseParameter(args, "-DoutPath")+"/time1.txt");
        logFile.createNewFile();
        FileWriter logWriter = new FileWriter(logFile);

        HazelcastInstance hz = Utils.getHazelClientInstance(args);
        Utils.loadQuery1ReadingsFromCSV(args,hz,logWriter);
        final KeyValueSource<String, Query1Reading> dataSource = KeyValueSource.fromList(
                hz.getList("g9_sensors_readings"));


        logWithTimeStamp(logWriter, "Inicio del trabajo map/reduce");

        JobTracker jt = hz.getJobTracker("g9_jobs");
        Job<String, Query1Reading> job = jt.newJob(dataSource);

        ICompletableFuture<Stream<Map.Entry<String, Long>>> future = job
                .mapper(new PedestriansBySensorMapper())
                .combiner( new PedestriansBySensorCombiner<>() )
                .reducer( new PedestriansBySensorReducer<>() )
                .submit(new PedestriansPerSensorCollator());

        Stream<Map.Entry<String, Long>> result = future.get();

        File csvFile = new File(parseParameter(args, "-DoutPath")+"/query1.csv");
        csvFile.createNewFile();
        FileWriter csvWriter = new FileWriter(csvFile);

        csvWriter.write("Sensor;Total_Count\n");

        result.forEach(r -> {
            try {
                csvWriter.write(r.getKey() + ";" + r.getValue() + "\n");
            } catch (IOException exception) {
                HazelcastClient.shutdownAll();
            }
        });

        logWithTimeStamp(logWriter, "Fin del trabajo map/reduce");

        logWriter.close();
        csvWriter.close();
        HazelcastClient.shutdownAll();
    }


}
