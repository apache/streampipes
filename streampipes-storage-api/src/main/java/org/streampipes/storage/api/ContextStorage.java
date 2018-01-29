package org.streampipes.storage.api;

import java.util.List;

import org.streampipes.model.client.ontology.Context;

public interface ContextStorage {

	List<String> getAvailableContexts();
	
	boolean addContext(Context context);
	
	boolean deleteContext(String contextId);
}
