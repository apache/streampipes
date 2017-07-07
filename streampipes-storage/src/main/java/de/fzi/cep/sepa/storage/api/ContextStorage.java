package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.model.client.ontology.Context;

public interface ContextStorage {

	List<String> getAvailableContexts();
	
	boolean addContext(Context context);
	
	boolean deleteContext(String contextId);
}
