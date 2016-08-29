package de.fzi.cep.sepa.rest.api;

public interface IPipelineElement {

	String getAvailable(String username);
	String getFavorites(String username);
	String getOwn(String username);
	
	String addFavorite(String username, String elementUri);
	
	String removeFavorite(String username, String elementUri);
	String removeOwn(String username, String elementUri);
	
	String getAsJsonLd(String elementUri);
	
	String getElement(String username, String elementUri);
	
	
}
