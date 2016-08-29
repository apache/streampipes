package de.fzi.cep.sepa.rest.api;

public interface IOntologyPipelineElement {

	String getStreams();
	
	String getSepas();
	
	String getActions();
	
	String getStream(String streamId, boolean keepIds);
	
	String getSepa(String sepaId, boolean keepIds);
	
	String getAction(String actionId, boolean keepIds);
	
}
