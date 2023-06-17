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

package org.apache.streampipes.service.core.filter;

import org.apache.streampipes.commons.constants.HttpConstants;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;
import org.apache.streampipes.user.management.model.ServiceAccountDetails;
import org.apache.streampipes.user.management.model.UserAccountDetails;
import org.apache.streampipes.user.management.service.TokenService;
import org.apache.streampipes.user.management.util.TokenUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final IUserStorage userStorage;

  private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

  public TokenAuthenticationFilter() {
    this.tokenProvider = new JwtTokenProvider();
    this.userStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI();
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    try {
      String jwt = getJwtFromRequest(request);

      if (StringUtils.hasText(jwt) && tokenProvider.validateJwtToken(jwt)) {
        String username = tokenProvider.getUserIdFromToken(jwt);
        applySuccessfulAuth(request, username);
      } else {
        String apiKey = getApiKeyFromRequest(request);
        String apiUser = getApiUserFromRequest(request);
        if (StringUtils.hasText(apiKey) && StringUtils.hasText(apiUser)) {
          String hashedToken = TokenUtil.hashToken(apiKey);
          boolean hasValidToken = new TokenService().hasValidToken(apiUser, hashedToken);
          if (hasValidToken) {
            applySuccessfulAuth(request, apiUser);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Could not set user authentication in security context", ex);
    }

    filterChain.doFilter(request, response);
  }

  private void applySuccessfulAuth(HttpServletRequest request,
                                   String username) {
    Principal user = userStorage.getUser(username);
    PrincipalUserDetails<?> userDetails = user instanceof UserAccount ? new UserAccountDetails((UserAccount) user) :
        new ServiceAccountDetails((ServiceAccount) user);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }


  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(HttpConstants.AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HttpConstants.BEARER)) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private String getApiKeyFromRequest(HttpServletRequest request) {
    return request.getHeader(HttpConstants.X_API_KEY);
  }

  private String getApiUserFromRequest(HttpServletRequest request) {
    return request.getHeader(HttpConstants.X_API_USER);
  }
}
