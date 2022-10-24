package ar.edu.itba.pod.client;

import ar.edu.itba.pod.collators.PedestriansPerYearCollator;
import ar.edu.itba.pod.mappers.PedestriansPerYearMapper;
import ar.edu.itba.pod.models.Query2Reading;
import ar.edu.itba.pod.models.YearCount;
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

public class Query2 {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        File logFile = new File(parseParameter(args, "-DoutPath")+"/time2.txt");
        logFile.createNewFile();
        FileWriter logWriter = new FileWriter(logFile);

        HazelcastInstance hz = Utils.getHazelClientInstance(args);
        Utils.loadQuery2ReadingsFromCSV(args,hz,logWriter);
        final KeyValueSource<String, Query2Reading> dataSource = KeyValueSource.fromList(
                hz.getList("g9_sensors_readings"));


        logWithTimeStamp(logWriter, "Inicio del trabajo map/reduce");

        JobTracker jt = hz.getJobTracker("g9_jobs");
        Job<String, Query2Reading> job = jt.newJob(dataSource);

        ICompletableFuture<Stream<Map.Entry<Long, YearCount>>> future = job
                .mapper(new PedestriansPerYearMapper())
                .reducer( new PedestriansPerYearReducer<>() )
                .submit(new PedestriansPerYearCollator());
        Stream<Map.Entry<Long, YearCount>> result = future.get();

        File csvFile = new File(parseParameter(args, "-DoutPath")+"/query2.csv");
        csvFile.createNewFile();
        FileWriter csvWriter = new FileWriter(csvFile);

        csvWriter.write("Year;Weekdays_Count;Weekends_Count;Total_Count\n");

        result.forEach(r -> {
            try {
               csvWriter.write(r.getKey() + ";" + r.getValue().getReadingsInWorkweeks() + ";"
                       + r.getValue().getReadingsInWeekends() + ";" + r.getValue().getReadingsTotal() + "\n");
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
