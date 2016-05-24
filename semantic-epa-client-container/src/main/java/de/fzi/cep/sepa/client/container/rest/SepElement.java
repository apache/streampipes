package de.fzi.cep.sepa.client.container.rest;

import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.html.model.SemanticEventProducerDescription;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/sep")
public class SepElement extends Element<SemanticEventProducerDeclarer> {

    @Override
    protected List<SemanticEventProducerDeclarer> getElementDeclarers() {
        return DeclarersSingleton.getInstance().getProducerDeclarers();
    }

//    @GET
//    @Path("{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String getDescription(@PathParam("id") String elementId) {
//        List<SemanticEventProducerDeclarer> seps = DeclarersSingleton.getInstance().getProducerDeclarers();
//        return getJsonLd(seps, elementId);
//    }
}
