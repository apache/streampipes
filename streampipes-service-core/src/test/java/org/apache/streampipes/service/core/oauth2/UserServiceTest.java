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
import org.apache.streampipes.commons.environment.model.OAuthConfiguration;
import org.apache.streampipes.model.client.user.DefaultRole;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.rest.security.OAuth2AuthenticationProcessingException;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

  private static final String PROVIDER = "keycloak";
  private static final String OTHER_PROVIDER = "azure";
  private static final String USER_ID_CLAIM = "preferred_username";
  private static final String EMAIL_CLAIM = "email";
  private static final String NAME_CLAIM = "name";

  private static final String ADMIN_ID = "8103eadc-3c9f-4a0e-9a52-0d1c7c1f5b11";
  private static final String ADMIN_USERNAME = "admin@streampipes.apache.org";

  private Map<String, Principal> usersById;
  private IUserStorage userStorage;
  private UserService userService;

  @BeforeEach
  void setUp() {
    usersById = new HashMap<>();
    userStorage = mock(IUserStorage.class);
    when(userStorage.getUserById(anyString())).thenAnswer(i -> usersById.get(i.<String>getArgument(0)));
    when(userStorage.checkUserExists(anyString())).thenAnswer(i -> usersById
        .values()
        .stream()
        .anyMatch(p -> p.getUsername().equalsIgnoreCase(i.getArgument(0))));
    doAnswer(i -> {
      Principal principal = i.getArgument(0);
      usersById.put(principal.getPrincipalId(), principal);
      return null;
    }).when(userStorage).storeUser(any());

    var env = mock(Environment.class);
    when(env.getOAuthConfigurations()).thenReturn(List.of(makeConfig()));

    userService = new UserService(
        userStorage,
        mock(IRoleStorage.class),
        mock(IUserGroupStorage.class),
        mock(IPermissionStorage.class),
        env
    );
  }

  @Test
  void rejectsSignUpWhenEmailMatchesUsernameOfExistingAccount() {
    addLocalAdmin();

    assertThrows(
        OAuth2AuthenticationProcessingException.class,
        () -> userService.processUserRegistration(PROVIDER, claims("attacker", ADMIN_USERNAME))
    );

    verify(userStorage, never()).storeUser(any());
    verify(userStorage, never()).updateUser(any());
    assertEquals(1, usersById.size());
  }

  @Test
  void rejectsSignUpWhenEmailMatchesUsernameInDifferentCase() {
    addLocalAdmin();

    assertThrows(
        OAuth2AuthenticationProcessingException.class,
        () -> userService.processUserRegistration(PROVIDER, claims("attacker", "Admin@StreamPipes.Apache.Org"))
    );

    verify(userStorage, never()).storeUser(any());
  }

  @Test
  void rejectsLoginWhenUserIdMatchesLocalAccount() {
    var admin = addLocalAdmin();

    assertThrows(
        OAuth2AuthenticationProcessingException.class,
        () -> userService.processUserRegistration(PROVIDER, claims(ADMIN_ID, "attacker@example.org"))
    );

    verify(userStorage, never()).storeUser(any());
    verify(userStorage, never()).updateUser(any());
    assertEquals(UserAccount.LOCAL, admin.getProvider());
    assertEquals(Set.of(DefaultRole.Constants.ROLE_ADMIN_VALUE), admin.getRoles());
    assertEquals(0L, admin.getLastLoginAtMillis());
  }

  @Test
  void rejectsLoginWhenUserIdMatchesAccountOfOtherProvider() {
    var account = makeAccount("external-id", "user@example.org", OTHER_PROVIDER);
    usersById.put(account.getPrincipalId(), account);

    assertThrows(
        OAuth2AuthenticationProcessingException.class,
        () -> userService.processUserRegistration(PROVIDER, claims("external-id", "user@example.org"))
    );

    verify(userStorage, never()).updateUser(any());
  }

  @Test
  void rejectsLoginWhenUserIdMatchesServiceAccount() {
    var serviceAccount = ServiceAccount.from("sp-service-client", "secret", new HashSet<>());
    serviceAccount.setPrincipalId("service-id");
    usersById.put(serviceAccount.getPrincipalId(), serviceAccount);

    assertThrows(
        OAuth2AuthenticationProcessingException.class,
        () -> userService.processUserRegistration(PROVIDER, claims("service-id", "attacker@example.org"))
    );

    verify(userStorage, never()).updateUser(any());
  }

  @Test
  void rejectionDoesNotRevealWhichValueIsInUse() {
    addLocalAdmin();

    var usernameInUse = assertThrows(
        OAuth2AuthenticationProcessingException.class,
        () -> userService.processUserRegistration(PROVIDER, claims("attacker", ADMIN_USERNAME))
    );
    var userIdInUse = assertThrows(
        OAuth2AuthenticationProcessingException.class,
        () -> userService.processUserRegistration(PROVIDER, claims(ADMIN_ID, "attacker@example.org"))
    );

    assertEquals(usernameInUse.getMessage(), userIdInUse.getMessage());
    assertFalse(usernameInUse.getMessage().toLowerCase().contains("email"));
    assertFalse(usernameInUse.getMessage().contains(ADMIN_USERNAME));
    assertFalse(userIdInUse.getMessage().contains(ADMIN_ID));
  }

  @Test
  void createsDisabledAccountForNewUserWithUnusedEmail() {
    addLocalAdmin();

    var details = userService.processUserRegistration(PROVIDER, claims("new-user", "new.user@example.org"));

    var created = (UserAccount) usersById.get("new-user");
    assertEquals("new.user@example.org", created.getUsername());
    assertEquals(PROVIDER, created.getProvider());
    assertEquals("New User", created.getFullName());
    assertFalse(created.isAccountEnabled());
    assertEquals("new.user@example.org", details.getUsername());
    verify(userStorage).storeUser(created);
    verify(userStorage, never()).updateUser(any());
  }

  @Test
  void returningUserOfSameProviderIsUpdatedAndNotChecked() {
    var account = makeAccount("returning-user", "returning@example.org", PROVIDER);
    usersById.put(account.getPrincipalId(), account);

    var details = userService.processUserRegistration(PROVIDER, claims("returning-user", "returning@example.org"));

    assertEquals("returning@example.org", details.getUsername());
    verify(userStorage).updateUser(account);
    verify(userStorage, never()).storeUser(any());
    verify(userStorage, never()).checkUserExists(anyString());
  }

  private UserAccount addLocalAdmin() {
    var admin = makeAccount(ADMIN_ID, ADMIN_USERNAME, UserAccount.LOCAL);
    admin.setRoles(new HashSet<>(Set.of(DefaultRole.Constants.ROLE_ADMIN_VALUE)));
    usersById.put(admin.getPrincipalId(), admin);
    return admin;
  }

  private UserAccount makeAccount(String principalId,
                                  String username,
                                  String provider) {
    var account = UserAccount.from(username, "password", new HashSet<>());
    account.setPrincipalId(principalId);
    account.setProvider(provider);
    return account;
  }

  private Map<String, Object> claims(String userId,
                                     String email) {
    return Map.of(USER_ID_CLAIM, userId, EMAIL_CLAIM, email, NAME_CLAIM, "New User");
  }

  private OAuthConfiguration makeConfig() {
    var config = new OAuthConfiguration();
    config.setRegistrationId(PROVIDER);
    config.setUserIdAttributeName(USER_ID_CLAIM);
    config.setEmailAttributeName(EMAIL_CLAIM);
    config.setFullNameAttributeName(NAME_CLAIM);
    config.setDefaultRoles(Set.of());
    return config;
  }
}
