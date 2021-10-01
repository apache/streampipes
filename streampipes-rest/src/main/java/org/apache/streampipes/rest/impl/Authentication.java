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

import org.apache.streampipes.manager.storage.UserManagementService;
import org.apache.streampipes.model.client.user.*;
import org.apache.streampipes.model.message.ErrorMessage;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;
import org.apache.streampipes.user.management.model.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

@Path("/v2/auth")
public class Authentication extends AbstractRestResource {

  @Autowired
  AuthenticationManager authenticationManager;

  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @POST
  @Path("/login")
  public Response doLogin(ShiroAuthenticationRequest token) {
    try {
      org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(token.getUsername(), token.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      JwtAuthenticationResponse tokenResp = makeJwtResponse(authentication);
      return ok(tokenResp);
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
      JwtAuthenticationResponse tokenResp = makeJwtResponse(auth);
      return ok(tokenResp);
    } catch (BadCredentialsException e) {
      return ok(new ErrorMessage(NotificationType.LOGIN_FAILED.uiNotification()));
    }
  }

  @Path("/register")
  @POST
  @GsonWithIds
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response doRegister(RegistrationData data) {

    Set<Role> roles = new HashSet<>();
    roles.add(data.getRole());
    if (getUserStorage().emailExists(data.getEmail())) {
      return ok(Notifications.error("This email address already exists. Please choose another address."));
    } else {
      new UserManagementService().registerUser(data, roles);
      return ok(new SuccessMessage(NotificationType.REGISTRATION_SUCCESS.uiNotification()));
    }
  }

  private JwtAuthenticationResponse makeJwtResponse(org.springframework.security.core.Authentication auth) {
    String jwt = new JwtTokenProvider().createToken(auth);
    LocalUser localUser = (LocalUser) auth.getPrincipal();
    return JwtAuthenticationResponse.from(jwt, toUserInfo(localUser));
  }

  private UserInfo toUserInfo(LocalUser localUser) {
    UserInfo userInfo = new UserInfo();
    userInfo.setUserId("id");
    userInfo.setEmail(localUser.getEmail());
    userInfo.setDisplayName(localUser.getUsername());
    userInfo.setShowTutorial(!localUser.isHideTutorial());
    return userInfo;
  }
}
