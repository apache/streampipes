package de.fzi.cep.sepa.rest.impl;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.model.client.messages.ErrorMessage;
import de.fzi.cep.sepa.model.client.messages.NotificationType;
import de.fzi.cep.sepa.model.client.messages.Notifications;
import de.fzi.cep.sepa.model.client.messages.SuccessMessage;
import de.fzi.cep.sepa.model.client.user.*;
import de.fzi.cep.sepa.rest.annotation.GsonWithIds;
import de.fzi.cep.sepa.rest.api.IAuthentication;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

@Path("/v2/admin")
public class Authentication extends AbstractRestInterface implements IAuthentication {

    static Map<String, Session> tokenMap = new HashMap<>();

    private static final String StreamStoryComponentId = "streamstory";
    private static final String PanddaComponentId = "pandda";

    private static final String StreamStoryCallbackUrl = "/login/token";
    private static final String PanddaCallbackUrl = "/default/user/login";

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @POST
    @Override
    @Path("/login")
    public Response doLogin(ShiroAuthenticationRequest token) {
        try {
            String secretToken = UUID.randomUUID().toString();
            ShiroAuthenticationResponse authResponse = login(token, secretToken);
            sendToken(secretToken, StreamStoryComponentId, null);
            sendToken(secretToken, PanddaComponentId, null);
            return ok(authResponse);
        } catch (AuthenticationException e) {
            return ok(new ErrorMessage(NotificationType.LOGIN_FAILED.uiNotification()));
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @POST
    @Override
    @Path("/login/{componentId}")
    public Response doLoginFromSso(ShiroAuthenticationRequest token, @PathParam("componentId") String componentId, @QueryParam("session") String sessionId) {
        System.out.println("Login SSO");
        try {
            String secretToken = UUID.randomUUID().toString();
            ShiroAuthenticationResponse authResponse = login(token, secretToken);
            authResponse.setCallbackUrl(Configuration.getInstance().STREAMSTORY_URL);
            sendToken(secretToken, componentId, sessionId);

            return ok(authResponse);
        } catch (AuthenticationException e) {
            return ok(new ErrorMessage(NotificationType.LOGIN_FAILED.uiNotification()));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            doLogout();
            return ok(new ErrorMessage(NotificationType.LOGIN_FAILED.uiNotification()));
        }
    }

    private void sendToken(String secretToken, String componentId, String sessionId) {

        JsonObject jsonObject = new JsonObject();
        if (sessionId != null) {
            jsonObject.add("session", new JsonPrimitive(sessionId));
        }
        jsonObject.add("token", new JsonPrimitive(secretToken));
        try {
            String endpointUrl = makeEndpointUrl(componentId);
            String message = new Gson().toJson(jsonObject);
            org.apache.http.client.fluent.Response response = Request
                    .Post(endpointUrl)
                    .addHeader("Content-type", MediaType.APPLICATION_JSON)
                    .body(new StringEntity(message, Charsets.UTF_8))
                    .execute();

            int statusCode = response.returnResponse().getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode >= 300) {
                System.out.println("Wrong status code");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String makeEndpointUrl(String componentId) {
        if (componentId.equals(StreamStoryComponentId)) {
            return fixStreamStoryUrl(Configuration.getInstance().STREAMSTORY_URL) + StreamStoryCallbackUrl;
        } else if (componentId.equals(PanddaComponentId)) {
            return Configuration.getInstance().PANDDA_URL + PanddaCallbackUrl;
        } else {
            return null;
        }
    }


    private String fixStreamStoryUrl(String url) {
        return url.replaceAll("/dashboard.html", "");
    }

    @Path("/logout")
    @GET
    @GsonWithIds
    @Override
    public Response doLogout() {
        Subject subject = SecurityUtils.getSubject();
        for (String key : tokenMap.keySet()) {
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
        } else if (StorageManager.INSTANCE.getUserStorageAPI().usernameExists(data.getUsername())) {
            return ok(Notifications.error("This username address already exists. Please choose another username."));
        } else {
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

        if (ConfigurationManager.isConfigured()) {
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

    private ShiroAuthenticationResponse login(ShiroAuthenticationRequest token, String secretToken) {
        Subject subject = SecurityUtils.getSubject();
        //if (SecurityUtils.getSubject().isAuthenticated()) {
        //	return ok("Already logged in. Please log out to change user");
        //}
        UsernamePasswordToken shiroToken = new UsernamePasswordToken(token.getUsername(), token.getPassword());
        shiroToken.setRememberMe(true);

        subject.login(shiroToken);
        tokenMap.put(secretToken, subject.getSession());
        return ShiroAuthenticationResponseFactory.create((User) StorageManager
                .INSTANCE.getUserStorageAPI().getUser((String) subject.getPrincipal()));

    }
}
