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

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.UserNotFoundException;
import org.apache.streampipes.commons.exceptions.UsernameAlreadyTakenException;
import org.apache.streampipes.model.client.user.JwtAuthenticationResponse;
import org.apache.streampipes.model.client.user.LoginRequest;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.client.user.UserRegistrationData;
import org.apache.streampipes.model.configuration.GeneralConfig;
import org.apache.streampipes.model.message.ErrorMessage;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/auth")
public class Authentication extends AbstractRestResource {

  @Autowired
  AuthenticationManager authenticationManager;

  @PostMapping(
      path = "/login",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
      consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> doLogin(@RequestBody LoginRequest login) {
    try {
      org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(login.username(), login.password()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return processAuth(authentication);
    } catch (BadCredentialsException e) {
      return unauthorized();
    }
  }

  @GetMapping(
      path = "/token/renew",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> doLogin() {
    try {
      org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      return processAuth(auth);
    } catch (BadCredentialsException e) {
      return ok(new ErrorMessage(NotificationType.LOGIN_FAILED.uiNotification()));
    }
  }

  @PostMapping(
      path = "/register",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
      consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public synchronized ResponseEntity<SuccessMessage> doRegister(
      @RequestBody UserRegistrationData userRegistrationData
  ) {
    GeneralConfig config = getSpCoreConfigurationStorage().get().getGeneralConfig();
    if (!config.isAllowSelfRegistration()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    var enrichedUserRegistrationData = new UserRegistrationData(
        userRegistrationData.getUsername(),
        userRegistrationData.getPassword(),
        config.getDefaultUserRoles()
    );
    try {
      getSpResourceManager().manageUsers().registerUser(enrichedUserRegistrationData);
      return ok(new SuccessMessage(NotificationType.REGISTRATION_SUCCESS.uiNotification()));
    } catch (UsernameAlreadyTakenException e) {
      throw new SpMessageException(
          HttpStatus.BAD_REQUEST,
          Notifications.error("This email address already exists. Please choose another address."));
    } catch (IllegalArgumentException e) {
      throw new SpMessageException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          Notifications.error("User registration failed. Please report this to your admin."));
    }
  }

  @PostMapping(
      path = "restore/{username}",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> sendPasswordRecoveryLink(@PathVariable("username") String username) {
    try {
      getSpResourceManager().manageUsers().sendPasswordRecoveryLink(username);
      return ok(new SuccessMessage(NotificationType.PASSWORD_RECOVERY_LINK_SENT.uiNotification()));
    } catch (UserNotFoundException e) {
      return ok();
    } catch (Exception e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, e);
    }
  }

  @GetMapping(
      path = "settings",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> getAuthSettings() {
    GeneralConfig config = getSpCoreConfigurationStorage().get().getGeneralConfig();
    Map<String, Object> response = new HashMap<>();
    response.put("allowSelfRegistration", config.isAllowSelfRegistration());
    response.put("allowPasswordRecovery", config.isAllowPasswordRecovery());
    response.put("linkSettings", config.getLinkSettings());
    response.put("oAuthSettings", makeOAuthSettings());

    return ok(response);
  }

  private ResponseEntity<JwtAuthenticationResponse> processAuth(org.springframework.security.core.Authentication auth) {
    Principal principal = ((PrincipalUserDetails<?>) auth.getPrincipal()).getDetails();
    if (principal instanceof UserAccount) {
      JwtAuthenticationResponse tokenResp = makeJwtResponse(auth);
      return ok(tokenResp);
    } else {
      throw new BadCredentialsException("Could not create auth token");
    }
  }

  private JwtAuthenticationResponse makeJwtResponse(org.springframework.security.core.Authentication auth) {
    String jwt = new JwtTokenProvider().createToken(auth);
    return new JwtAuthenticationResponse(jwt);
  }

  private UiOAuthSettings makeOAuthSettings() {
    var env = Environments.getEnvironment();
    var oAuthConfigs = env.getOAuthConfigurations();
    return new UiOAuthSettings(
        env.getOAuthEnabled().getValueOrDefault(),
        env.getOAuthRedirectUri().getValueOrDefault(),
        oAuthConfigs.stream().map(c -> new OAuthProvider(c.getRegistrationName(), c.getRegistrationId())).toList()
    );
  }

  /**
   * Record which contains information on the configured OAuth providers required by the login page
   * @param enabled indicates if an OAuth provider is configured
   * @param redirectUri the redirect URI
   * @param supportedProviders A list of configured OAuth providers
   */
  private record UiOAuthSettings(boolean enabled,
                                 String redirectUri,
                                 List<OAuthProvider> supportedProviders) {
  }

  private record OAuthProvider(String name, String registrationId) {
  }
}
