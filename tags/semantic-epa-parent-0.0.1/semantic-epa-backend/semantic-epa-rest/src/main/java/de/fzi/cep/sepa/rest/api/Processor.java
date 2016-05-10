package de.fzi.cep.sepa.rest.api;

public interface Processor {

	String postProcessor(String uri);
	
	String getProcessor(String sepaId);
	
	String checkBinding(String subPipeline);
	
	String getAllProcessors(String domain);

	String getAllUserProcessors();

	String deleteProcessor(String sepaId);
}
