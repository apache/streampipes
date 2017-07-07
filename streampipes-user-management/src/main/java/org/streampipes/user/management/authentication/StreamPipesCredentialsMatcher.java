package org.streampipes.user.management.authentication;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.streampipes.user.management.util.PasswordUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by riemer on 07.07.2017.
 */
public class StreamPipesCredentialsMatcher implements CredentialsMatcher {

  @Override
  public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
    try {
      return PasswordUtil.validatePassword(new String((char[]) authenticationToken
                      .getCredentials()),
              authenticationInfo
              .getCredentials().toString());
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      return false;
    }
  }
}
