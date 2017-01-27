package de.fzi.cep.sepa.sources.samples.mnist;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

import java.util.ArrayList;
import java.util.List;

public class MLDataProducer implements SemanticEventProducerDeclarer {

    @Override
    public SepDescription declareModel() {
        return new SepDescription("source_ml_data", "ML data producer", "Produces data to test " +
                "the machine learning library");
    }

    @Override
    public List<EventStreamDeclarer> getEventStreams() {

        List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
        streams.add(new MnistStream());
        return streams;
    }
}
