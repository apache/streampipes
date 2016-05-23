package de.fzi.cep.sepa.client.container.rest;

import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/sec")
public class SecElement extends Element {

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDescription(@PathParam("id") String elementId) {
        List<SemanticEventConsumerDeclarer> secs = DeclarersSingleton.getInstance().getConsumerDeclarers();
        return getJsonLd(secs, elementId);
    }
}
