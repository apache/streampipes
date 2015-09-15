package de.fzi.cep.sepa.rest;


import com.google.gson.JsonObject;
import de.fzi.cep.sepa.model.client.user.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.SimpleByteSource;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.List;


/**
 * Principals are a subjects identifying attributes  such as first name, last name or user name.
 * Credentials are usually secret values known only by the subject
 *
 * In order to check username and password with CouchDB you need to create a view password
 * with the following map function:
    function(doc) {
        if(doc.username && doc.password) {
            emit(doc.username, doc.password);
         }
    }
 *
 * Created by robin on 26.04.15.
 */
public  class StreamPipeRealm implements Realm {

    Logger LOG = LoggerFactory.getLogger(StreamPipeRealm.class);

    SimpleCredentialsMatcher credentialsMatcher;

    public StreamPipeRealm() {
        this.credentialsMatcher = new SimpleCredentialsMatcher();
    }

    @Override
    public String getName() {
        return "StreamPipeRealm";
    }

    @Override
    /**
     * Check if type of AuthenticationToken is supported.
     * So far we only support UsernamePasswordToken.
     */
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof UsernamePasswordToken;
    }

    @Override
    /**
     * Checks if token is correct. See class JavaDoc for creating the password view.
     */
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        if (authenticationToken instanceof  UsernamePasswordToken) {
            CouchDbClient dbClient = de.fzi.cep.sepa.storage.util.Utils.getCouchDbUserClient();
            try {
                String email = ((UsernamePasswordToken) authenticationToken).getUsername();
                List<JsonObject> users = dbClient.view("users/password").key(email).includeDocs(true).query(JsonObject.class);
                if (users.size() != 1) throw new AuthenticationException("None or to many users with matching username");
                JsonObject user = users.get(0);
                String password = user.get("password").getAsString();

                SimpleAuthenticationInfo info = new SimpleAuthenticationInfo();
                SimplePrincipalCollection principals = new SimplePrincipalCollection();
                principals.add(email, this.getName());

                LOG.info(principals.toString());

                info.setPrincipals(principals);
                info.setCredentials(password);

                if (credentialsMatcher.doCredentialsMatch(authenticationToken, info)) {
                    System.out.println("User successfully authenticated");
                } else {
                    throw new AuthenticationException("Could not authenticate");
                }
                return info;

            } catch (CouchDbException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        return null;
        //System.out.println(authenticationToken.getPrincipal().toString());

        //Get AuthenticationInfo from System
       /* SimpleAuthenticationInfo info = new SimpleAuthenticationInfo();
        SimplePrincipalCollection principals = new SimplePrincipalCollection();
        principals.add("username", this.getName());
        info.setPrincipals(principals);
        info.setCredentials("password");*/


    }
}