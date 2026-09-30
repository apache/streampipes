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

import org.apache.streampipes.service.core.oauth2.util.CookieUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.apache.streampipes.service.core.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;


@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  /**
   * The only failure information sent to the client. The reason of a failed login is written to
   * the log and must not be exposed, as it can reveal which accounts exist.
   */
  static final String ERROR_CODE = "oauth_login_failed";

  private static final Logger LOG = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);
  private static final String ERROR_PARAM = "error";

  @Autowired
  HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
                                      HttpServletResponse response,
                                      AuthenticationException exception) throws IOException {
    String targetUrl = CookieUtils
        .getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue)
        .orElse(("/"));

    LOG.warn("OAuth login failed: {}", sanitizeForLog(exception.getMessage()));

    httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

    getRedirectStrategy().sendRedirect(request, response, appendErrorCode(targetUrl));
  }

  /**
   * Appends the error code to the end of the url, so that it becomes part of the route when the
   * redirect uri points to a hash route of the UI (e.g. {@code http://host/#/login}).
   */
  static String appendErrorCode(String targetUrl) {
    var route = targetUrl.substring(targetUrl.indexOf('#') + 1);
    var separator = route.contains("?") ? "&" : "?";
    return targetUrl + separator + ERROR_PARAM + "=" + ERROR_CODE;
  }

  /**
   * Failure messages can contain values received from the identity provider.
   */
  static String sanitizeForLog(String message) {
    return message == null ? "" : message.replaceAll("\\p{Cntrl}", "_");
  }
}
