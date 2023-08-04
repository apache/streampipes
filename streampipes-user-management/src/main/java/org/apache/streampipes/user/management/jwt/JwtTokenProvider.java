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

package org.apache.streampipes.user.management.jwt;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.configuration.JwtSigningMode;
import org.apache.streampipes.model.configuration.LocalAuthConfig;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.security.jwt.JwtTokenGenerator;
import org.apache.streampipes.security.jwt.JwtTokenUtils;
import org.apache.streampipes.security.jwt.JwtTokenValidator;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;
import org.apache.streampipes.user.management.util.GrantedAuthoritiesBuilder;
import org.apache.streampipes.user.management.util.UserInfoUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenProvider {

  public static final String CLAIM_USER = "user";
  private static final Logger LOG = LoggerFactory.getLogger(JwtTokenProvider.class);
  private SpCoreConfiguration config;
  private Environment env;

  public JwtTokenProvider() {
    this.config =  StorageDispatcher
        .INSTANCE
        .getNoSqlStore()
        .getSpCoreConfigurationStorage()
        .get();

    this.env = Environments.getEnvironment();
  }

  public String createToken(Authentication authentication) {
    Principal userPrincipal = ((PrincipalUserDetails<?>) authentication.getPrincipal()).getDetails();
    Set<String> roles = authentication
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());

    return createToken(userPrincipal, roles);

  }

  public String createToken(Principal userPrincipal) {
    Set<String> roles = new GrantedAuthoritiesBuilder(userPrincipal).buildAllAuthorities();
    return createToken(userPrincipal, roles);
  }

  public String createToken(Principal userPrincipal,
                            Set<String> roles) {
    Date tokenExpirationDate = makeExpirationDate();
    Map<String, Object> claims = makeClaims(userPrincipal, roles);

    if (authConfig().getJwtSigningMode() == JwtSigningMode.HMAC) {
      return JwtTokenGenerator.makeJwtToken(userPrincipal.getUsername(), tokenSecret(), claims, tokenExpirationDate);
    } else {
      try {
        return JwtTokenGenerator.makeJwtToken(userPrincipal.getUsername(), getKeyFilePath(), claims,
            tokenExpirationDate);
      } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
        LOG.warn("Could not create JWT token from private key location..defaulting to HMAC");
        return JwtTokenGenerator.makeJwtToken(userPrincipal.getUsername(), tokenSecret(), claims, tokenExpirationDate);
      }
    }
  }

  private Map<String, Object> makeClaims(Principal principal,
                                         Set<String> roles) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(CLAIM_USER, UserInfoUtil.toUserInfoObj(principal, roles));

    return claims;
  }

  public String getUserIdFromToken(String token) {
    return JwtTokenUtils.getUserIdFromToken(token, new SpKeyResolver(tokenSecret()));
  }

  public boolean validateJwtToken(String jwtToken) {
    return JwtTokenValidator.validateJwtToken(jwtToken, new SpKeyResolver(tokenSecret()));
  }

  public boolean validateJwtToken(String tokenSecret,
                                  String jwtToken) {
    return JwtTokenValidator.validateJwtToken(tokenSecret, jwtToken);
  }

  private String tokenSecret() {
    return authConfig().getTokenSecret();
  }

  private Path getKeyFilePath() {
    return Paths.get(env.getJwtPrivateKeyLoc().getValue());
  }

  private LocalAuthConfig authConfig() {
    return this.config.getLocalAuthConfig();
  }

  private Date makeExpirationDate() {
    Date now = new Date();
    return new Date(now.getTime() + authConfig().getTokenExpirationTimeMillis());
  }


}
