import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class Q1Test {
    private HazelcastInstance client, member;
    private TestHazelcastFactory hzFactory;

    @Before
    public void setUp() {
        String ip = "192.168.0.*", name = "g9", pass = "g9-pass";
        hzFactory = new TestHazelcastFactory();

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

        member = hzFactory.newHazelcastInstance(config);
        
        ClientConfig clientConfig = new ClientConfig().setGroupConfig(groupConfig);
        client = hzFactory.newHazelcastClient(clientConfig);
    }

    @Test
    public void test() {
        // TODO
    }

    @After
    public void closeHazelcast() {
        hzFactory.shutdownAll();
    }
}
