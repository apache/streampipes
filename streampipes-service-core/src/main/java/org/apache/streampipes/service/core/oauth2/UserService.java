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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.resource.management.UserResourceManager;
import org.apache.streampipes.rest.security.OAuth2AuthenticationProcessingException;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class UserService {

  private final IUserStorage userStorage;
  private final Environment env;

  public UserService() {
    this.userStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI();
    this.env = Environments.getEnvironment();
  }

  public OidcUserAccountDetails processUserRegistration(String registrationId,
                                                        Map<String, Object> attributes) {
    return processUserRegistration(registrationId, attributes, null, null);
  }

  public OidcUserAccountDetails processUserRegistration(String registrationId,
                                                        Map<String, Object> attributes,
                                                        OidcIdToken idToken,
                                                        OidcUserInfo userInfo) {
    var oAuthConfigOpt = env.getOAuthConfigurations()
        .stream()
        .filter(c -> c.getRegistrationId().equals(registrationId))
        .findFirst();

    if (oAuthConfigOpt.isPresent()) {
      var oAuthConfig = oAuthConfigOpt.get();
      var principalId = attributes.get(oAuthConfig.getUserIdAttributeName()).toString();
      var fullName = attributes.get(oAuthConfig.getFullNameAttributeName());
      if (oAuthConfig.getEmailAttributeName().isEmpty()) {
        throw new OAuth2AuthenticationProcessingException("Email attribute key not found in attributes");
      }
      var email = attributes.get(oAuthConfig.getEmailAttributeName()).toString();
      UserAccount user = (UserAccount) userStorage.getUserById(principalId);
      if (user != null) {
        if (!user.getProvider().equals(registrationId) && !user.getProvider().equals(UserAccount.LOCAL)) {
          throw new OAuth2AuthenticationProcessingException(
              String.format("Already signed up with another provider %s", user.getProvider())
          );
        }
      } else {
        new UserResourceManager().registerOauthUser(toUserAccount(registrationId, principalId, email, fullName));
        user = (UserAccount) userStorage.getUserById(principalId);
      }
      return OidcUserAccountDetails.create(user, attributes, idToken, userInfo);
    } else {
      throw new OAuth2AuthenticationProcessingException(
          String.format("No config found for provider %s", registrationId)
      );
    }
  }

  private UserAccount toUserAccount(String registrationId,
                                    String principalId,
                                    String email,
                                    Object fullName) {
    List<Role> roles = Stream.of(Role.ROLE_ADMIN.toString()).map(Role::valueOf).toList();
    var user = UserAccount.from(email, null, new HashSet<>(roles));
    user.setPrincipalId(principalId);
    if (Objects.nonNull(fullName)) {
      user.setFullName(fullName.toString());
    }
    user.setAccountEnabled(false);
    user.setProvider(registrationId);
    return user;
  }
}
