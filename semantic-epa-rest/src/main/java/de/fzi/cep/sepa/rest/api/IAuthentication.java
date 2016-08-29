package de.fzi.cep.sepa.rest.api;

import javax.servlet.http.HttpServletRequest;

public interface IAuthentication {

	String doLogin(String token);
	
	String doLogout();
	
	String doRegister(String registrationData);
	
	String userAuthenticated(HttpServletRequest req);
	
}
