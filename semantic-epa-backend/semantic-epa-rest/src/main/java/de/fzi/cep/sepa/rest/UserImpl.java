package de.fzi.cep.sepa.rest;

import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.*;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

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
    @GET
    public String doLoginUser() {
        Subject subject = SecurityUtils.getSubject();
        return subject.toString() + "is a subject";
    }

    @Override
    public String doLogoutUser() {
        return null;
    }

    @Override
    public String getAllSources() {
        return null;
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
}
