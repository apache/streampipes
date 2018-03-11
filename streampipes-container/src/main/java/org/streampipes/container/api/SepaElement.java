package org.streampipes.container.api;

import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.util.Util;
import org.streampipes.model.graph.DataProcessorInvocation;

import java.util.List;

import javax.ws.rs.Path;

@Path("/sepa")
public class SepaElement extends InvocableElement<DataProcessorInvocation, SemanticEventProcessingAgentDeclarer> {

    public SepaElement() {

        super(DataProcessorInvocation.class);
    }

    @Override
    protected List<SemanticEventProcessingAgentDeclarer> getElementDeclarers() {
        return DeclarersSingleton.getInstance().getEpaDeclarers();
    }

    @Override
    protected String getInstanceId(String uri, String elementId) {
        return Util.getInstanceId(uri, "sepa", elementId);
    }
}
