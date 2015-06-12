package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.messages.ElementRecommendation;
import de.fzi.cep.sepa.model.client.connection.Connection;

public interface ConnectionStorage {

	public void addConnection(Connection connection);
	
	public List<ElementRecommendation> getRecommendedElements(String from);
	
}
