package de.fzi.cep.sepa.rest.test;

import de.fzi.cep.sepa.rest.StreamPipeRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for the shiro realm.
 * Working UsernamePassWordToken is 'username' + 'password'
 *
 * Created by robin on 28.04.15.
 */
public class TestShiro {

    private static final Logger LOG = LoggerFactory.getLogger(TestShiro.class);

    public static void main(String[] args) {
        Realm realm = new StreamPipeRealm();
        SecurityManager securityManager = new DefaultSecurityManager(realm);
        SecurityUtils.setSecurityManager(securityManager);

        UsernamePasswordToken testToken = new UsernamePasswordToken("username", "password");
        //testToken.setRememberMe(true);
        Subject currentUser = SecurityUtils.getSubject();

        try {
            currentUser.login(testToken);
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
        }

        //AuthenticationInfo info = realm.getAuthenticationInfo(testToken);
        //System.out.println(info.getCredentials().toString());
    }
}
