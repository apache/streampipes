package org.streampipes.storage.api;

import java.util.List;

import org.streampipes.model.client.pipeline.PipelineElementRecommendation;
import org.streampipes.model.client.connection.Connection;

public interface ConnectionStorage {

	void addConnection(Connection connection);
	
	List<PipelineElementRecommendation> getRecommendedElements(String from);
	
}
