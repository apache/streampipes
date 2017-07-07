package de.fzi.cep.sepa.sources.kd2.sources;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.kd2.streams.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 18.11.2016.
 */
public class BiodataSource implements SemanticEventProducerDeclarer {
    @Override
    public SepDescription declareModel() {
        SepDescription sep = new SepDescription("source-biodata", "Biodata", "KD2 Biodata Events");
        return sep;
    }

    @Override
    public List<EventStreamDeclarer> getEventStreams() {
        List<EventStreamDeclarer> eventStreams = new ArrayList<EventStreamDeclarer>();

        eventStreams.add(new BiodataStream());
        eventStreams.add(new EmotionalArousalStream());
        eventStreams.add(new HeartRateStream());
        eventStreams.add(new PulseStream());
        eventStreams.add(new SkinConductanceStream());

        return eventStreams;
    }
}
