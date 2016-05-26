package de.fzi.cep.sepa.client.container.init;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import org.junit.Test;

import de.fzi.cep.sepa.client.declarer.Declarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public class DeclarersSingletonTest {
    @Test
    public void getInstanceIsSingletonTest() throws Exception {
        DeclarersSingleton ds1 = DeclarersSingleton.getInstance();
        DeclarersSingleton ds2 = DeclarersSingleton.getInstance();

        assertTrue(ds1 == ds2);
    }

    @Test
    public void addDeclarersTest() throws Exception {
        List<Declarer> declarers = new ArrayList<>();
        declarers.add(getSepaDeclarer());

        DeclarersSingleton.getInstance().addDeclarers(declarers);
        assertEquals(DeclarersSingleton.getInstance().getEpaDeclarers().size(), 1);
    }

    private SemanticEventProcessingAgentDeclarer getSepaDeclarer() {
       return new SemanticEventProcessingAgentDeclarer() {
            @Override
            public Response invokeRuntime(SepaInvocation invocationGraph) {
                return null;
            }

            @Override
            public Response detachRuntime(String pipelineId) {
                return null;
            }

            @Override
            public SepaDescription declareModel() {
                return null;
            }
        };
    }
}