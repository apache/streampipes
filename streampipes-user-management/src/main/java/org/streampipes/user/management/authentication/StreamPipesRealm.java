package org.streampipes.user.management.authentication;


import com.google.gson.JsonObject;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.storage.util.Utils;

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
public  class StreamPipesRealm implements Realm {

    Logger LOG = LoggerFactory.getLogger(StreamPipesRealm.class);

    StreamPipesCredentialsMatcher credentialsMatcher;

    public StreamPipesRealm() {
        this.credentialsMatcher = new StreamPipesCredentialsMatcher();
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
            CouchDbClient dbClient = Utils.getCouchDbUserClient();
            try {
                String email = ((UsernamePasswordToken) authenticationToken).getUsername();
                List<JsonObject> users = dbClient.view("users/password").key(email).includeDocs(true).query(JsonObject.class);
                if (users.size() != 1) throw new AuthenticationException("None or too many users " +
                        "with matching username");
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
    }
}