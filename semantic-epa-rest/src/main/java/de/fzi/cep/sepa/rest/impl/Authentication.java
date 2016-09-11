package de.fzi.cep.sepa.rest.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.fzi.cep.sepa.rest.annotation.GsonWithIds;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.model.client.messages.ErrorMessage;
import de.fzi.cep.sepa.model.client.messages.NotificationType;
import de.fzi.cep.sepa.model.client.messages.Notifications;
import de.fzi.cep.sepa.model.client.messages.SuccessMessage;
import de.fzi.cep.sepa.model.client.user.RegistrationData;
import de.fzi.cep.sepa.model.client.user.Role;
import de.fzi.cep.sepa.model.client.user.ShiroAuthenticationRequest;
import de.fzi.cep.sepa.model.client.user.ShiroAuthenticationResponse;
import de.fzi.cep.sepa.model.client.user.ShiroAuthenticationResponseFactory;
import de.fzi.cep.sepa.model.client.user.User;
import de.fzi.cep.sepa.rest.api.IAuthentication;
import de.fzi.cep.sepa.storage.controller.StorageManager;

@Path("/v2/admin")
public class Authentication extends AbstractRestInterface implements IAuthentication {

	static Map<String, Session> tokenMap = new HashMap<>();
	
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	@GsonWithIds
    @POST
	@Override
	@Path("/login")
	public Response doLogin(ShiroAuthenticationRequest token) {
    	Subject subject = SecurityUtils.getSubject();
        if (SecurityUtils.getSubject().isAuthenticated()) {
			return ok("Already logged in. Please log out to change user");
		}
        UsernamePasswordToken shiroToken = new UsernamePasswordToken(token.getUsername(), token.getPassword());
        shiroToken.setRememberMe(true);
        try {
            subject.login(shiroToken);
            String secretToken = UUID.randomUUID().toString();
            System.out.println("secret token: " +secretToken);
            tokenMap.put(secretToken, subject.getSession());
            return ok(ShiroAuthenticationResponseFactory.create((User) StorageManager.INSTANCE.getUserStorageAPI().getUser((String) subject.getPrincipal())));
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return ok(new ErrorMessage(NotificationType.LOGIN_FAILED.uiNotification()));
        } 
	}

    @Path("/logout")
    @GET
	@GsonWithIds
	@Override
	public Response doLogout() {
		Subject subject = SecurityUtils.getSubject();
		for(String key : tokenMap.keySet()) {
			if (tokenMap.get(key).equals(subject.getSession())) tokenMap.remove(key);
		}
        subject.logout();
        
        return ok(new SuccessMessage(NotificationType.LOGOUT_SUCCESS.uiNotification()));
	}

    @Path("/register")
    @POST
	@GsonWithIds
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response doRegister(RegistrationData data) {

        Set<Role> roles = new HashSet<Role>();
        roles.add(data.getRole());
        if (StorageManager.INSTANCE.getUserStorageAPI().emailExists(data.getEmail())) {
			return ok(Notifications.error("This email address already exists. Please choose another address."));
		}
        else if (StorageManager.INSTANCE.getUserStorageAPI().usernameExists(data.getUsername())) {
			return ok(Notifications.error("This username address already exists. Please choose another username."));
		}
        else {
	        de.fzi.cep.sepa.model.client.user.User user = new de.fzi.cep.sepa.model.client.user.User(data.getUsername(), data.getEmail(), data.getPassword(), roles);
	        StorageManager.INSTANCE.getUserStorageAPI().storeUser(user);
	        return ok(new SuccessMessage(NotificationType.REGISTRATION_SUCCESS.uiNotification()));
        }
	}
    
    @GET
	@GsonWithIds
    @Path("/authc")
    @Override
    public Response userAuthenticated(@Context HttpServletRequest req) {
    	
    	if (ConfigurationManager.isConfigured())
    	{
    		System.out.println(req.getSession().getId());
	        if (SecurityUtils.getSubject().isAuthenticated()) {
	        	 return ok(ShiroAuthenticationResponseFactory.create((User) StorageManager.INSTANCE.getUserStorageAPI().getUser((String) SecurityUtils.getSubject().getPrincipal())));
	        }
    	}
        return ok(new ErrorMessage(NotificationType.NOT_LOGGED_IN.uiNotification()));
    }
    
    @GET
    @Path("/sso")
    public Response userCredentials(@QueryParam("session") String session) {

    	if (tokenMap.containsKey(session)) {
    		try {
	    		Subject requestSubject = new Subject.Builder().session(tokenMap.get(session)).buildSubject();
	        	ShiroAuthenticationResponse shiroResp = ShiroAuthenticationResponseFactory.create((User) 
	    				StorageManager.INSTANCE.getUserStorageAPI().getUser((String) requestSubject.getPrincipal()));
	        	return corsResponse(shiroResp);
    		} catch (Exception e) {
    			e.printStackTrace();
			}
    	}
		return ok(new ErrorMessage(NotificationType.NOT_LOGGED_IN.uiNotification()));
	}

	private <T> Response corsResponse(T entity) {
		return Response.ok() //200
				.entity(entity)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, HEAD, OPTIONS").build();

	}


}
