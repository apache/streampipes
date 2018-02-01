package org.streampipes.storage.api;

import org.streampipes.model.client.connection.Connection;
import org.streampipes.model.client.pipeline.PipelineElementRecommendation;

import java.util.List;

public interface IPipelineElementConnectionStorage {

	void addConnection(Connection connection);
	
	List<PipelineElementRecommendation> getRecommendedElements(String from);
	
}
