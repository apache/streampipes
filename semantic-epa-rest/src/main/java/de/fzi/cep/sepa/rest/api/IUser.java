package de.fzi.cep.sepa.rest.api;


public interface IUser {

	String doRegisterUser(String username, String password);
	
	String doLoginUser(String username, String password);
	
	String doLogoutUser();
	
	String getAllSources();
	
	String getAllStreams();
	
	String getAllActions();
	
	String getSelectedSources();
	
	String getSelectedStreams();
	
	String getSelectedActions();

	String removeSource(String uri);

	String removeAction(String uri);

	String removeSepa(String uri);

}
