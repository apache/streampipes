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
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.rest.shared.exception.BadRequestException;
import org.apache.streampipes.service.core.oauth2.util.CookieUtils;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;
import org.apache.streampipes.user.management.service.RefreshTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final String REFRESH_TOKEN_COOKIE = "sp-refresh-token";
  private static final String ENCODED_REFRESH_TOKEN_PREFIX = "b64.";
  private static final long MIN_REFRESH_COOKIE_SECONDS = 1;

  private final JwtTokenProvider tokenProvider;
  private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
  private final Environment env;

  @Autowired
  OAuth2AuthenticationSuccessHandler(HttpCookieOAuth2AuthorizationRequestRepository
                                         httpCookieOAuth2AuthorizationRequestRepository,
                                     ISpCoreConfigurationStorage coreConfigurationStorage,
                                     IRoleStorage roleStorage,
                                     IUserGroupStorage userGroupStorage) {
    this.tokenProvider = new JwtTokenProvider(coreConfigurationStorage, roleStorage, userGroupStorage);
    this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    this.env = Environments.getEnvironment();
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {
    String targetUrl = determineTargetUrl(request, response, authentication);

    if (response.isCommitted()) {
      return;
    }

    clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  @Override
  protected String determineTargetUrl(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) {
    Optional<String> redirectUri = CookieUtils
        .getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue);

    if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
      throw new BadRequestException(
          "Unauthorized redirect uri found - check the redirect uri in your OAuth config"
      );
    }

    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
    boolean rememberMe = CookieUtils
        .getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REMEMBER_ME_PARAM_COOKIE_NAME)
        .map(Cookie::getValue)
        .map(Boolean::parseBoolean)
        .orElse(false);

    Principal principal = ((PrincipalUserDetails<?>) authentication.getPrincipal()).getDetails();
    var refreshToken = new RefreshTokenService().issueRefreshToken(principal.getPrincipalId(), rememberMe);
    setRefreshCookie(request, response, refreshToken);

    String token = tokenProvider.createToken(authentication);

    return targetUrl + "?token=" + token;
  }

  private void setRefreshCookie(HttpServletRequest request,
                                HttpServletResponse response,
                                RefreshTokenService.IssuedRefreshToken issuedRefreshToken) {
    long maxAgeSeconds = TimeUnit.MILLISECONDS.toSeconds(
        Math.max(
            MIN_REFRESH_COOKIE_SECONDS,
            issuedRefreshToken.expiresAtMillis() - System.currentTimeMillis()
        )
    );

    ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie
        .from(REFRESH_TOKEN_COOKIE, encodeCookieTokenValue(issuedRefreshToken.rawToken()))
        .httpOnly(true)
        .secure(isSecureRequest(request))
        .path(refreshCookiePath(request))
        .sameSite("Lax");

    if (issuedRefreshToken.rememberMe()) {
      cookieBuilder.maxAge(maxAgeSeconds);
    }

    response.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());
  }

  private String refreshCookiePath(HttpServletRequest request) {
    var contextPath = request.getContextPath();
    return (contextPath == null ? "" : contextPath) + "/api/v2/auth";
  }

  private boolean isSecureRequest(HttpServletRequest request) {
    String forwardedProto = request.getHeader("X-Forwarded-Proto");
    return request.isSecure() || "https".equalsIgnoreCase(forwardedProto);
  }

  private String encodeCookieTokenValue(String rawToken) {
    return ENCODED_REFRESH_TOKEN_PREFIX + Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));
  }

  protected void clearAuthenticationAttributes(HttpServletRequest request,
                                               HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  private boolean isAuthorizedRedirectUri(String uri) {
    URI clientRedirectUri = URI.create(uri);
    var authorizedRedirectUri = env.getOAuthRedirectUri();
    if (authorizedRedirectUri.exists()) {
      URI authorizedURI = URI.create(authorizedRedirectUri.getValue());
      return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
          && authorizedURI.getPort() == clientRedirectUri.getPort();
    } else {
      return false;
    }
  }
}
