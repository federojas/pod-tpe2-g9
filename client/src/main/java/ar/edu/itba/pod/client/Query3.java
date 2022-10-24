package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.MaxMeasurePerSensorCollator;
import ar.edu.itba.pod.mappers.MaxMeasurePerSensorMapper;
import ar.edu.itba.pod.models.DateTimeReading;
import ar.edu.itba.pod.models.FeasibleMaxMeasure;
import ar.edu.itba.pod.reducers.MaxMeasurePerSensorReducer;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static ar.edu.itba.pod.client.QueryUtils.logWithTimeStamp;
import static ar.edu.itba.pod.client.QueryUtils.parseParameter;

public class Query3 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        File logFile = new File(parseParameter(args, "-DoutPath")+"/time3.txt");
        logFile.createNewFile();
        FileWriter logWriter = new FileWriter(logFile);

        HazelcastInstance hz = QueryUtils.getHazelClientInstance(args);
        QueryUtils.loadQuery3ReadingsFromCSV(args,hz,logWriter, parseParameter(args,"-Dmin"));
        final KeyValueSource<String, DateTimeReading> dataSource = KeyValueSource.fromList(
                hz.getList("g9_sensors_readings"));


        logWithTimeStamp(logWriter, "Inicio del trabajo map/reduce");

        JobTracker jt = hz.getJobTracker("g9_jobs");
        Job<String, DateTimeReading> job = jt.newJob(dataSource);

        ICompletableFuture<Stream<Map.Entry<String, FeasibleMaxMeasure>>> future = job
                .mapper(new MaxMeasurePerSensorMapper())
                .reducer( new MaxMeasurePerSensorReducer<>() )
                .submit(new MaxMeasurePerSensorCollator());

        Stream<Map.Entry<String, FeasibleMaxMeasure>> result = future.get();

        File csvFile = new File(parseParameter(args, "-DoutPath")+"/query3.csv");
        csvFile.createNewFile();
        FileWriter csvWriter = new FileWriter(csvFile);

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

        logWithTimeStamp(logWriter, "Fin del trabajo map/reduce");

        logWriter.close();
        csvWriter.close();
        HazelcastClient.shutdownAll();
    }
}
