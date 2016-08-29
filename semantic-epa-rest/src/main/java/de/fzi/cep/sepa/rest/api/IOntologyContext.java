package de.fzi.cep.sepa.rest.api;

import java.io.InputStream;


public interface IOntologyContext {

	String getAvailableContexts();
	
	String addContext(InputStream inputFile, String json);
	
	String deleteContext(String contextId);
}
