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

import org.apache.streampipes.commons.security.ServiceAccountSecret;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.configuration.JwtSigningMode;
import org.apache.streampipes.security.jwt.KeyGenerator;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.user.management.service.ServiceAccountSecretManager;
import org.apache.streampipes.user.management.util.PrincipalStatus;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.UnsupportedJwtException;

import java.security.Key;

public class SpKeyResolver implements SigningKeyResolver {

  private final String tokenSecret;
  private final IUserStorage userStorage;
  private final ISpCoreConfigurationStorage coreConfigStorage;

  public SpKeyResolver(String tokenSecret,
                       ISpCoreConfigurationStorage coreConfigurationStorage,
                       IUserStorage userStorage) {
    this.tokenSecret = tokenSecret;
    this.coreConfigStorage = coreConfigurationStorage;
    this.userStorage = userStorage;
  }

  @Override
  public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
    Principal principal = getPrincipal(claims.getSubject());
    if (!PrincipalStatus.canAuthenticate(principal)) {
      throw new UnsupportedJwtException("Principal cannot authenticate");
    }
    var keys = new KeyGenerator();
    var config = coreConfigStorage.get().getLocalAuthConfig();
    String algorithm = jwsHeader.getAlgorithm();
    if (principal instanceof ServiceAccount account) {
      String secret = ServiceAccountSecretManager.readSecret(account);
      if (!ServiceAccountSecret.isValid(secret)) {
        throw new UnsupportedJwtException("Service credential must be replaced");
      }
      // Service clients sign with their own HMAC key, including in RSA deployments.
      if (!"RS256".equals(algorithm)) {
        return keys.makeKeyForSecret(algorithm, secret, null);
      }
    } else if (!(principal instanceof UserAccount)) {
      throw new UnsupportedJwtException("Unsupported principal type");
    }
    if (config.getJwtSigningMode() == JwtSigningMode.HMAC && !"RS256".equals(algorithm)) {
      return keys.makeKeyForSecret(algorithm, tokenSecret, null);
    } else if (config.getJwtSigningMode() == JwtSigningMode.RSA && "RS256".equals(algorithm)) {
      return keys.makeKeyForSecret(algorithm, null, getPublicKeyFromConfig());
    }
    throw new UnsupportedJwtException("JWT algorithm is not allowed for this principal");
  }

  @Override
  public Key resolveSigningKey(JwsHeader jwsHeader, String s) {
    return null;
  }

  private Principal getPrincipal(String username) {
    return username == null || username.isBlank() ? null : userStorage.getUser(username);
  }

  public String getPublicKeyFromConfig() {
    return coreConfigStorage
        .get()
        .getLocalAuthConfig()
        .getPublicKey();
  }


}
