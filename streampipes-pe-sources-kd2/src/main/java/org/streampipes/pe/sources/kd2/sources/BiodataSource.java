package org.streampipes.pe.sources.kd2.sources;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.kd2.streams.BiodataStream;
import org.streampipes.pe.sources.kd2.streams.EmotionalArousalStream;
import org.streampipes.pe.sources.kd2.streams.HeartRateStream;
import org.streampipes.pe.sources.kd2.streams.PulseStream;
import org.streampipes.pe.sources.kd2.streams.SkinConductanceStream;

import java.util.ArrayList;
import java.util.List;

public class BiodataSource implements SemanticEventProducerDeclarer {
    @Override
    public DataSourceDescription declareModel() {
        DataSourceDescription sep = new DataSourceDescription("source-biodata", "Biodata", "KD2 Biodata Events");
        return sep;
    }

    @Override
    public List<DataStreamDeclarer> getEventStreams() {
        List<DataStreamDeclarer> eventStreams = new ArrayList<DataStreamDeclarer>();

        eventStreams.add(new BiodataStream());
        eventStreams.add(new EmotionalArousalStream());
        eventStreams.add(new HeartRateStream());
        eventStreams.add(new PulseStream());
        eventStreams.add(new SkinConductanceStream());

        return eventStreams;
    }
}
