package ar.edu.itba.pod.client;

import ar.edu.itba.pod.mappers.MaxMeasurePerSensorMapper;
import ar.edu.itba.pod.mappers.PedestriansPerYearMapper;
import ar.edu.itba.pod.models.Query2Reading;
import ar.edu.itba.pod.models.Query3Reading;
import ar.edu.itba.pod.models.SensorMeasure;
import ar.edu.itba.pod.models.YearCount;
import ar.edu.itba.pod.reducers.MaxMeasurePerSensorReducer;
import ar.edu.itba.pod.reducers.PedestriansPerYearReducer;
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
import java.util.stream.StreamSupport;

import static ar.edu.itba.pod.client.Utils.logWithTimeStamp;
import static ar.edu.itba.pod.client.Utils.parseParameter;

public class Query3 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        File logFile = new File(parseParameter(args, "-DoutPath")+"/time3.txt");
        logFile.createNewFile();
        FileWriter logWriter = new FileWriter(logFile);

        HazelcastInstance hz = Utils.getHazelClientInstance(args);
        Utils.loadQuery3ReadingsFromCSV(args,hz,logWriter, parseParameter(args,"-Dmin"));
        final KeyValueSource<String, Query3Reading> dataSource = KeyValueSource.fromList(
                hz.getList("g9_sensors_readings"));


        logWithTimeStamp(logWriter, "Inicio del trabajo map/reduce");

        JobTracker jt = hz.getJobTracker("g9_jobs");
        Job<String, Query3Reading> job = jt.newJob(dataSource);

        ICompletableFuture<Stream<Map.Entry<String, SensorMeasure>>> future = job
                .mapper(new MaxMeasurePerSensorMapper())
                .reducer( new MaxMeasurePerSensorReducer<>() )
                .submit((values) ->
                        StreamSupport.stream(values.spliterator(), false)
                                .sorted(Map.Entry.<String, SensorMeasure>comparingByKey().reversed()));
        Stream<Map.Entry<String, SensorMeasure>> result = future.get();

        File csvFile = new File(parseParameter(args, "-DoutPath")+"/query3.csv");
        csvFile.createNewFile();
        FileWriter csvWriter = new FileWriter(csvFile);

        csvWriter.write("Sensor;Max_Reading_Count;Max_Reading_DateTime\n");

        result.forEach(r -> {
            try {
                csvWriter.write(r.getKey() + ";" + r.getValue().getCount() + ";"
                        + r.getValue().getDateTime() + "\n");
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
