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

package org.apache.streampipes.service.core;

import org.apache.streampipes.model.client.user.RefreshToken;
import org.apache.streampipes.storage.api.user.IRefreshTokenStorage;
import org.apache.streampipes.user.management.service.RefreshTokenService;
import org.apache.streampipes.user.management.util.TokenUtil;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenAuditActorTest {
  private final IRefreshTokenStorage storage = mock(IRefreshTokenStorage.class);
  private final RefreshTokenService service = new RefreshTokenService(storage);

  private RefreshToken token(long expiresAt) {
    var token = RefreshToken.create("token-id", "user-1", TokenUtil.hashToken("raw"), 0, expiresAt, false);
    when(storage.findByHashedToken(TokenUtil.hashToken("raw"))).thenReturn(token);
    when(storage.findByPrincipalId("user-1")).thenReturn(List.of(token));
    return token;
  }

  @Test
  void validTokenProvidesActorAndRevokesTokens() {
    var token = token(Long.MAX_VALUE);
    assertEquals("user-1", service.deleteAllRefreshTokensAndGetPrincipalId("raw"));
    verify(storage).deleteElement(token);
  }

  @Test
  void expiredTokenStillRevokesButDoesNotAuthenticateActor() {
    var token = token(1);
    assertNull(service.deleteAllRefreshTokensAndGetPrincipalId("raw"));
    verify(storage).deleteElement(token);
  }

  @Test
  void revokedTokenStillRevokesButDoesNotAuthenticateActor() {
    var token = token(Long.MAX_VALUE);
    token.setRevokedAtMillis(1L);
    assertNull(service.deleteAllRefreshTokensAndGetPrincipalId("raw"));
    verify(storage).deleteElement(token);
  }

  @Test
  void unknownTokenDoesNotAuthenticateActor() {
    assertNull(service.deleteAllRefreshTokensAndGetPrincipalId("raw"));
  }
}
