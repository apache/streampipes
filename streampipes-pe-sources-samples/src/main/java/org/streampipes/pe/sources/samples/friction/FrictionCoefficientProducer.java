package org.streampipes.pe.sources.samples.friction;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 26.10.2016.
 */
public class FrictionCoefficientProducer implements SemanticEventProducerDeclarer {
    @Override
    public DataSourceDescription declareModel() {
        DataSourceDescription sep = new DataSourceDescription("source_friction", "Friction Coefficient Replay", "");
        return sep;
    }

    @Override
    public List<EventStreamDeclarer> getEventStreams() {
        List<EventStreamDeclarer> eventStreams = new ArrayList<EventStreamDeclarer>();

        eventStreams.add(new FrictionCoefficientGearbox());
        eventStreams.add(new FrictionCoefficientSwivel());
        return eventStreams;
    }
}
