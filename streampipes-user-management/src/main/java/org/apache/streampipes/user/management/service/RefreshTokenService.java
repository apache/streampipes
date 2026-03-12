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
package org.apache.streampipes.user.management.service;

import org.apache.streampipes.model.client.user.RefreshToken;
import org.apache.streampipes.storage.api.user.IRefreshTokenStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.util.TokenUtil;

import java.util.UUID;

public class RefreshTokenService {

  private static final int REFRESH_TOKEN_LENGTH = 64;
  private static final long SESSION_REFRESH_TOKEN_TTL_MILLIS = 24L * 60 * 60 * 1000;
  private static final long REMEMBER_ME_REFRESH_TOKEN_TTL_MILLIS = 30L * 24 * 60 * 60 * 1000;

  private final IRefreshTokenStorage refreshTokenStorage;

  public RefreshTokenService() {
    this.refreshTokenStorage = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getRefreshTokenStorage();
  }

  public IssuedRefreshToken issueRefreshToken(String principalId,
                                              boolean rememberMe) {
    long createdAtMillis = System.currentTimeMillis();
    long expiresAtMillis = createdAtMillis + getTokenLifetime(rememberMe);

    String rawToken = TokenUtil.generateToken(REFRESH_TOKEN_LENGTH);
    String hashedToken = TokenUtil.hashToken(rawToken);
    String tokenId = UUID.randomUUID().toString();
    RefreshToken refreshToken = RefreshToken.create(
        tokenId,
        principalId,
        hashedToken,
        createdAtMillis,
        expiresAtMillis,
        rememberMe
    );

    persistOrThrow(refreshToken);

    return new IssuedRefreshToken(tokenId, principalId, rawToken, expiresAtMillis, rememberMe);
  }

  public IssuedRefreshToken rotateRefreshToken(String rawToken) {
    String hashedToken = TokenUtil.hashToken(rawToken);
    RefreshToken existingToken = refreshTokenStorage.findByHashedToken(hashedToken);

    if (!isValid(existingToken)) {
      return null;
    }

    if (existingToken.getPrincipalId() == null) {
      return null;
    }

    long now = System.currentTimeMillis();
    IssuedRefreshToken replacement = issueRefreshToken(existingToken.getPrincipalId(), existingToken.isRememberMe());

    existingToken.setRevokedAtMillis(now);
    existingToken.setReplacedByTokenId(replacement.tokenId());
    refreshTokenStorage.updateElement(existingToken);

    return replacement;
  }

  public void deleteAllRefreshTokens(String principalId) {
    refreshTokenStorage
        .findByPrincipalId(principalId)
        .forEach(refreshTokenStorage::deleteElement);
  }

  public void deleteAllRefreshTokensByRawToken(String rawToken) {
    String hashedToken = TokenUtil.hashToken(rawToken);
    RefreshToken existingToken = refreshTokenStorage.findByHashedToken(hashedToken);

    if (existingToken != null && existingToken.getPrincipalId() != null) {
      deleteAllRefreshTokens(existingToken.getPrincipalId());
    }
  }

  private long getTokenLifetime(boolean rememberMe) {
    return rememberMe ? REMEMBER_ME_REFRESH_TOKEN_TTL_MILLIS : SESSION_REFRESH_TOKEN_TTL_MILLIS;
  }

  private boolean isValid(RefreshToken token) {
    if (token == null) {
      return false;
    }

    if (token.getRevokedAtMillis() != null) {
      return false;
    }

    return token.getExpiresAtMillis() > System.currentTimeMillis();
  }

  private void persistOrThrow(RefreshToken refreshToken) {
    var persistResult = refreshTokenStorage.persist(refreshToken);

    if (!persistResult.k) {
      throw new IllegalStateException(
          String.format("Could not persist refresh token for principal '%s'", refreshToken.getPrincipalId())
      );
    }
  }

  public record IssuedRefreshToken(String tokenId,
                                   String principalId,
                                   String rawToken,
                                   long expiresAtMillis,
                                   boolean rememberMe) {
  }
}
