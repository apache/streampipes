package org.streampipes.container.declarer;

import org.streampipes.model.graph.DataSourceDescription;

import java.util.List;

public interface SemanticEventProducerDeclarer extends Declarer<DataSourceDescription> {
	List<DataStreamDeclarer> getEventStreams();
}
