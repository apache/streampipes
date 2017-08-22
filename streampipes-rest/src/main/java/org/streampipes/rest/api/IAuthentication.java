package org.streampipes.rest.api;

import org.streampipes.model.client.user.RegistrationData;
import org.streampipes.model.client.user.ShiroAuthenticationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

public interface IAuthentication {

	Response doLogin(ShiroAuthenticationRequest token);

	Response doLogout();
	
	Response doRegister(RegistrationData registrationData);

	Response userAuthenticated(HttpServletRequest req);
	
}
