package org.streampipes.container.api;

import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.Declarer;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.transform.Transformer;
import org.streampipes.empire.core.empire.SupportsRdfId;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.impl.graph.SepaDescription;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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

            String uri = DeclarersSingleton.getInstance().getBaseUri()+ type + desc.getUri();
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
