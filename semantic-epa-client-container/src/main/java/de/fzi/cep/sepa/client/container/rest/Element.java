package de.fzi.cep.sepa.client.container.rest;

import com.clarkparsia.empire.SupportsRdfId;
import com.clarkparsia.empire.annotation.InvalidRdfException;
import de.fzi.cep.sepa.client.container.init.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.declarer.*;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.transform.Transformer;
import org.openrdf.model.Graph;
import org.openrdf.rio.RDFHandlerException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;

public abstract class Element<D extends Declarer> {
    public Element() {
    }

    protected abstract List<D> getElementDeclarers();

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDescription(@PathParam("id") String elementId) {
        List<D> declarers = getElementDeclarers();
        return getJsonLd(elementId);
    }

    protected String getJsonLd(String id) {
        NamedSEPAElement elem = getById(id);
        return toJsonLd(elem);
    }

    protected D getDeclarerById(String id) {
        for (D declarer : getElementDeclarers()) {
            if (declarer.declareModel().getUri().equals(id)) {
                return declarer;
            }
        }
        return null;
    }

    protected NamedSEPAElement getById(String id) {
        NamedSEPAElement desc = null;
        for (Declarer declarer : getElementDeclarers()) {
            if (declarer.declareModel().getUri().equals(id)) {
                //TODO find a better solution to add the event streams to the SepDescription
                if (declarer instanceof SemanticEventProducerDeclarer) {
                    SepDescription secDesc = ((SemanticEventProducerDeclarer) declarer).declareModel();
                    List<EventStreamDeclarer> eventStreamDeclarers = ((SemanticEventProducerDeclarer) declarer).getEventStreams();
                    for (EventStreamDeclarer esd : eventStreamDeclarers) {
                        secDesc.addEventStream(esd.declareModel(secDesc));
                    }

                    desc = secDesc;
                } else {
                    desc = declarer.declareModel();
                }
            }
        }

        //TODO remove this and find a better solution
        if (desc != null) {
            String type = "";

            if (desc instanceof SepaDescription) {
                type = "sepa/";
            } else if (desc instanceof SepDescription) {
                type = "sep/";
            } else if (desc instanceof SecDescription) {
                type = "sec/";
            }

            String uri = EmbeddedModelSubmitter.getBaseUri()+ "sepa/" + desc.getUri();
            desc.setUri(uri);
            desc.setRdfId(new SupportsRdfId.URIKey(URI.create(uri)));
        }

        return desc;
    }

    protected String toJsonLd(NamedSEPAElement namedElement) {
        if (namedElement != null) {
            Graph rdfGraph;
            try {
                rdfGraph = Transformer.toJsonLd(namedElement);
                return Utils.asString(rdfGraph);
            } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | InvalidRdfException | RDFHandlerException e) {
                e.printStackTrace();
            }
        }
        return "{}";
    }
}
