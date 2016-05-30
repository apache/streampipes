package de.fzi.cep.sepa.implementations.stream.story.sepas;

import de.fzi.cep.sepa.client.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public class FrictionCoefficientController implements SemanticEventProcessingAgentDeclarer {
    @Override
    public SepaDescription declareModel() {
        return null;
    }

    @Override
    public Response invokeRuntime(SepaInvocation invocationGraph) {
        return null;
    }

    @Override
    public Response detachRuntime(String pipelineId) {
        return null;
    }


}
