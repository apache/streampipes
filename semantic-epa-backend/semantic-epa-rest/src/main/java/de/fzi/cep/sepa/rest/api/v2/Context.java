package de.fzi.cep.sepa.rest.api.v2;

import java.io.InputStream;


public interface Context {

	public String getAvailableContexts();
	
	public String addContext(InputStream inputFile, String json);
	
	public String deleteContext(String contextId);
}
