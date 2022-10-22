package ar.edu.itba.pod.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;

import java.util.stream.Stream;

public final class Utils {

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
}
