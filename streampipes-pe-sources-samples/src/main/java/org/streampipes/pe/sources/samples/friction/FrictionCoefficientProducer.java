package org.streampipes.pe.sources.samples.friction;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;

import java.util.ArrayList;
import java.util.List;

public class FrictionCoefficientProducer implements SemanticEventProducerDeclarer {
    @Override
    public DataSourceDescription declareModel() {
        DataSourceDescription sep = new DataSourceDescription("source_friction", "Friction Coefficient Replay", "");
        return sep;
    }

    @Override
    public List<DataStreamDeclarer> getEventStreams() {
        List<DataStreamDeclarer> eventStreams = new ArrayList<DataStreamDeclarer>();

        eventStreams.add(new FrictionCoefficientGearbox());
        eventStreams.add(new FrictionCoefficientSwivel());
        return eventStreams;
    }
}
