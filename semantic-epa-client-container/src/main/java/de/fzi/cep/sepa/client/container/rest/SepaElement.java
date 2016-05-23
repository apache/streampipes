package de.fzi.cep.sepa.client.container.rest;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.declarer.Declarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.transform.Transformer;
import org.openrdf.model.Graph;
import org.openrdf.rio.RDFHandlerException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
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

}
