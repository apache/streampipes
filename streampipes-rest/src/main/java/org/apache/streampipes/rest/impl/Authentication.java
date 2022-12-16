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

import org.apache.streampipes.commons.exceptions.UserNotFoundException;
import org.apache.streampipes.commons.exceptions.UsernameAlreadyTakenException;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.model.GeneralConfig;
import org.apache.streampipes.model.client.user.JwtAuthenticationResponse;
import org.apache.streampipes.model.client.user.LoginRequest;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.RegistrationData;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.message.ErrorMessage;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("/v2/auth")
public class Authentication extends AbstractRestResource {

  @Autowired
  AuthenticationManager authenticationManager;

  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @POST
  @Path("/login")
  public Response doLogin(LoginRequest token) {
    try {
      org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(token.getUsername(), token.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return processAuth(authentication);
    } catch (BadCredentialsException e) {
      return unauthorized();
    }
  }

  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @GET
  @Path("/token/renew")
  public Response doLogin() {
    try {
      org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      return processAuth(auth);
    } catch (BadCredentialsException e) {
      return ok(new ErrorMessage(NotificationType.LOGIN_FAILED.uiNotification()));
    }
  }

  @Path("/register")
  @POST
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response doRegister(RegistrationData data) {
    GeneralConfig config = BackendConfig.INSTANCE.getGeneralConfig();
    if (!config.isAllowSelfRegistration()) {
      throw new WebApplicationException(Response.Status.FORBIDDEN);
    }
    data.setRoles(config.getDefaultUserRoles());
    try {
      getSpResourceManager().manageUsers().registerUser(data);
      return ok(new SuccessMessage(NotificationType.REGISTRATION_SUCCESS.uiNotification()));
    } catch (UsernameAlreadyTakenException e) {
      return badRequest(Notifications.error("This email address already exists. Please choose another address."));
    }
  }

  @Path("restore/{username}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response sendPasswordRecoveryLink(@PathParam("username") String username) {
    try {
      getSpResourceManager().manageUsers().sendPasswordRecoveryLink(username);
      return ok(new SuccessMessage(NotificationType.PASSWORD_RECOVERY_LINK_SENT.uiNotification()));
    } catch (UserNotFoundException e) {
      return ok();
    } catch (Exception e) {
      return badRequest();
    }
  }

  @Path("settings")
  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAuthSettings() {
    GeneralConfig config = BackendConfig.INSTANCE.getGeneralConfig();
    Map<String, Object> response = new HashMap<>();
    response.put("allowSelfRegistration", config.isAllowSelfRegistration());
    response.put("allowPasswordRecovery", config.isAllowPasswordRecovery());

    return ok(response);
  }

  private Response processAuth(org.springframework.security.core.Authentication auth) {
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
    return JwtAuthenticationResponse.from(jwt);
  }


}
