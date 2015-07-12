package de.fzi.cep.sepa.rest.api;

import javax.ws.rs.core.Response;

public interface User {

	public String doRegisterUser(String username, String password);
	
	public String doLoginUser(String username, String password);
	
	public String doLogoutUser();
	
	public String getAllSources();
	
	public String getAllStreams();
	
	public String getAllActions();
	
	public String getSelectedSources();
	
	public String getSelectedStreams();
	
	public String getSelectedActions();

	public String removeSource(String uri);

	public String removeAction(String uri);

	public String removeSepa(String uri);

}
