package org.streampipes.container.declarer;

import java.util.List;

import org.streampipes.model.impl.graph.SepDescription;

public interface SemanticEventProducerDeclarer extends Declarer<SepDescription> {
	List<EventStreamDeclarer> getEventStreams();
}
