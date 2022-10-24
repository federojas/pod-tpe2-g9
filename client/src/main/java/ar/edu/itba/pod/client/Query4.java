package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.AverageMeasurePerMonthCollator;
import ar.edu.itba.pod.mappers.AverageMeasurePerMonthMapper;
import ar.edu.itba.pod.models.MonthAverage;
import ar.edu.itba.pod.models.SensorMonthReading;
import ar.edu.itba.pod.reducers.AverageMeasurePerMonthReducer;
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

import static ar.edu.itba.pod.client.QueryUtils.logWithTimeStamp;
import static ar.edu.itba.pod.client.QueryUtils.parseParameter;

//TODO CODIGO REPETIDO EN LAS QUERYS
public class Query4 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        File logFile = new File(parseParameter(args, "-DoutPath")+"/time4.txt");
        logFile.createNewFile();
        FileWriter logWriter = new FileWriter(logFile);

        HazelcastInstance hz = QueryUtils.getHazelClientInstance(args);
        QueryUtils.loadQuery4ReadingsFromCSV(args,hz,logWriter, Long.parseLong(parseParameter(args,"-Dyear")));
        final KeyValueSource<String, SensorMonthReading> dataSource = KeyValueSource.fromList(
                hz.getList("g9_sensors_readings"));

        logWithTimeStamp(logWriter, "Inicio del trabajo map/reduce");

        JobTracker jt = hz.getJobTracker("g9_jobs");
        Job<String, SensorMonthReading> job = jt.newJob(dataSource);

        ICompletableFuture<Stream<Map.Entry<String, MonthAverage>>> future = job
                .mapper(new AverageMeasurePerMonthMapper())
                .reducer( new AverageMeasurePerMonthReducer<>() )
                .submit(new AverageMeasurePerMonthCollator(Long.parseLong(parseParameter(args,"-Dn"))));

        Stream<Map.Entry<String, MonthAverage>> result = future.get();

        File csvFile = new File(parseParameter(args, "-DoutPath")+"/query4.csv");
        csvFile.createNewFile();
        FileWriter csvWriter = new FileWriter(csvFile);

        csvWriter.write("Sensor;Month,Max_Monthly_Avg\n");

        result.forEach(r -> {
            try {
                csvWriter.write(r.getKey() + ";" + r.getValue().getMonth() + ";"
                        + String.format("%.2f", r.getValue().getAverage()) + "\n");
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
