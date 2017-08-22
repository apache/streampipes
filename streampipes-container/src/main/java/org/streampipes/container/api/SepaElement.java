package org.streampipes.container.api;

import java.util.List;

import javax.ws.rs.Path;

import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.util.Util;
import org.streampipes.model.impl.graph.SepaInvocation;

@Path("/sepa")
public class SepaElement extends InvocableElement<SepaInvocation, SemanticEventProcessingAgentDeclarer> {

    public SepaElement() {

        super(SepaInvocation.class);
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
