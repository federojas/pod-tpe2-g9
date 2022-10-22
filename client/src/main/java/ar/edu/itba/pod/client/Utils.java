package ar.edu.itba.pod.client;

import ar.edu.itba.pod.models.Sensor;
import ar.edu.itba.pod.models.SensorReading;
import ar.edu.itba.pod.models.Status;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
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

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

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

    public static void logTimestamp(FileWriter fileWriter, String message) throws IOException {
        String timestamp = (new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSSS")).format(new Date());
        fileWriter.write(timestamp + " - " + message + "\n");
    }

    public static Map<Long, SensorReading> loadSensorReadingsFromCSV(String dir, HazelcastInstance hz) throws IOException {
        Map<Long, SensorReading> readings = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(dir + "/" + READINGS_FILE_NAME), StandardCharsets.ISO_8859_1);

        int readingsLoaded = 0;
        for(String line : lines) {
            String[] values = line.split("[;]");

            SensorReading sr = new SensorReading(Long.valueOf(values[2]), values[3], Integer.valueOf(values[4]), values[5], Long.valueOf(values[9]));

            readings.put(Long.parseLong(values[0]), sr);
            readingsLoaded++;
        }

        logger.info("{} readings added", readingsLoaded);

//        TODAVIA NO LO USAMOS
//        IMap<Long, SensorReading> readingsIMap = hz.getMap("readings");
//        readingsIMap.clear();
//        readingsIMap.putAll(readings);

        return readings;
    }

    public static void loadSensorsFromCSV(String[] args, HazelcastInstance hz, FileWriter timestampWriter) throws IOException {
        logTimestamp(timestampWriter, "Inicio de la lectura del archivo");

        String dir = parseParameter(args, "-DinPath");

        // Cargo la lista de SensorReadings con el conteo por cada Sensor_ID

        Map<Long, SensorReading> sensorReadings = loadSensorReadingsFromCSV(dir, hz);

        // Recorremos el CSV y cargamos los arboles a una lista local

        List<String> lines = Files.readAllLines(Paths.get(dir + "/" + SENSORS_FILE_NAME), StandardCharsets.ISO_8859_1);

        int sensorsLoaded = 0;
        for(String line : lines) {
            String[] values = line.split("[;]");

            Sensor s = new Sensor(Status.valueOf(values[4]), values[2], values[1], Long.parseLong(values[0]));

            if (sensorReadings.containsKey(s.getSensorId())) {
                sensorReadings.get(s.getSensorId()).setSensor(s);
            }

            sensorsLoaded++;
        }

        logger.info("{} sensors added", sensorsLoaded);


//        IList<SensorReading> srDist = hz.getList("sensors_readings");
//        srDist.clear();
//        srDist.addAll(readings);

        logTimestamp(timestampWriter, "Fin de la lectura del archivo");
    }
}
