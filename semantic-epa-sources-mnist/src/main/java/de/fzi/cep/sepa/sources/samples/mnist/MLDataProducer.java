package de.fzi.cep.sepa.sources.samples.mnist;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MLDataProducer implements SemanticEventProducerDeclarer {

    private static String dataFolder = System.getProperty("user.home") + File.separator +"Coding" +File.separator +"data" +File.separator +"semmnist" + File.separator;

    @Override
    public SepDescription declareModel() {
        return new SepDescription("source_ml_data", "ML data producer", "Produces data to test " +
                "the machine learning library");
    }

    @Override
    public List<EventStreamDeclarer> getEventStreams() {

        List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
        streams.add(new MnistStream());

        // TODO add here a stream per file folder

        return streams;
    }
}
