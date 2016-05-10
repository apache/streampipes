package de.fzi.cep.sepa.rest.api;

public interface Stream {
	
	String getAllStreams();
	
	String getStreamById(String id);
	
	String postSource(String uri);
}
