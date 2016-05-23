package de.fzi.cep.sepa.client.container.init;

import de.fzi.cep.sepa.desc.declarer.Declarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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