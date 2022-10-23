package ar.edu.itba.pod.client;

import com.hazelcast.core.HazelcastInstance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static ar.edu.itba.pod.client.Utils.parseParameter;

public class Query3 {
    public static void main(String[] args) throws IOException {
        File logFile = new File(parseParameter(args, "-DoutPath")+"/time3.txt");
        logFile.createNewFile();
        FileWriter logWriter = new FileWriter(logFile);

        HazelcastInstance hz = Utils.getHazelClientInstance(args);
    }
}
