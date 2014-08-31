package de.fzi.cep.sepa.rest.api;


public interface Action {

	String getAction();
	
	String postAction(String uri);
	
	String deleteAction(String actionId);
	
}
