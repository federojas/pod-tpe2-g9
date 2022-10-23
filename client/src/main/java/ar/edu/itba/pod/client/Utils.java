package ar.edu.itba.pod.client;

import ar.edu.itba.pod.models.Sensor;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.models.Status;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public final class Utils {

    public static final String SENSORS_FILE_NAME = "sensors.csv";
    public static final String READINGS_FILE_NAME = "readings.csv";

    private Utils() {

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


    public static List<SensorReading> loadSensorReadingsFromCSV(Map<Long,Sensor> sensorMap, String dir) throws IOException {
        List<SensorReading> readings = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(dir + "/" + READINGS_FILE_NAME), StandardCharsets.ISO_8859_1);
        lines.remove(0);
        for(String line : lines) {
            String[] values = line.split(";");
            if(sensorMap.containsKey(Long.parseLong(values[7]))) {
                SensorReading sr = new SensorReading(sensorMap.get(Long.parseLong(values[7])), Long.parseLong(values[2]),
                        values[3], Integer.parseInt(values[4]), values[5], values[6], Long.parseLong(values[9]));

                readings.add(sr);
            }
        }
        return readings;
    }

    public static void loadSensorsFromCSV(String[] args, HazelcastInstance hz, FileWriter timestampWriter) throws IOException {

        Map<Long, Sensor> sensorMap = new HashMap<>();
        String dir = parseParameter(args, "-DinPath");

        logWithTimeStamp(timestampWriter, "Inicio de la lectura del archivo");
        List<String> lines = Files.readAllLines(
                Paths.get(dir + "/" + SENSORS_FILE_NAME), StandardCharsets.ISO_8859_1);
        lines.remove(0);
        for (String line : lines) {
            String[] values = line.split(";");
            if(Status.valueOf(values[4]).equals(Status.A)) {
                Sensor s = new Sensor(Status.valueOf(values[4]), values[1], Long.parseLong(values[0]));
                sensorMap.put(s.getSensorId(),s);
            }
        }

        List<SensorReading> sensorReadings = loadSensorReadingsFromCSV(sensorMap, dir);
        logWithTimeStamp(timestampWriter, "Fin de la lectura del archivo");
        IList<SensorReading> readingIList = hz.getList("g9_sensors_readings");
        readingIList.clear();
        readingIList.addAll(sensorReadings);
    }
}
