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

import org.apache.streampipes.audit.events.AuthenticationAuditRecorder;
import org.apache.streampipes.audit.events.AuthenticationMethod;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class OAuth2AuditTest {
  private final AuthenticationAuditRecorder audit = mock(AuthenticationAuditRecorder.class);
  private final HttpCookieOAuth2AuthorizationRequestRepository cookies =
      mock(HttpCookieOAuth2AuthorizationRequestRepository.class);
  private final HttpServletRequest request = mock(HttpServletRequest.class);
  private final HttpServletResponse response = mock(HttpServletResponse.class);
  private final Authentication authentication = mock(Authentication.class);
  private final RedirectStrategy redirects = mock(RedirectStrategy.class);

  private OAuth2AuthenticationSuccessHandler successHandler() {
    var handler = spy(new OAuth2AuthenticationSuccessHandler(cookies,
        mock(ISpCoreConfigurationStorage.class), mock(IRoleStorage.class),
        mock(IUserGroupStorage.class), mock(IUserStorage.class), audit));
    doReturn("/").when(handler).determineTargetUrl(request, response, authentication);
    handler.setRedirectStrategy(redirects);
    var user = new UserAccount();
    user.setPrincipalId("user-1");
    var principal = mock(PrincipalUserDetails.class);
    doReturn(user).when(principal).getDetails();
    when(authentication.getPrincipal()).thenReturn(principal);
    return handler;
  }

  @Test
  void successfulRedirectRecordsResolvedActorOnce() throws IOException {
    successHandler().onAuthenticationSuccess(request, response, authentication);
    verify(audit).loggedIn("user-1", AuthenticationMethod.OAUTH2);
    verifyNoMoreInteractions(audit);
  }

  @Test
  void committedResponseDoesNotRecordLogin() throws IOException {
    when(response.isCommitted()).thenReturn(true);
    successHandler().onAuthenticationSuccess(request, response, authentication);
    verifyNoInteractions(audit);
  }

  @Test
  void failedRedirectDoesNotRecordSuccessfulLogin() throws IOException {
    doThrow(new IOException("redirect failed")).when(redirects).sendRedirect(request, response, "/");
    var handler = successHandler();
    assertThrows(IOException.class, () -> handler.onAuthenticationSuccess(request, response, authentication));
    verifyNoInteractions(audit);
  }

  @Test
  void rejectedLoginRecordsOnlyAuthenticationMethod() throws IOException {
    var handler = new OAuth2AuthenticationFailureHandler(cookies, audit);
    handler.setRedirectStrategy(redirects);
    handler.onAuthenticationFailure(request, response, new BadCredentialsException("rejected"));
    verify(audit).loginDenied(AuthenticationMethod.OAUTH2);
    verifyNoMoreInteractions(audit);
  }
}
