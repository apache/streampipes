/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.service.core.oauth2;


import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.rest.security.OAuth2AuthenticationProcessingException;
import org.apache.streampipes.storage.api.user.IPermissionStorage;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CustomOidcUserService extends OidcUserService {

  private final IPermissionStorage permissionStorage;

  public CustomOidcUserService(IPermissionStorage permissionStorage) {
    this.permissionStorage = permissionStorage;
    var env = Environments.getEnvironment();
    this.setRetrieveUserInfo(req -> {
      var config = env.getOAuthConfigurations()
          .stream()
          .filter(c -> c.getRegistrationId().equals(req.getClientRegistration().getRegistrationId()))
          .findFirst();
      return config
          .filter(oAuthConfiguration -> Objects.nonNull(oAuthConfiguration.getUserInfoUri()))
          .isPresent();
    });
  }

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = super.loadUser(userRequest);
    try {
      var provider = userRequest.getClientRegistration().getRegistrationId();
      return new UserService(permissionStorage).processUserRegistration(
          provider,
          oidcUser.getAttributes(),
          oidcUser.getIdToken(),
          oidcUser.getUserInfo()
      );
    } catch (AuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new OAuth2AuthenticationProcessingException(e.getMessage(), e.getCause());
    }
  }
}
