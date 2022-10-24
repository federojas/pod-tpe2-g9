package ar.edu.itba.pod.client;

import ar.edu.itba.pod.models.*;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;


import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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


    private static void loadQuery1SensorReadingsFromCSV(Map<Long,Sensor> sensorMap, String dir, IList<Query1Reading> readingIList) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(dir + "/" + READINGS_FILE_NAME), StandardCharsets.ISO_8859_1);
        lines.remove(0);
        for(String line : lines) {
            String[] values = line.split(";");
            if(sensorMap.containsKey(Long.parseLong(values[7]))) {
                Query1Reading sr = new Query1Reading(sensorMap.get(Long.parseLong(values[7])).getDescription(), Long.parseLong(values[9]));
                readingIList.add(sr);
            }
        }
    }

    public static void loadQuery1SensorsFromCSV(String[] args, HazelcastInstance hz, FileWriter timestampWriter) throws IOException {
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
                sensorMap.put(s.getSensorId(), s);
            }
        }

        IList<Query1Reading> readingIList = hz.getList("g9_sensors_readings");
        readingIList.clear();

        loadQuery1SensorReadingsFromCSV(sensorMap, dir, readingIList);
        logWithTimeStamp(timestampWriter, "Fin de la lectura del archivo");
    }



    public static void loadQuery2ReadingsFromCSV(String[] args, HazelcastInstance hz, FileWriter timestampWriter) throws IOException {
        String dir = parseParameter(args, "-DinPath");

        logWithTimeStamp(timestampWriter, "Inicio de la lectura del archivo");
        List<String> lines = Files.readAllLines(
                Paths.get(dir + "/" + READINGS_FILE_NAME), StandardCharsets.ISO_8859_1);

        IList<Query2Reading> readingIList = hz.getList("g9_sensors_readings");
        readingIList.clear();

        lines.remove(0);
        for(String line : lines) {
            String[] values = line.split(";");
                Query2Reading sr = new Query2Reading(Long.parseLong(values[2]), values[5], Long.parseLong(values[9]));
                readingIList.add(sr);
        }

        logWithTimeStamp(timestampWriter, "Fin de la lectura del archivo");
    }

    public static void loadQuery3ReadingsFromCSV(String[] args, HazelcastInstance hz, FileWriter timestampWriter, String min) throws IOException {
        String dir = parseParameter(args, "-DinPath");
        Map<Long, Sensor> sensorMap = new HashMap<>();
        logWithTimeStamp(timestampWriter, "Inicio de la lectura del archivo");

        List<String> sensorLines = Files.readAllLines(
                Paths.get(dir + "/" + SENSORS_FILE_NAME), StandardCharsets.ISO_8859_1);
        sensorLines.remove(0);
        for (String line : sensorLines) {
            String[] values = line.split(";");
            if(Status.valueOf(values[4]).equals(Status.A)) {
                Sensor s = new Sensor(Status.valueOf(values[4]), values[1], Long.parseLong(values[0]));
                sensorMap.put(s.getSensorId(), s);
            }
        }

        List<String> lines = Files.readAllLines(
                Paths.get(dir + "/" + READINGS_FILE_NAME), StandardCharsets.ISO_8859_1);

        IList<Query3Reading> readingIList = hz.getList("g9_sensors_readings");
        readingIList.clear();

        lines.remove(0);
        for(String line : lines) {
            String[] values = line.split(";");
            if(sensorMap.containsKey(Long.parseLong(values[7])) && Long.parseLong(values[9]) > Long.parseLong(min)) {
                Query3Reading sr = new Query3Reading(sensorMap.get(Long.parseLong(values[7])).getDescription()
                        , Long.parseLong(values[9]), values[1] );
                readingIList.add(sr);
            }
        }
        logWithTimeStamp(timestampWriter, "Fin de la lectura del archivo");
    }


}
