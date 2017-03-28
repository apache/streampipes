package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.model.client.pipeline.PipelineElementRecommendation;
import de.fzi.cep.sepa.model.client.connection.Connection;

public interface ConnectionStorage {

	void addConnection(Connection connection);
	
	List<PipelineElementRecommendation> getRecommendedElements(String from);
	
}
