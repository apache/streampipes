package de.fzi.cep.sepa.client.container.rest;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.declarer.Declarer;
import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.transform.Transformer;
import org.openrdf.model.Graph;
import org.openrdf.rio.RDFHandlerException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class Element {

    protected <T extends  Declarer> String getJsonLd(List<T> declarers, String id) {
        NamedSEPAElement elem = getById(declarers, id);
        return toJsonLd(elem);
    }

    protected <T extends  Declarer> NamedSEPAElement getById(List<T> declarers, String id) {
        for (Declarer declarer : declarers) {
            if (declarer.declareModel().getUri().equals(id)) {

                //TODO find a better solution to add the event streams to the SepDescription
                if (declarer instanceof SemanticEventProducerDeclarer) {
                    SepDescription desc = ((SemanticEventProducerDeclarer) declarer).declareModel();
                    List<EventStreamDeclarer> eventStreamDeclarers = ((SemanticEventProducerDeclarer) declarer).getEventStreams();
                    for (EventStreamDeclarer esd : eventStreamDeclarers) {
                        desc.addEventStream(esd.declareModel(desc));
                    }
                    return desc;
                }

                return declarer.declareModel();
            }
        }

        return null;
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
