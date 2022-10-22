package ar.edu.itba.pod.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static ar.edu.itba.pod.client.Utils.*;

public class Query1 {

    private static final Logger logger = LoggerFactory.getLogger(Query1.class);

    public static void main(String[] args) throws IOException {

        File logFile = new File(parseParameter(args, "-DoutPath")+"/time1.txt");
        logFile.createNewFile();
        FileWriter logWriter = new FileWriter(logFile);
    }


}
