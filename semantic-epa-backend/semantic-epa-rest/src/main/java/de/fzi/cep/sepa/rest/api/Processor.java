package de.fzi.cep.sepa.rest.api;

public interface Processor {

	String postProcessor(String uri);
	
	String getProcessor(String sepaId);
	
	String getAllProcessors(String domain);
	
	String deleteProcessor(String sepaId);
}
