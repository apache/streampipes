package org.streampipes.rest.api;


import javax.ws.rs.core.Response;

public interface IPipelineElement {

	Response getAvailable(String username);
	Response getFavorites(String username);
	Response getOwn(String username);

	Response addFavorite(String username, String elementUri);

	Response removeFavorite(String username, String elementUri);
	Response removeOwn(String username, String elementUri);

	String getAsJsonLd(String elementUri);

	Response getElement(String username, String elementUri);
	
	
}
