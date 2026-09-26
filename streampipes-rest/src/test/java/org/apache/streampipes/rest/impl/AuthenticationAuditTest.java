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

import org.apache.streampipes.audit.events.AuthenticationAuditRecorder;
import org.apache.streampipes.audit.events.AuthenticationMethod;
import org.apache.streampipes.model.client.user.LoginRequest;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.resource.management.UserResourceManager;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;
import org.apache.streampipes.user.management.service.RefreshTokenService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AuthenticationAuditTest {
  private final AuthenticationAuditRecorder audit = mock(AuthenticationAuditRecorder.class);
  private final AuthenticationManager manager = mock(AuthenticationManager.class);
  private final SpResourceManager resources = mock(SpResourceManager.class);
  private final Authentication controller = new Authentication(manager, resources, audit);
  private final HttpServletRequest request = mock(HttpServletRequest.class);
  private final HttpServletResponse response = mock(HttpServletResponse.class);

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  private UsernamePasswordAuthenticationToken authenticated() {
    var user = new UserAccount();
    user.setPrincipalId("user-1");
    var principal = mock(PrincipalUserDetails.class);
    doReturn(user).when(principal).getDetails();
    return new UsernamePasswordAuthenticationToken(principal, null, List.of());
  }

  @Test
  void successfulLoginUsesResolvedPrincipalAndRecordsOnceAfterIssuingTokens() {
    var users = mock(UserResourceManager.class);
    when(resources.manageUsers()).thenReturn(users);
    var authentication = authenticated();
    when(manager.authenticate(any())).thenReturn(authentication);
    try (var jwt = mockConstruction(JwtTokenProvider.class);
         var tokens = mockConstruction(RefreshTokenService.class, (service, context) ->
             when(service.issueRefreshToken(anyString(), anyBoolean())).thenReturn(
                 new RefreshTokenService.IssuedRefreshToken("id", "user-1", "secret", Long.MAX_VALUE, false)))) {
      assertEquals(200, controller.doLogin(new LoginRequest("submitted", "password", false), request, response)
          .getStatusCode().value());
      verify(tokens.constructed().getFirst()).issueRefreshToken("user-1", false);
      verify(audit).loggedIn("user-1", AuthenticationMethod.PASSWORD);
      verifyNoMoreInteractions(audit);
    }
  }

  @Test
  void badCredentialsAreDeniedWithoutRecordingSubmittedIdentity() {
    when(manager.authenticate(any())).thenThrow(new BadCredentialsException("secret"));
    assertEquals(401, controller.doLogin(new LoginRequest("submitted", "password", false), request, response)
        .getStatusCode().value());
    verify(audit).loginDenied(AuthenticationMethod.PASSWORD);
    verifyNoMoreInteractions(audit);
  }

  @Test
  void cookieOnlyLogoutCapturesPrincipalBeforeRevocation() {
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sp-refresh-token", "cookie-token")});
    try (var tokens = mockConstruction(RefreshTokenService.class, (service, context) ->
        when(service.deleteAllRefreshTokensAndGetPrincipalId("cookie-token")).thenReturn("user-1"))) {
      assertEquals(200, controller.logout(request, response).getStatusCode().value());
      verify(audit).loggedOut("user-1");
      verifyNoMoreInteractions(audit);
    }
  }

  @Test
  void authenticatedLogoutWithoutCookieIsRecorded() {
    SecurityContextHolder.getContext().setAuthentication(authenticated());
    try (var tokens = mockConstruction(RefreshTokenService.class)) {
      controller.logout(request, response);
      verify(tokens.constructed().getFirst()).deleteAllRefreshTokens("user-1");
      verify(audit).loggedOut("user-1");
    }
  }

  @Test
  void invalidCookieUsesAuthenticatedContextForLogoutActor() {
    SecurityContextHolder.getContext().setAuthentication(authenticated());
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sp-refresh-token", "invalid")});
    try (var tokens = mockConstruction(RefreshTokenService.class)) {
      controller.logout(request, response);
      verify(audit).loggedOut("user-1");
      verifyNoMoreInteractions(audit);
    }
  }

  @Test
  void successfulRefreshDoesNotRecordAnotherLogin() {
    var users = mock(UserResourceManager.class);
    var storage = mock(IUserStorage.class);
    when(resources.manageUsers()).thenReturn(users);
    when(users.getDb()).thenReturn(storage);
    var user = new UserAccount();
    user.setPrincipalId("user-1");
    when(storage.getUserById("user-1")).thenReturn(user);
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sp-refresh-token", "cookie-token")});
    try (var jwt = mockConstruction(JwtTokenProvider.class);
         var tokens = mockConstruction(RefreshTokenService.class, (service, context) ->
             when(service.rotateRefreshToken("cookie-token")).thenReturn(
                 new RefreshTokenService.IssuedRefreshToken("id", "user-1", "secret", Long.MAX_VALUE, false)))) {
      assertEquals(200, controller.refreshToken(request, response).getStatusCode().value());
      verifyNoInteractions(audit);
    }
  }

  @Test
  void anonymousLogoutAndFailedRefreshDoNotInventSessionEvents() {
    try (var tokens = mockConstruction(RefreshTokenService.class)) {
      controller.logout(request, response);
      controller.refreshToken(request, response);
      verifyNoInteractions(audit);
    }
  }
}
