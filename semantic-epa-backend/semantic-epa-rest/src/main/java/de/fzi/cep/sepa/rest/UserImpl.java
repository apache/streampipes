package de.fzi.cep.sepa.rest;

import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.*;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;

import javax.ws.rs.*;

/**
 * Created by robin on 04.05.15.
 */
@Path("/user")
public class UserImpl extends AbstractRestInterface implements User{
    @Override
    public String doRegisterUser() {
        return null;
    }


    @Override
    @Path("/login")
    @POST
    public String doLoginUser(@FormParam("username") String username, @FormParam("password") String password) {
        Subject subject = SecurityUtils.getSubject();
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
    @POST
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
            return "Secret sources";
        }
        return "Leider keinen Zugriff";
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
        return Boolean.toString(SecurityUtils.getSubject().isAuthenticated());
    }


}
