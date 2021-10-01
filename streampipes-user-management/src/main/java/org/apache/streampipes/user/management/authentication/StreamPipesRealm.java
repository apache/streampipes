///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// */
//
//package org.apache.streampipes.user.management.authentication;
//
//
//import org.apache.shiro.authc.*;
//import org.apache.shiro.realm.Realm;
//import org.apache.shiro.subject.SimplePrincipalCollection;
//import org.apache.streampipes.model.client.user.User;
//import org.apache.streampipes.user.management.service.TokenService;
//import org.apache.streampipes.user.management.service.UserService;
//import org.apache.streampipes.user.management.util.TokenUtil;
//import org.lightcouch.CouchDbException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
///**
// * Principals are a subjects identifying attributes  such as first name, last name or user name.
// * Credentials are usually secret values known only by the subject
// */
//public class StreamPipesRealm implements Realm {
//
//  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesRealm.class);
//
//  private StreamPipesCredentialsMatcher credentialsMatcher;
//
//  public StreamPipesRealm() {
//    this.credentialsMatcher = new StreamPipesCredentialsMatcher();
//  }
//
//  @Override
//  public String getName() {
//    return "StreamPipeRealm";
//  }
//
//  @Override
//  /**
//   * Check if type of AuthenticationToken is supported.
//   * So far we only support UsernamePasswordToken.
//   */
//  public boolean supports(AuthenticationToken authenticationToken) {
//    return authenticationToken instanceof UsernamePasswordToken || authenticationToken instanceof BearerToken;
//  }
//
//  @Override
//  /**
//   * Checks if token is correct. See class JavaDoc for creating the password view.
//   */
//  public AuthenticationInfo getAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
//
//    if (authenticationToken instanceof UsernamePasswordToken) {
//
//      try {
//        String email = ((UsernamePasswordToken) authenticationToken).getUsername();
//        UserService userService = new UserService(email);
//
//        SimpleAuthenticationInfo info = makeInfo(email);
//        info.setCredentials(userService.getPassword());
//
//        if (credentialsMatcher.doCredentialsMatch(authenticationToken, info)) {
//          LOG.info("User successfully authenticated");
//        } else {
//          throw new AuthenticationException("Could not authenticate");
//        }
//        return info;
//
//      } catch (CouchDbException | NullPointerException e) {
//        e.printStackTrace();
//      }
//    } else if (authenticationToken instanceof BearerToken) {
//      BearerToken token = (BearerToken) authenticationToken;
//      String hashedToken = TokenUtil.hashToken(token.getToken());
//      User user = new TokenService().findUserForToken(hashedToken);
//      SimpleAuthenticationInfo info = makeInfo(user.getEmail());
//
//      return info;
//    }
//
//    return null;
//  }
//
//  private SimpleAuthenticationInfo makeInfo(String email) {
//    SimpleAuthenticationInfo info = new SimpleAuthenticationInfo();
//    SimplePrincipalCollection principals = new SimplePrincipalCollection();
//    principals.add(email, this.getName());
//
//    LOG.info(principals.toString());
//    info.setPrincipals(principals);
//
//    return info;
//  }
//}
