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

import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.model.client.user.RefreshToken;
import org.apache.streampipes.storage.api.user.IRefreshTokenStorage;
import org.apache.streampipes.user.management.util.TokenUtil;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenServiceTest {

  private final IRefreshTokenStorage storage = mock(IRefreshTokenStorage.class);
  private final RefreshTokenService service = new RefreshTokenService(storage);

  @Test
  void persistsOnlyHashAndKeepsSessionLifetime() {
    when(storage.persist(any())).thenReturn(new Tuple2<>(true, "stored"));

    var issued = service.issueRefreshToken("principal", false);

    var captured = ArgumentCaptor.forClass(RefreshToken.class);
    verify(storage).persist(captured.capture());
    var stored = captured.getValue();
    assertEquals(TokenUtil.hashToken(issued.rawToken()), stored.getHashedToken());
    assertNotEquals(issued.rawToken(), stored.getHashedToken());
    assertEquals("principal", stored.getPrincipalId());
    assertEquals(24L * 60 * 60 * 1000, stored.getExpiresAtMillis() - stored.getCreatedAtMillis());
  }

  @Test
  void rotatesValidTokenAndRevokesPreviousToken() {
    var existing = token();
    when(storage.findByHashedToken(TokenUtil.hashToken("test-token"))).thenReturn(existing);
    when(storage.persist(any())).thenReturn(new Tuple2<>(true, "stored"));

    var replacement = service.rotateRefreshToken("test-token");

    assertNotNull(replacement);
    assertNotNull(existing.getRevokedAtMillis());
    assertEquals(replacement.tokenId(), existing.getReplacedByTokenId());
    verify(storage).updateElement(existing);
  }

  @Test
  void rejectsExpiredAndRevokedTokens() {
    var existing = token();
    existing.setExpiresAtMillis(0);
    when(storage.findByHashedToken(TokenUtil.hashToken("test-token"))).thenReturn(existing);
    assertNull(service.rotateRefreshToken("test-token"));

    existing.setExpiresAtMillis(System.currentTimeMillis() + 60_000);
    existing.setRevokedAtMillis(1L);
    assertNull(service.rotateRefreshToken("test-token"));
    verify(storage, never()).persist(any());
  }

  @Test
  void doesNotReturnTokenWhenPersistenceFails() {
    when(storage.persist(any())).thenReturn(new Tuple2<>(false, "failed"));

    assertThrows(IllegalStateException.class, () -> service.issueRefreshToken("principal", false));
  }

  private RefreshToken token() {
    return RefreshToken.create("id", "principal", TokenUtil.hashToken("test-token"),
        System.currentTimeMillis(), System.currentTimeMillis() + 60_000, false);
  }
}
