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

import org.apache.streampipes.audit.events.authentication.AuthenticationAuditRecorder;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.RedirectStrategy;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OAuth2AuthenticationFailureHandlerTest {

  @Test
  void errorCodeBecomesPartOfTheHashRoute() {
    assertEquals(
        "http://localhost/#/login?error=oauth_login_failed",
        OAuth2AuthenticationFailureHandler.appendErrorCode("http://localhost/#/login")
    );
  }

  @Test
  void errorCodeIsAppendedToExistingRouteParameters() {
    assertEquals(
        "http://localhost/#/login?returnUrl=home&error=oauth_login_failed",
        OAuth2AuthenticationFailureHandler.appendErrorCode("http://localhost/#/login?returnUrl=home")
    );
  }

  @Test
  void queryBeforeTheHashRouteDoesNotCount() {
    assertEquals(
        "http://localhost/?a=b#/login?error=oauth_login_failed",
        OAuth2AuthenticationFailureHandler.appendErrorCode("http://localhost/?a=b#/login")
    );
  }

  @Test
  void errorCodeIsAppendedToUrlWithoutHashRoute() {
    assertEquals("/?error=oauth_login_failed", OAuth2AuthenticationFailureHandler.appendErrorCode("/"));
    assertEquals("/?a=b&error=oauth_login_failed", OAuth2AuthenticationFailureHandler.appendErrorCode("/?a=b"));
  }

  @Test
  void redirectsToConfiguredOriginOnly() {
    var authorized = "https://sp.example.org";

    assertEquals("https://sp.example.org/#/login",
        OAuth2AuthenticationFailureHandler.targetUrl("https://sp.example.org/#/login", authorized));
    assertEquals("/", OAuth2AuthenticationFailureHandler.targetUrl("https://evil.example/landing", authorized));
    assertEquals("/", OAuth2AuthenticationFailureHandler.targetUrl("//evil.example/landing", authorized));
    assertEquals("/", OAuth2AuthenticationFailureHandler.targetUrl(null, authorized));
    assertEquals("/", OAuth2AuthenticationFailureHandler.targetUrl("https://sp.example.org/#/login", null));
  }

  @Test
  void doesNotRedirectToForeignOriginFromCookie() throws IOException {
    var request = mock(HttpServletRequest.class);
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("redirect_uri", "https://evil.example/landing")});
    var redirects = mock(RedirectStrategy.class);
    var handler = new OAuth2AuthenticationFailureHandler(
        mock(HttpCookieOAuth2AuthorizationRequestRepository.class), mock(AuthenticationAuditRecorder.class));
    handler.setRedirectStrategy(redirects);
    var response = mock(HttpServletResponse.class);

    handler.onAuthenticationFailure(request, response, new BadCredentialsException("rejected"));

    verify(redirects).sendRedirect(request, response, "/?error=oauth_login_failed");
  }

  @Test
  void logMessageContainsNoControlCharacters() {
    assertEquals(
        "failed__INFO forged entry_",
        OAuth2AuthenticationFailureHandler.sanitizeForLog("failed\r\nINFO forged entry\t")
    );
    assertEquals("", OAuth2AuthenticationFailureHandler.sanitizeForLog(null));
  }
}
