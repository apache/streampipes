package de.fzi.cep.sepa.rest;

import com.google.gson.JsonArray;

import de.fzi.cep.sepa.messages.ErrorMessage;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.SuccessMessage;
import de.fzi.cep.sepa.model.client.user.Element;
import de.fzi.cep.sepa.model.client.user.Role;
import de.fzi.cep.sepa.rest.api.*;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.impl.UserStorage;

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

import org.lightcouch.NoDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User REST-Interface.
 *
 * Created by robin on 04.05.15.
 */
@Path("/user")
public class UserImpl extends AbstractRestInterface implements User{


    Logger LOG = LoggerFactory.getLogger(UserImpl.class);

    CouchDbClient dbClient = de.fzi.cep.sepa.storage.util.Utils.getCouchDbUserClient();


    @Override
    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Store user in database.
     */
    public String doRegisterUser(@FormParam("username") String username, @FormParam("password") String password) {

        if (StorageManager.INSTANCE.getUserStorageAPI().checkUser(username)) {
            return toJson(new ErrorMessage(NotificationType.REGISTRATION_FAILED.uiNotification()));
        }

        Set<Role> roles = new HashSet<Role>();
  
        de.fzi.cep.sepa.model.client.user.User user = new de.fzi.cep.sepa.model.client.user.User(username, "", password, roles);
        userStorage.storeUser(user);
        return  toJson(new SuccessMessage(NotificationType.REGISTRATION_SUCCESS.uiNotification()));
    }

    @Override
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public String doLoginUser(@FormParam("username") String username, @FormParam("password") String password) {

        Subject subject = SecurityUtils.getSubject();
        if (SecurityUtils.getSubject().isAuthenticated()) return "Already logged in. Please log out to change user";

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

    /*public static void main(String[] args) {
        //String streams = new UserImpl().getAllStreams();
        //System.out.println(streams);
        String username = "user";
        CouchDbClient dbClient = de.fzi.cep.sepa.storage.util.Utils.getCouchDbUserClient();
        JsonArray pipelineIds = dbClient.view("users/pipelines").key(username).query(JsonObject.class).get(0).get("value").getAsJsonArray();
        System.out.println((pipelineIds));
    }*/


    @Override
    @GET
    @Path("/streams")
    /**
     * Returns IDs from all Pipelines the user added
     */
    public String getAllStreams() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            String username = SecurityUtils.getSubject().getPrincipal().toString();
            JsonArray pipelineIds = dbClient.view("users/pipelines").key(username).query(JsonObject.class).get(0).get("value").getAsJsonArray();
            return toJson(pipelineIds);
        }
        return null;
    }

    @Override
    public String getAllActions() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            String username = SecurityUtils.getSubject().getPrincipal().toString();
        }
        return null;
    }

    @Override
    public String getSelectedSources() {
        Source source = new SourceImpl();
        return source.getAllUserSources();

    }

    @Override
    public String getSelectedStreams() {
        Processor processor = new ProcessorImpl();
        return processor.getAllUserProcessors();
    }

    @Override
    public String getSelectedActions() {
        Action action = new ActionImpl();
        return action.getAllUserActions();
    }


    public String removeAction(String id) {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            String username = SecurityUtils.getSubject().getPrincipal().toString();
            de.fzi.cep.sepa.model.client.user.User user = StorageManager.INSTANCE.getUserStorageAPI().getUser(username);
            user.removeAction(id);
            StorageManager.INSTANCE.getUserStorageAPI().updateUser(user);
            return toJson(new SuccessMessage(NotificationType.REMOVED_ACTION.uiNotification()));
        }
        return toJson(new ErrorMessage(NotificationType.NOT_REMOVED.uiNotification()));

    }

    public String removeSepa(String id) {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            String username = SecurityUtils.getSubject().getPrincipal().toString();
            de.fzi.cep.sepa.model.client.user.User user = StorageManager.INSTANCE.getUserStorageAPI().getUser(username);
            user.removeSepa(id);
            StorageManager.INSTANCE.getUserStorageAPI().updateUser(user);
            return toJson(new SuccessMessage(NotificationType.REMOVED_SEPA.uiNotification()));
        }
        return toJson(new ErrorMessage(NotificationType.NOT_REMOVED.uiNotification()));
    }

    public String removeSource(String id) {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            String username = SecurityUtils.getSubject().getPrincipal().toString();
            de.fzi.cep.sepa.model.client.user.User user = StorageManager.INSTANCE.getUserStorageAPI().getUser(username);
            user.removeSource(id);
            StorageManager.INSTANCE.getUserStorageAPI().updateUser(user);
            return toJson(new SuccessMessage(NotificationType.REMOVED_SOURCE.uiNotification()));
        }
        return toJson(new ErrorMessage(NotificationType.NOT_REMOVED.uiNotification()));
    }

    @GET
    @Path("/authc")
    @Produces(MediaType.APPLICATION_JSON)
    public String userAuthenticated() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
        	Notification notification = new Notification(SecurityUtils.getSubject().getPrincipal().toString(), "");
        	return toJson(new SuccessMessage(notification));
        }
        return toJson(new ErrorMessage(NotificationType.NOT_LOGGED_IN.uiNotification()));
    }


}
