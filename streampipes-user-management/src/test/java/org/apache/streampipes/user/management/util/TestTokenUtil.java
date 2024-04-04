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
package org.apache.streampipes.user.management.util;

import org.apache.streampipes.model.client.user.RawUserApiToken;
import org.apache.streampipes.model.client.user.UserApiToken;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestTokenUtil {

  @Test
  public void testCreateToken() {
    String tokenName = "test-token";

    RawUserApiToken rawToken = TokenUtil.createToken(tokenName);
    Assertions.assertEquals("test-token", rawToken.getTokenName());
    Assertions.assertNotNull(rawToken.getTokenId());
    Assertions.assertNotNull(rawToken.getRawToken());
    Assertions.assertNotNull(rawToken.getHashedToken());
  }

  @Test
  public void testToUserToken() {
    RawUserApiToken rawToken = TokenUtil.createToken("test-token");
    UserApiToken token = TokenUtil.toUserToken(rawToken);

    Assertions.assertEquals(rawToken.getHashedToken(), token.getHashedToken());
    Assertions.assertEquals(rawToken.getTokenId(), token.getTokenId());
    Assertions.assertEquals(rawToken.getTokenName(), token.getTokenName());
  }

  @Test
  public void testTokenValidation() {
    RawUserApiToken rawToken = TokenUtil.createToken("test-token");
    String rawApiKey = rawToken.getRawToken();

    Assertions.assertTrue(TokenUtil.validateToken(rawApiKey, rawToken.getHashedToken()));
  }

}
