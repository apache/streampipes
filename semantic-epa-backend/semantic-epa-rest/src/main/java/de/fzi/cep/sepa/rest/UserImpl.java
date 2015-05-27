package de.fzi.cep.sepa.rest;

import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.User;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
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
import java.util.List;

/**
 * Created by robin on 04.05.15.
 */
@Path("/user")
public class UserImpl extends AbstractRestInterface implements User{


    Logger LOG = LoggerFactory.getLogger(UserImpl.class);

    StorageRequests requestor = StorageManager.INSTANCE.getStorageAPI();
    String username;


    List<String> sources;
    List<String> actions;
    List<String> streams;



    @Override
    @POST
    @Path("/register")
    /**
     * Store user in database.
     */
    public String doRegisterUser(@FormParam("username") String username, @FormParam("password") String password) {
        CouchDbClient dbClient = new CouchDbClient();

        if (dbClient.view("users/password").key(username).includeDocs(true).query(JsonObject.class).size() != 0) {
            return "Username already exists. Sorry choose another one";
        }

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
        dbClient.save(user);
        dbClient.shutdown();

        return  "Registered user";
    }

    @Override
    @Path("/login")
    @POST
    public String doLoginUser(@FormParam("username") String username, @FormParam("password") String password) {
        LOG.info(SecurityUtils.getSecurityManager().toString());

        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) return "Already logged in. Please log out to change user";

        CouchDbClient dbClient = new CouchDbClient();
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

        //dbClient.save(user);
        dbClient.shutdown();

        this.username = username;

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(true);
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return "Could not login. Wrong password or username";
        }
        return "Successfully logged in" + username;
    }

    @Override
    @GET
    @Path("/logout")
    public String doLogoutUser() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "Logged out";
    }

    @Override
    @Path("/sources")
    @GET
    public String getAllSources() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            CouchDbClient dbClient = new CouchDbClient();

            return "Secret sources";
        }
        return "Leider keinen Zugriff";
    }

    @Path("/remember")
    @GET
    public String isRemembered() {

        if (SecurityUtils.getSubject().isRemembered()) {
            return "You are remembered";
        } else return "You are a stranger";
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
    public String isAuthenticated() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            return SecurityUtils.getSubject().getPrincipal().toString();
        }
        return "false";
        //return Boolean.toString(SecurityUtils.getSubject().isAuthenticated());
    }


}
