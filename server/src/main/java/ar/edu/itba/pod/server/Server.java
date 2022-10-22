package ar.edu.itba.pod.server;

import org.slf4j.LoggerFactory;
import com.hazelcast.core.Hazelcast;
import java.io.IOException;
import com.hazelcast.config.*;
import org.slf4j.Logger;
import java.util.Collections;
import java.io.InputStream;
import java.util.Properties;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final String propertiesFile = "config.properties";

    public static void main(String[] args) throws IOException {
        logger.info("Server Starting");
        String ip = "192.168.0.*", name = "g9", pass = "g9-pass";
        try {
            Properties props = new Properties();
            InputStream inputStream = Server.class.getClassLoader().getResourceAsStream(propertiesFile);

            if (inputStream == null)
                throw new IllegalArgumentException();
            props.load(inputStream);
            ip = props.getProperty("ip");
            name = props.getProperty("name");
            pass = props.getProperty("pass");

        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
        logger.info("Using ip address: " + ip);

        Config config = new Config();
        GroupConfig groupConfig = new GroupConfig().setName(name).setPassword(pass);
        config.setGroupConfig(groupConfig);

        MulticastConfig multicastConfig = new MulticastConfig();
        JoinConfig joinConfig = new JoinConfig().setMulticastConfig(multicastConfig);

        InterfacesConfig interfacesConfig = new InterfacesConfig()
                .setInterfaces(Collections.singletonList(ip))
                .setEnabled(true);

        NetworkConfig networkConfig = new NetworkConfig()
                .setInterfaces(interfacesConfig)
                .setJoin(joinConfig);

        config.setNetworkConfig(networkConfig);


//        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig()
//                .setUrl("http://localhost:32768/mancenter/")
//                .setEnabled(true);
//        config.setManagementCenterConfig(managementCenterConfig);



        Hazelcast.newHazelcastInstance(config);
    }
}