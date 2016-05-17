package de.fzi.cep.sepa.rest.api.v2;

public interface SepaElementOperation {

	public String getAvailable(String username);
	public String getFavorites(String username);
	public String getOwn(String username);
	
	public String addFavorite(String username, String elementUri);
	
	public String removeFavorite(String username, String elementUri);
	public String removeOwn(String username, String elementUri);
	
	public String getAsJsonLd(String elementUri);
	
	public String getElement(String username, String elementUri);
	
	
}
