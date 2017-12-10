package de.fzi.cep.sepa.init;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.streampipes.container.declarer.Declarer;
import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.model.Response;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;

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
            public Response invokeRuntime(DataProcessorInvocation invocationGraph) {
                return null;
            }

            @Override
            public Response detachRuntime(String pipelineId) {
                return null;
            }

            @Override
            public DataProcessorDescription declareModel() {
                return null;
            }
        };
    }
}