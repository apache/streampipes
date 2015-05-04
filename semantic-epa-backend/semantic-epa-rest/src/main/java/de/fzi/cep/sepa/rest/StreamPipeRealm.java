package de.fzi.cep.sepa.rest;


import de.fzi.cep.sepa.model.client.user.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.SimpleByteSource;

/**
 * Principals are a subjects identifying attributesm such as first name, last name or user name.
 * Credentials are usually secret values known only by the subject
 *
 *
 * Created by robin on 26.04.15.
 */
public  class StreamPipeRealm implements Realm {
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
        if (authenticationToken instanceof UsernamePasswordToken) {
            return true;
        } else return false;
    }

    @Override
    /**
     * Checks if token is correct.
     */
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        User user = new User("username", "username", "password", null);

        //Get AuthenticationInfo from System
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo();
        SimplePrincipalCollection principals = new SimplePrincipalCollection();
        principals.add("username", this.getName());
        info.setPrincipals(principals);
        info.setCredentials("password");

       if (credentialsMatcher.doCredentialsMatch(authenticationToken, info)) {
           System.out.println("User successfully authenticated");
       } else {
           throw new AuthenticationException("Could not authenticate");
       }

        return info;
    }
}