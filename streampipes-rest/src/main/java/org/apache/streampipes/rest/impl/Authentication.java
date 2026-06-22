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
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;
import org.apache.streampipes.user.management.service.RefreshTokenService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v2/auth")
public class Authentication extends AbstractRestResource {

  private static final String REFRESH_TOKEN_COOKIE = "sp-refresh-token";
  private static final String ENCODED_REFRESH_TOKEN_PREFIX = "b64.";
  private static final long MIN_REFRESH_COOKIE_SECONDS = 1;

  AuthenticationManager authenticationManager;
  private final SpResourceManager resourceManager;

  public Authentication(AuthenticationManager authenticationManager,
                        SpResourceManager resourceManager) {
    this.authenticationManager = authenticationManager;
    this.resourceManager = resourceManager;
  }

  @PostMapping(
      path = "/login",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
      consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> doLogin(@RequestBody LoginRequest login,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
    try {
      org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(login.username(), login.password()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return processAuth(authentication, login.rememberMe(), request, response);
    } catch (BadCredentialsException e) {
      return unauthorized();
    }
  }

  @PostMapping(
      path = "/token/refresh",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> refreshToken(HttpServletRequest request,
                                        HttpServletResponse response) {
    String existingToken = getRefreshTokenFromRequest(request);

    if (existingToken == null) {
      clearRefreshCookie(request, response);
      return unauthorized();
    }

    var issuedRefreshToken = new RefreshTokenService().rotateRefreshToken(existingToken);

    if (issuedRefreshToken == null) {
      clearRefreshCookie(request, response);
      return unauthorized();
    }

    var principal = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getUserStorageAPI()
        .getUserById(issuedRefreshToken.principalId());

    if (!(principal instanceof UserAccount userAccount)) {
      clearRefreshCookie(request, response);
      return unauthorized();
    }

    setRefreshCookie(request, response, issuedRefreshToken);

    String jwt = new JwtTokenProvider().createToken(userAccount);
    return ok(new JwtAuthenticationResponse(jwt));
  }

  @PostMapping(
      path = "/logout",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> logout(HttpServletRequest request,
                                  HttpServletResponse response) {
    RefreshTokenService refreshTokenService = new RefreshTokenService();
    String existingToken = getRefreshTokenFromRequest(request);

    if (existingToken != null) {
      refreshTokenService.deleteAllRefreshTokensByRawToken(existingToken);
    } else {
      var authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.getPrincipal() instanceof PrincipalUserDetails<?> principal) {
        refreshTokenService.deleteAllRefreshTokens(principal.getDetails().getPrincipalId());
      }
    }

    clearRefreshCookie(request, response);
    SecurityContextHolder.clearContext();

    return ok();
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
      resourceManager.manageUsers().registerUser(enrichedUserRegistrationData);
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
      resourceManager.manageUsers().sendPasswordRecoveryLink(username);
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
    var termsAcknowledgmentRequired = config.getUserAcknowledgment() != null
        && config.getUserAcknowledgment().required();
    Map<String, Object> response = new HashMap<>();
    response.put("allowSelfRegistration", config.isAllowSelfRegistration());
    response.put("allowPasswordRecovery", config.isAllowPasswordRecovery());
    response.put("linkSettings", config.getLinkSettings());
    response.put("oAuthSettings", makeOAuthSettings());
    response.put("termsAcknowledgmentRequired", termsAcknowledgmentRequired);
    if (termsAcknowledgmentRequired) {
      response.put("termsAcknowledgmentTitle", config.getUserAcknowledgment().title());
      response.put("termsAcknowledgmentText", config.getUserAcknowledgment().text());
    }

    return ok(response);
  }

  private ResponseEntity<JwtAuthenticationResponse> processAuth(org.springframework.security.core.Authentication auth,
                                                                boolean rememberMe,
                                                                HttpServletRequest request,
                                                                HttpServletResponse response) {
    Principal principal = ((PrincipalUserDetails<?>) auth.getPrincipal()).getDetails();
    if (principal instanceof UserAccount) {
      JwtAuthenticationResponse tokenResp = makeJwtResponse(auth);
      if (request != null && response != null) {
        var issuedRefreshToken = new RefreshTokenService().issueRefreshToken(principal.getPrincipalId(), rememberMe);
        setRefreshCookie(request, response, issuedRefreshToken);
      }
      ((UserAccount) principal).setLastLoginAtMillis(System.currentTimeMillis());
      resourceManager.manageUsers().updateUser(principal);
      return ok(tokenResp);
    } else {
      throw new BadCredentialsException("Could not create auth token");
    }
  }

  private JwtAuthenticationResponse makeJwtResponse(org.springframework.security.core.Authentication auth) {
    String jwt = new JwtTokenProvider().createToken(auth);
    return new JwtAuthenticationResponse(jwt);
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

  private void clearRefreshCookie(HttpServletRequest request,
                                  HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie
        .from(REFRESH_TOKEN_COOKIE, "")
        .httpOnly(true)
        .secure(isSecureRequest(request))
        .path(refreshCookiePath(request))
        .maxAge(0)
        .sameSite("Lax")
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  private String getRefreshTokenFromRequest(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();

    if (cookies == null) {
      return null;
    }

    for (Cookie cookie : cookies) {
      if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
        return decodeCookieTokenValue(cookie.getValue());
      }
    }

    return null;
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

  private String decodeCookieTokenValue(String cookieValue) {
    if (!cookieValue.startsWith(ENCODED_REFRESH_TOKEN_PREFIX)) {
      return cookieValue;
    }

    try {
      byte[] decoded = Base64.getUrlDecoder().decode(
          cookieValue.substring(ENCODED_REFRESH_TOKEN_PREFIX.length())
      );
      return new String(decoded, StandardCharsets.UTF_8);
    } catch (IllegalArgumentException e) {
      return null;
    }
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
