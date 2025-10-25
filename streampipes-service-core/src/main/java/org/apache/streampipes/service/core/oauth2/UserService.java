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
import org.apache.streampipes.commons.environment.model.OAuthConfiguration;
import org.apache.streampipes.model.client.user.Group;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.resource.management.UserResourceManager;
import org.apache.streampipes.rest.security.OAuth2AuthenticationProcessingException;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final IUserStorage userStorage;
  private final CRUDStorage<Role> roleStorage;
  private final CRUDStorage<Group> groupStorage;
  private final Environment env;
  private List<Role> allRoles;
  private List<Group> allGroups;

  public UserService() {
    this.userStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI();
    this.roleStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getRoleStorage();
    this.groupStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getUserGroupStorage();
    this.allGroups = this.groupStorage.findAll();
    this.allRoles = this.roleStorage.findAll();
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
        applyRoles(user, oAuthConfig, attributes, false);
        user.setLastLoginAtMillis(System.currentTimeMillis());
        userStorage.updateUser(user);
      } else {
        user = toUserAccount(registrationId, principalId, email, fullName);
        user.setLastLoginAtMillis(System.currentTimeMillis());
        applyRoles(user, oAuthConfig, attributes, true);
        new UserResourceManager().storeUser(user);
      }

      user = (UserAccount) userStorage.getUserById(principalId);
      return OidcUserAccountDetails.create(user, attributes, idToken, userInfo);
    } else {
      throw new OAuth2AuthenticationProcessingException(
          String.format("No config found for provider %s", registrationId)
      );
    }
  }

  private void applyRoles(UserAccount user,
                          OAuthConfiguration oAuthConfig,
                          Map<String, Object> attributes,
                          boolean newUser) {
    if (oAuthConfig.getRoleAttributeName() != null) {
      Object rolesObject = attributes.get(oAuthConfig.getRoleAttributeName());

      if (rolesObject instanceof List<?> rolesList) {
        Set<String> roles = extractRoleOrGroup("ROLE", rolesList);
        Set<String> groups = convertGroup(extractRoleOrGroup("GROUP", rolesList));

        allRoles.forEach(role -> {
          if (Objects.nonNull(role.getAlternateIds())) {
            role.getAlternateIds().forEach(a -> {
              if (rolesList.contains(a)) {
                roles.add(role.getElementId());
              }
            });
          }
        });

        allGroups.forEach(group -> {
          if (Objects.nonNull(group.getAlternateIds())) {
            group.getAlternateIds().forEach(a -> {
              if (rolesList.contains(a)) {
                groups.add(group.getElementId());
              }
            });
          }
        });

        user.setRoles(roles);
        user.setGroups(groups);
        user.setExternallyManagedRoles(true);
      } else {
        LOG.warn(
            "Invalid role attribute: {} of type {}",
            oAuthConfig.getRoleAttributeName(),
            Objects.nonNull(rolesObject) ? rolesObject.getClass().getName() : "null"
        );
        applyDefaultRole(user, oAuthConfig.getDefaultRoles(), newUser);
      }
    } else {
      LOG.warn("Applying default roles as no role attribute is configured");
      applyDefaultRole(user, oAuthConfig.getDefaultRoles(), newUser);
    }
  }

  private void applyDefaultRole(UserAccount user,
                                Set<String> defaultRoles,
                                boolean newUser) {
    if (newUser) {
      user.setRoles(
          defaultRoles
              .stream()
              .filter(r -> allRoles
                  .stream()
                  .anyMatch(role -> role.getElementId().equals(r)))
              .collect(Collectors.toSet())
      );
      user.setExternallyManagedRoles(false);
    }
  }

  private UserAccount toUserAccount(String registrationId,
                                    String principalId,
                                    String email,
                                    Object fullName) {
    var user = UserAccount.from(email, null, new HashSet<>());
    user.setPrincipalId(principalId);
    if (Objects.nonNull(fullName)) {
      user.setFullName(fullName.toString());
    }
    user.setAccountEnabled(false);
    user.setProvider(registrationId);
    return user;
  }

  private Set<String> extractRoleOrGroup(String type,
                                         List<?> roles) {
    return roles.stream()
        .filter(role -> role instanceof String)
        .filter(role -> ((String) role).startsWith(type))
        .map(role -> (String) role)
        .collect(Collectors.toSet());
  }

  private Set<String> convertGroup(Set<String> groups) {
    return groups.stream()
        .map(group -> group.split("_"))
        .filter(parts -> parts.length == 2)
        .map(parts -> parts[1])
        .collect(Collectors.toSet());
  }
}
