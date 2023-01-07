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


package org.apache.streampipes.service.extensions.security;

import org.apache.streampipes.commons.constants.HttpConstants;
import org.apache.streampipes.model.UserInfo;
import org.apache.streampipes.security.jwt.JwtTokenUtils;
import org.apache.streampipes.security.jwt.JwtTokenValidator;
import org.apache.streampipes.security.jwt.PublicKeyResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TokenAuthenticationFilter extends OncePerRequestFilter {


  public TokenAuthenticationFilter() {
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    try {
      String jwt = getJwtFromRequest(request);
      if (StringUtils.hasText(jwt) && JwtTokenValidator.validateJwtToken(jwt, new PublicKeyResolver())) {
        Claims claims = JwtTokenUtils.getClaimsFromToken(jwt, new PublicKeyResolver());
        applySuccessfulAuth(request, claims);
      }
    } catch (Exception ex) {
      logger.error("Could not set user authentication in security context", ex);
    }

    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(HttpConstants.AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HttpConstants.BEARER)) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private void applySuccessfulAuth(HttpServletRequest request,
                                   Claims claims) throws JsonProcessingException {
    UserInfo userInfo = parseUserInfo((Map<String, Object>) claims.get("user"));
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userInfo, null, null);
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private UserInfo parseUserInfo(Map<String, Object> user) {
    UserInfo userInfo = new UserInfo();
    userInfo.setUsername(user.get("username").toString());
    userInfo.setDisplayName(user.get("displayName").toString());
    userInfo.setRoles(new HashSet<>((List<String>) user.get("roles")));

    return userInfo;
  }
}
