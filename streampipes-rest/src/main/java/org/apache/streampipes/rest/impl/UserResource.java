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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.mail.MailSender;
import org.apache.streampipes.model.ShortUserInfo;
import org.apache.streampipes.model.client.user.ChangePasswordRequest;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.RawUserApiToken;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.utils.Utils;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;
import org.apache.streampipes.user.management.service.TokenService;
import org.apache.streampipes.user.management.util.PasswordUtil;

import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/users")
public class UserResource extends AbstractAuthGuardedRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ShortUserInfo>> listUsers() {
    var users = getUserStorage()
        .getAllUserAccounts()
        .stream()
        .map(u -> ShortUserInfo.create(u.getPrincipalId(), u.getUsername(), u.getFullName()))
        .collect(Collectors.toList());

    return ok(users);
  }


  @GetMapping(path = "{principalId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getUserDetails(@PathVariable("principalId") String principalId) {
    Principal principal = getPrincipalById(principalId);
    Utils.removeCredentials(principal);

    if (principal != null) {
      return ok(principal);
    } else {
      return statusMessage(Notifications.error("User not found"));
    }
  }

  @GetMapping(path = "username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getUserDetailsByName(@PathVariable("username") String username) {
    Principal principal = getPrincipal(username);
    Utils.removeCredentials(principal);

    if (principal != null) {
      return ok(principal);
    } else {
      return statusMessage(Notifications.error("User not found"));
    }
  }

  @DeleteMapping(path = "{principalId}")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<?> deleteUser(@PathVariable("principalId") String principalId) {
    Principal principal = getPrincipalById(principalId);

    if (principal != null) {
      getUserStorage().deleteUser(principalId);
      return ok();
    } else {
      return statusMessage(Notifications.error("User not found"));
    }
  }

  @PutMapping(path = "{userId}/appearance/mode/{darkMode}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<? extends Message> updateAppearanceMode(@PathVariable("darkMode") boolean darkMode) {
    String authenticatedUsername = getAuthenticatedUsername();
    if (authenticatedUsername != null) {
      UserAccount user = getUser(authenticatedUsername);
      user.setDarkMode(darkMode);
      getUserStorage().updateUser(user);

      return ok(Notifications.success("Appearance updated"));
    } else {
      return statusMessage(Notifications.error("User not found"));
    }
  }

  @PostMapping(
      path = "/user",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<?> registerUser(@RequestBody UserAccount userAccount) {
    try {
      if (getUserStorage().getUser(userAccount.getUsername()) == null) {
        String property = userAccount.getPassword();
        if (property != null) {
          encryptAndStore(userAccount, property);
        } else {
          String generatedProperty = PasswordUtil.generateRandomPassword();
          encryptAndStore(userAccount, generatedProperty);
          new MailSender().sendInitialPasswordMail(userAccount.getUsername(), generatedProperty);
        }
        return ok();
      } else {
        return badRequest(Notifications.error("This user ID already exists. Please choose another address."));
      }
    } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
      return badRequest();
    }
  }

  @PostMapping(
      path = "/service",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<?> registerService(@RequestBody ServiceAccount serviceAccount) {
    if (getUserStorage().getUser(serviceAccount.getUsername()) == null) {
      serviceAccount.setClientSecret(SecretEncryptionManager.encrypt(serviceAccount.getClientSecret()));
      serviceAccount.setSecretEncrypted(true);
      getUserStorage().storeUser(serviceAccount);
      return ok();
    } else {
      return badRequest(Notifications.error("This user ID already exists. Please choose another address."));
    }
  }

  @PostMapping(
      path = "{userId}/tokens",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createNewApiToken(@PathVariable("userId") String username,
                                             @RequestBody RawUserApiToken rawToken) {
    String authenticatedUserName = getAuthenticatedUsername();
    if (authenticatedUserName.equals(username)) {
      RawUserApiToken generatedToken = new TokenService().createAndStoreNewToken(username, rawToken);
      return ok(generatedToken);
    } else {
      return statusMessage(Notifications.error("User not found"));
    }
  }

  @PutMapping(
      path = "user/{principalId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateUserAccountDetails(@PathVariable("principalId") String principalId,
                                                    @RequestBody UserAccount user) {
    String authenticatedUserId = getAuthenticatedUserSid();
    if (user != null && (authenticatedUserId.equals(principalId) || isAdmin())) {
      UserAccount existingUser = (UserAccount) getPrincipalById(principalId);
      updateUser(existingUser, user, isAdmin(), existingUser.getPassword());
      user.setRev(existingUser.getRev());
      getUserStorage().updateUser(user);
      return ok(Notifications.success("User updated"));
    } else {
      return statusMessage(Notifications.error("User not found"));
    }
  }

  @PutMapping(
      path = "user/{principalId}/username",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateUsername(@PathVariable("principalId") String principalId,
                                 @RequestBody UserAccount user) {
    String authenticatedUserId = getAuthenticatedUserSid();
    if (user != null && (authenticatedUserId.equals(principalId) || isAdmin())) {
      UserAccount existingUser = (UserAccount) getPrincipalById(principalId);
      try {
        if (PasswordUtil.validatePassword(user.getPassword(), existingUser.getPassword())) {
          existingUser.setUsername(user.getUsername());

          if (getUserStorage()
              .getAllUserAccounts()
              .stream()
              .noneMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()))) {
            updateUser(existingUser, user, isAdmin(), existingUser.getPassword());
            getUserStorage().updateUser(existingUser);
            return ok();
          } else {
            return badRequest(Notifications.error("Username already taken"));
          }
        } else {
          return badRequest(Notifications.error("Incorrect password"));
        }
      } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        return badRequest();
      }
    }

    return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
  }

  @PutMapping(path = "user/{principalId}/password",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updatePassword(@PathVariable("principalId") String principalId,
                                 @RequestBody ChangePasswordRequest passwordRequest) {
    String authenticatedUserId = getAuthenticatedUserSid();
    UserAccount existingUser = (UserAccount) getPrincipalById(principalId);
    if (principalId.equals(authenticatedUserId) || isAdmin()) {
      try {
        String existingPw = passwordRequest.existingPassword();
        if (PasswordUtil.validatePassword(existingPw, existingUser.getPassword())) {
          String newEncryptedPw = PasswordUtil.encryptPassword(passwordRequest.newPassword());
          updateUser(existingUser, existingUser, isAdmin(), newEncryptedPw);
          getUserStorage().updateUser(existingUser);

          return ok();
        } else {
          return badRequest(Notifications.error("Wrong password"));
        }
      } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        return badRequest();
      }
    } else {
      return badRequest(Notifications.error("The user ID does not match the current user."));
    }
  }

  @PutMapping(
      path = "service/{principalId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<? extends Message> updateServiceAccountDetails(@PathVariable("principalId") String principalId,
                                              @RequestBody ServiceAccount user) {
    String authenticatedUserId = getAuthenticatedUserSid();
    if (user != null && (authenticatedUserId.equals(principalId) || isAdmin())) {
      Principal existingUser = getPrincipalById(principalId);
      user.setRev(existingUser.getRev());
      if (!isAdmin()) {
        replacePermissions(user, existingUser);
      }
      if (!user.isSecretEncrypted()) {
        user.setClientSecret(SecretEncryptionManager.encrypt(user.getClientSecret()));
        user.setSecretEncrypted(true);
      }
      getUserStorage().updateUser(user);
      return ok(Notifications.success("User updated"));
    } else {
      return statusMessage(Notifications.error("User not found"));
    }
  }

  private boolean isAdmin() {
    return SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getAuthorities()
        .stream()
        .anyMatch(r -> r.getAuthority().equals(Role.ROLE_ADMIN.name()));
  }

  private void updateUser(UserAccount existingUser,
                          UserAccount user,
                          boolean adminPrivileges,
                          String property) {
    user.setPassword(property);
    user.setProvider(existingUser.getProvider());
    if (!existingUser.getProvider().equals(UserAccount.LOCAL)) {
      // These settings are managed externally
      user.setUsername(existingUser.getUsername());
      user.setFullName(existingUser.getFullName());
    }
    if (!adminPrivileges) {
      replacePermissions(user, existingUser);
    }
    user.setUserApiTokens(existingUser
        .getUserApiTokens()
        .stream()
        .filter(existingToken -> user.getUserApiTokens()
            .stream()
            .anyMatch(updatedToken -> existingToken
                .getTokenId()
                .equals(updatedToken.getTokenId())))
        .collect(Collectors.toList()));
  }

  private void encryptAndStore(UserAccount userAccount,
                               String property) throws NoSuchAlgorithmException, InvalidKeySpecException {
    String encryptedProperty = PasswordUtil.encryptPassword(property);
    userAccount.setPassword(encryptedProperty);
    getUserStorage().storeUser(userAccount);
  }

  private UserAccount getUser(String username) {
    return getUserStorage().getUserAccount(username);
  }

  private Principal getPrincipal(String username) {
    return getUserStorage().getUser(username);
  }

  private Principal getPrincipalById(String principalId) {
    return getUserStorage().getUserById(principalId);
  }

  private void replacePermissions(Principal principal, Principal existingPrincipal) {
    principal.setGroups(existingPrincipal.getGroups());
    principal.setObjectPermissions(existingPrincipal.getObjectPermissions());
    principal.setRoles(existingPrincipal.getRoles());
  }
}
