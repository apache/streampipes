package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.model.client.ontology.Context;

public interface ContextStorage {

	public List<String> getAvailableContexts();
	
	public boolean addContext(Context context);
	
	public boolean deleteContext(String contextId);
}
