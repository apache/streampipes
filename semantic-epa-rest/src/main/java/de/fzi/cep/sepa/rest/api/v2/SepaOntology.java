package de.fzi.cep.sepa.rest.api.v2;

public interface SepaOntology {

	public String getStreams();
	
	public String getSepas();
	
	public String getActions();
	
	public String getStream(String streamId, boolean keepIds);
	
	public String getSepa(String sepaId, boolean keepIds);
	
	public String getAction(String actionId, boolean keepIds);
	
}
