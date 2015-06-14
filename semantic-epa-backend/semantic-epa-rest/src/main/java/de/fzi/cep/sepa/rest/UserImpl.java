package de.fzi.cep.sepa.rest;

import de.fzi.cep.sepa.messages.ErrorMessage;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.SuccessMessage;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.User;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.*;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.lightcouch.CouchDbClient;

import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.List;

/**
 * Created by robin on 04.05.15.
 */
@Path("/user")
public class UserImpl extends AbstractRestInterface implements User{


    Logger LOG = LoggerFactory.getLogger(UserImpl.class);

    StorageRequests requestor = StorageManager.INSTANCE.getStorageAPI();
    CouchDbClient dbClient = new CouchDbClient("couchdb-users.properties");
    String username;


    List<String> sources;
    List<String> actions;
    List<String> streams;


    @Override
    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Store user in database.
     */
    public String doRegisterUser(@FormParam("username") String username, @FormParam("password") String password) {

        if (dbClient.view("users/password").key(username).includeDocs(true).query(JsonObject.class).size() != 0) {
            return "Username already exists. Sorry choose another one";
        }

        JsonObject user =  new JsonObject();
        user.addProperty("username", username);
        user.addProperty("password", password);
        //TODO change to real role management
        user.addProperty("roles", "user");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            byte[] input = digest.digest(password.getBytes("UTF-8"));
            user.addProperty("hashedPassword", new String(input));
        } catch (Exception e) {
            e.printStackTrace();
            return toJson(new ErrorMessage(NotificationType.REGISTRATION_FAILED.uiNotification()));
        }
        dbClient.save(user);

        return  toJson(new SuccessMessage(NotificationType.REGISTRATION_SUCCESS.uiNotification()));
    }

    @Override
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public String doLoginUser(@FormParam("username") String username, @FormParam("password") String password) {
        LOG.info(SecurityUtils.getSecurityManager().toString());

        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) return "Already logged in. Please log out to change user";

        JsonObject user =  new JsonObject();
        user.addProperty("username", username);
        user.addProperty("password", password);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            byte[] input = digest.digest(password.getBytes("UTF-8"));
            user.addProperty("hashedPassword", new String(input));
        } catch (Exception e) {
            e.printStackTrace();
        }


        this.username = username;

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(true);
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return toJson(new ErrorMessage(NotificationType.LOGIN_FAILED.uiNotification()));
        }
        return toJson(new SuccessMessage(NotificationType.LOGIN_SUCCESS.uiNotification()));
    }

    @Override
    @GET
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public String doLogoutUser() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return toJson(new SuccessMessage(NotificationType.LOGOUT_SUCCESS.uiNotification()));
    }

    @Override
    @Path("/sources")
    @GET
    public String getAllSources() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            return "Secret sources";
        }
        return "Leider keinen Zugriff";
    }

    @Path("/remember")
    @GET
    public String isRemembered() {

        if (SecurityUtils.getSubject().isRemembered()) {
            return toJson(new SuccessMessage(NotificationType.REMEMBERED.uiNotification()));
        }
        return toJson(new ErrorMessage(NotificationType.NOT_REMEMBERED.uiNotification()));
    }

    @Override
    public String getAllStreams() {
        return null;
    }

    @Override
    public String getAllActions() {
        return null;
    }

    @Override
    public String getSelectedSources() {
        return null;
    }

    @Override
    public String getSelectedStreams() {
        return null;
    }

    @Override
    public String getSelectedActions() {
        return null;
    }

    @GET
    @Path("/authc")
    @Produces(MediaType.APPLICATION_JSON)
    public String isAuthenticated() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
        	Notification notification = new Notification(SecurityUtils.getSubject().getPrincipal().toString(), "");
        	return toJson(new SuccessMessage(notification));
        }
        return toJson(new ErrorMessage(NotificationType.NOT_LOGGED_IN.uiNotification()));
    }


}
