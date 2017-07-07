package de.fzi.cep.sepa.rest.api;

import de.fzi.cep.sepa.model.client.user.RegistrationData;
import de.fzi.cep.sepa.model.client.user.ShiroAuthenticationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

public interface IAuthentication {

	Response doLogin(ShiroAuthenticationRequest token);

	Response doLoginFromSso(ShiroAuthenticationRequest token, String componentId, String sessionId);
	
	Response doLogout();
	
	Response doRegister(RegistrationData registrationData);

	Response userAuthenticated(HttpServletRequest req);
	
}
