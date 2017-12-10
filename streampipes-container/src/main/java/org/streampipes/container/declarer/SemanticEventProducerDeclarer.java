package org.streampipes.container.declarer;

import java.util.List;

import org.streampipes.model.graph.DataSourceDescription;

public interface SemanticEventProducerDeclarer extends Declarer<DataSourceDescription> {
	List<EventStreamDeclarer> getEventStreams();
}
