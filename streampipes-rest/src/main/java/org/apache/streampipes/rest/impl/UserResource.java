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
import org.apache.streampipes.model.client.user.ChangePasswordRequest;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.PrincipalType;
import org.apache.streampipes.model.client.user.RawUserApiToken;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;
import org.apache.streampipes.user.management.service.TokenService;
import org.apache.streampipes.user.management.util.PasswordUtil;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v2/users")
@Component
public class UserResource extends AbstractAuthGuardedRestResource {

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response getAllUsers(@QueryParam("type") String principalType) {
    List<Principal> allPrincipals = new ArrayList<>();
    if (principalType != null && principalType.equals(PrincipalType.USER_ACCOUNT.name())) {
      allPrincipals.addAll(getUserStorage().getAllUserAccounts());
    } else if (principalType != null && principalType.equals(PrincipalType.SERVICE_ACCOUNT.name())) {
      allPrincipals.addAll(getUserStorage().getAllServiceAccounts());
    } else {
      allPrincipals.addAll(getUserStorage().getAllUsers());
    }
    removeCredentials(allPrincipals);
    return ok(allPrincipals);
  }


  @GET
  @JacksonSerialized
  @Path("{principalId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUserDetails(@PathParam("principalId") String principalId) {
    Principal principal = getPrincipalById(principalId);
    removeCredentials(principal);

    if (principal != null) {
      return ok(principal);
    } else {
      return statusMessage(Notifications.error("User not found"));
    }
  }

  @GET
  @JacksonSerialized
  @Path("username/{username}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUserDetailsByName(@PathParam("username") String username) {
    Principal principal = getPrincipal(username);
    removeCredentials(principal);

    if (principal != null) {
      return ok(principal);
    } else {
      return statusMessage(Notifications.error("User not found"));
    }
  }

  @DELETE
  @JacksonSerialized
  @Path("{principalId}")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response deleteUser(@PathParam("principalId") String principalId) {
    Principal principal = getPrincipalById(principalId);

    if (principal != null) {
      getUserStorage().deleteUser(principalId);
      return ok();
    } else {
      return statusMessage(Notifications.error("User not found"));
    }
  }

  @Path("{userId}/appearance/mode/{darkMode}")
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateAppearanceMode(@PathParam("userId") String userId,
                                       @PathParam("darkMode") boolean darkMode) {
    String authenticatedUserId = getAuthenticatedUsername();
    if (authenticatedUserId != null) {
      UserAccount user = getUser(authenticatedUserId);
      user.setDarkMode(darkMode);
      getUserStorage().updateUser(user);

      return ok(Notifications.success("Appearance updated"));
    } else {
      return statusMessage(Notifications.error("User not found"));
    }
  }

  @POST
  @Path("/user")
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerUser(UserAccount userAccount) {
    // TODO check if userId is already taken
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

  @POST
  @Path("/service")
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerService(ServiceAccount serviceAccount) {
    // TODO check if userId is already taken
    if (getUserStorage().getUser(serviceAccount.getUsername()) == null) {
      serviceAccount.setClientSecret(SecretEncryptionManager.encrypt(serviceAccount.getClientSecret()));
      serviceAccount.setSecretEncrypted(true);
      getUserStorage().storeUser(serviceAccount);
      return ok();
    } else {
      return badRequest(Notifications.error("This user ID already exists. Please choose another address."));
    }
  }

  @POST
  @Path("{userId}/tokens")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response createNewApiToken(@PathParam("userId") String userId,
                                    RawUserApiToken rawToken) {
    RawUserApiToken generatedToken = new TokenService().createAndStoreNewToken(userId, rawToken);
    return ok(generatedToken);
  }

  @PUT
  @Path("user/{principalId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateUserAccountDetails(@PathParam("principalId") String principalId,
                                           UserAccount user) {
    String authenticatedUserId = getAuthenticatedUsername();
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

  @PUT
  @Path("user/{principalId}/username")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateUsername(@PathParam("principalId") String principalId,
                                 UserAccount user) {
    String authenticatedUserId = getAuthenticatedUsername();
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

    return Response.status(401).build();
  }

  @PUT
  @Path("user/{principalId}/password")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updatePassword(@PathParam("principalId") String principalId,
                                 ChangePasswordRequest passwordRequest) {
    String authenticatedUserId = getAuthenticatedUsername();
    UserAccount existingUser = (UserAccount) getPrincipalById(principalId);
    if (principalId.equals(authenticatedUserId) || isAdmin()) {
      try {
        String existingPw = passwordRequest.getExistingPassword();
        if (PasswordUtil.validatePassword(existingPw, existingUser.getPassword())) {
          String newEncryptedPw = PasswordUtil.encryptPassword(passwordRequest.getNewPassword());
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

  @PUT
  @Path("service/{principalId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateServiceAccountDetails(@PathParam("principalId") String principalId,
                                              ServiceAccount user) {
    String authenticatedUserId = getAuthenticatedUsername();
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

  private void removeCredentials(List<Principal> principals) {
    principals.forEach(this::removeCredentials);
  }

  private void removeCredentials(Principal principal) {
    if (principal instanceof UserAccount) {
      ((UserAccount) principal).setPassword("");
    }
  }

  private void replacePermissions(Principal principal, Principal existingPrincipal) {
    principal.setGroups(existingPrincipal.getGroups());
    principal.setObjectPermissions(existingPrincipal.getObjectPermissions());
    principal.setRoles(existingPrincipal.getRoles());
  }
}
