package de.fzi.cep.sepa.client.container.rest;

import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.container.init.RunningInstances;
import de.fzi.cep.sepa.desc.declarer.InvocableDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/sec")
public class SecElement extends InvocableElement<SecInvocation, SemanticEventConsumerDeclarer> {

    public SecElement() {
        super(SecInvocation.class);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDescription(@PathParam("id") String elementId) {
        List<SemanticEventConsumerDeclarer> secs = DeclarersSingleton.getInstance().getConsumerDeclarers();
        return getJsonLd(secs, elementId);
    }

    @Override
    protected List<SemanticEventConsumerDeclarer> getDeclarers() {
        return DeclarersSingleton.getInstance().getConsumerDeclarers();
    }

    @GET
    @Path("{elementId}/{runningInstanceId}")
    @Produces(MediaType.TEXT_HTML)
    public String detach(@PathParam("elementId") String elementId, @PathParam("runningInstanceId") String runningInstanceId) {

        InvocableDeclarer runningInstance = RunningInstances.INSTANCE.getInvocation(runningInstanceId);
        NamedSEPAElement description = RunningInstances.INSTANCE.getDescription(runningInstanceId);

        if (runningInstance != null && runningInstance instanceof SemanticEventConsumerDeclarer && description != null
                && description instanceof SecInvocation) {

            SemanticEventConsumerDeclarer instanceDeclarer = (SemanticEventConsumerDeclarer) runningInstance;
            SecInvocation desctionDeclarer = (SecInvocation) description;
            return instanceDeclarer.getHtml(desctionDeclarer);

        } else {
            return "Error in element " + elementId;
        }
    }
}
