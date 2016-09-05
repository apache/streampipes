package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.messages.ElementRecommendation;
import de.fzi.cep.sepa.model.client.connection.Connection;

public interface ConnectionStorage {

	void addConnection(Connection connection);
	
	List<ElementRecommendation> getRecommendedElements(String from);
	
}
