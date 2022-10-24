import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import org.junit.After;
import org.junit.Before;

public class Q1Test {
    private HazelcastInstance client, member;
    private TestHazelcastFactory hzFactory;

    @Before
    public void setUp() {
        String name = "g9", pass = "g9-pass";
        hzFactory = new TestHazelcastFactory();
        client = hzFactory.newHazelcastClient();

        GroupConfig groupConfig = new GroupConfig().setName(name).setPassword(pass);
        Config config = new Config().setGroupConfig(groupConfig);
        member = hzFactory.newHazelcastInstance(config);
        ClientConfig clientConfig = new ClientConfig().setGroupConfig(groupConfig);
        client = hzFactory.newHazelcastClient(clientConfig);
    }

    @After
    public void closeHazelcast() {
        hzFactory.shutdownAll();
    }
}
