package ar.edu.itba.pod.client;

import ar.edu.itba.pod.models.*;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public final class QueryUtils {

    public static final String SENSORS_FILE_NAME = "sensors.csv";
    public static final String READINGS_FILE_NAME = "readings.csv";
    public static final String MAP_REDUCE_START = "Inicio del trabajo map/reduce";
    public static final String MAP_REDUCE_END = "Fin del trabajo map/reduce";
    public static final String CSV_READ_START = "Inicio de la lectura del archivo";
    public static final String CSV_READ_END = "Fin de la lectura del archivo";
    private QueryUtils() {

    }

    public static String parseParameter(String[] args, String requestedParam) {
        return Stream.of(args).filter(arg -> arg.contains(requestedParam))
                .map(arg -> arg.substring(arg.indexOf("=") + 1))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        "Use: " + requestedParam + "=<value>")
                );
    }

    public static HazelcastInstance getHazelClientInstance(String[] args) {
        String name = "g9", pass = "g9-pass";

        ClientConfig clientConfig = new ClientConfig();

        GroupConfig groupConfig = new GroupConfig().setName(name).setPassword(pass);
        clientConfig.setGroupConfig(groupConfig);

        String[] servers = parseParameter(args, "-Daddresses").split(";");

        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
        clientNetworkConfig.addAddress(servers);
        clientConfig.setNetworkConfig(clientNetworkConfig);

        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    public static void logWithTimeStamp(FileWriter logWriter, String message) throws IOException {
        String timestamp = (new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSSS")).format(new Date());
        logWriter.write(timestamp + " - " + message + "\n");
    }

    public static List<String> prepareCSVLoad(String file, String dir) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(dir + "/" + file), StandardCharsets.ISO_8859_1);
        lines.remove(0);
        return lines;
    }

    public static Map<Long, ActiveSensor> getActiveSensors(String dir) throws IOException {
        List<String> sensorLines = prepareCSVLoad(SENSORS_FILE_NAME, dir);
        Map<Long, ActiveSensor> sensorMap = new HashMap<>();
        for (String line : sensorLines) {
            String[] values = line.split(";");
            if(Status.valueOf(values[4]).equals(Status.A)) {
                ActiveSensor s = new ActiveSensor(values[1], Long.parseLong(values[0]));
                sensorMap.put(s.getSensorId(), s);
            }
        }
        return sensorMap;
    }

    public static <V> Job<String, V> prepareJob(CsvLoader csvLoader, FileWriter fileWriter, String[] args) throws IOException {
        HazelcastInstance hz = QueryUtils.getHazelClientInstance(args);
        logWithTimeStamp(fileWriter, CSV_READ_START );
        csvLoader.loadReadingsFromCsv(args,hz,fileWriter);
        logWithTimeStamp(fileWriter, CSV_READ_END);

        final KeyValueSource<String, V> dataSource = KeyValueSource.fromList(
                hz.getList("g9_sensors_readings"));

        JobTracker jt = hz.getJobTracker("g9_jobs");
        return jt.newJob(dataSource);
    }

    public static FileWriter createFileWriter(String pathname) throws IOException {
        File logFile = new File(pathname);
        logFile.createNewFile();
        return new FileWriter(logFile);
    }
}
