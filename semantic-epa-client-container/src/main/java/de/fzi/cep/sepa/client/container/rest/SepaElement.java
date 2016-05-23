package de.fzi.cep.sepa.client.container.rest;

import com.google.gson.Gson;
import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.transform.Transformer;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/sepa")
public class SepaElement extends Element {

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDescription(@PathParam("id") String elementId) {
        List<SemanticEventProcessingAgentDeclarer> sepas = DeclarersSingleton.getInstance().getEpaDeclarers();
        return getJsonLd(sepas, elementId);
    }

    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String invokeRuntime(@PathParam("id") String elementId, String payload) {

        try {
            SepaInvocation graph = Transformer.fromJsonLd(SepaInvocation.class, payload);
            List<SemanticEventProcessingAgentDeclarer> sepas = DeclarersSingleton.getInstance().getEpaDeclarers();
            SemanticEventProcessingAgentDeclarer sepa = (SemanticEventProcessingAgentDeclarer) getDeclarerById(sepas, elementId);

            if (sepa != null) {
//                String instanceId = graph.getElementId();

                Gson gson = new Gson();
                return gson.toJson(sepa.invokeRuntime(graph));

            }
        } catch (RDFParseException | IOException | RepositoryException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        return gson.toJson(new Response("", false, "Could not find the element with id: "));
    }

}
