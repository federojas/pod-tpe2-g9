package ar.edu.itba.pod.client;

import com.hazelcast.core.HazelcastInstance;

import java.io.FileWriter;
import java.io.IOException;

public interface CsvLoader {
    void loadReadingsFromCsv(String[] args, HazelcastInstance hz, FileWriter timestampWriter) throws IOException;
}
