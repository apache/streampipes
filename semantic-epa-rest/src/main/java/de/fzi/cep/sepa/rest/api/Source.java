package de.fzi.cep.sepa.rest.api;

public interface Source {

	String postSource(String uri);
	
	String getAllSources(String domain);

	String getAllUserSources();
	
	String getStreamsBySource(String sourceId);
	
	String getSource(String sepId);
	
	String deleteSource(String sepId);
}
