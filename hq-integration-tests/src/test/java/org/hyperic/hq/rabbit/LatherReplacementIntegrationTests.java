package org.hyperic.hq.rabbit;


import org.hyperic.hq.agent.AgentConfig;
import org.hyperic.hq.agent.AgentConfigException;
import org.hyperic.hq.agent.AgentConnectionException;
import org.hyperic.hq.agent.AgentRemoteException;
import org.hyperic.hq.agent.bizapp.agent.ProviderInfo;
import org.hyperic.hq.agent.bizapp.agent.client.AgentClient;
import org.hyperic.hq.agent.bizapp.client.*;
import org.hyperic.hq.bizapp.server.operations.RegisterAgentService;
import org.hyperic.hq.test.BaseInfrastructureTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@Ignore("'AGENT_HOME' must be set")
public class LatherReplacementIntegrationTests extends BaseInfrastructureTest {

    @Autowired private RegisterAgentService registerAgentService;

    /**
     * Must configure
     */
    private final static String AGENT_HOME = "/agent-4.6.0.BUILD-SNAPSHOT";

    private BizappCallbackClient bizappClient;

    private final String host = "localhost";

    private final String user = "hqadmin";

    private final String pass = "hqadmin";

    private final int port = 7080;

    @Before
    public void prepare() {
        assertNotNull(registerAgentService);
        System.setProperty("agent.install.home", AGENT_HOME);
        System.setProperty("agent.bundle.home", AGENT_HOME + "/bin");
        ProviderInfo providerInfo = new ProviderInfo(AgentCallbackClient.getDefaultProviderURL(host, port, false), "no-auth");
        this.bizappClient = new BizappCallbackClient(new StaticProviderFetcher(providerInfo), AgentConfig.newInstance());
        assertNotNull("'bcc' must not be null", bizappClient);
    }

    @Test
    public void bizappRegisterAgentSuccess() throws AgentCallbackClientException, InterruptedException { 
        RegisterAgentResult response = this.bizappClient.registerAgent(null, user, pass, "fooAuthToken", host, port, "", 1, false, false);
        assertNotNull(response);
        assertTrue(response.response.startsWith("token:"));
    }

    @Test
    public void bizappRegisterAgentFail() throws AgentCallbackClientException, InterruptedException {
        RegisterAgentResult error = this.bizappClient.registerAgent(null, "invalid", pass, "fooAuthToken", host, port, "", 1, false, false);
        assertNotNull(error);
        assertTrue(error.response.contains("Permission denied"));
    }


    @Test
    public void isUserValid() throws AgentConnectionException, AgentRemoteException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(1);
 
        executor.submit(new Callable<String>() {
            public String call() throws Exception {
                AgentClient.main(new String[]{"start"});
                ProviderInfo providerInfo = new ProviderInfo(AgentCallbackClient.getDefaultProviderURL(host, port, false), "no-auth");
                assertNotNull(providerInfo);
                BizappCallbackClient bcc = new BizappCallbackClient(new StaticProviderFetcher(providerInfo), AgentConfig.newInstance());
                assertTrue(bcc.userIsValid("hqadmin", "hqadmin"));
                AgentClient.main(new String[]{"die"});
                TimeUnit.MILLISECONDS.sleep(1000);
                return null;
            }
        });
        TimeUnit.MILLISECONDS.sleep(5000);
        executor.shutdown();
    }




    
    /* not migrated yet */
    private AutoinventoryCallbackClient autoinventoryClient;

    private ControlCallbackClient controlClient;

    private MeasurementCallbackClient measurementClient;

    private PlugininventoryCallbackClient pluginInventoryClient;
    
    @Test
    public void bizappUserIsValid() throws AgentConfigException, AgentCallbackClientException, InterruptedException {
        assertTrue(bizappClient.userIsValid(user, pass));
    }
 
    public void bizappUpdateAgent() throws InterruptedException, AgentCallbackClientException {
        String result = bizappClient.updateAgent("", user, pass, host, port, false, false);
        assertNotNull(result);
    }
}
