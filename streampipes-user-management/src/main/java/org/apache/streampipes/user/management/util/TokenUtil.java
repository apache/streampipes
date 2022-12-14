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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public class TokenUtil {

  private static final Integer TOKEN_LENGTH = 24;

  public static RawUserApiToken createToken(String tokenName) {
    RawUserApiToken rawToken = new RawUserApiToken();
    rawToken.setTokenId(UUID.randomUUID().toString());
    rawToken.setTokenName(tokenName);
    rawToken.setRawToken(generateToken());
    rawToken.setHashedToken(hashToken(rawToken.getRawToken()));

    return rawToken;
  }

  public static UserApiToken toUserToken(RawUserApiToken rawToken) {
    UserApiToken userApiToken = new UserApiToken();
    userApiToken.setTokenId(rawToken.getTokenId());
    userApiToken.setHashedToken(rawToken.getHashedToken());
    userApiToken.setTokenName(rawToken.getTokenName());

    return userApiToken;
  }

  public static boolean validateToken(String providedToken, String hashedToken) {
    return hashToken(providedToken).equals(hashedToken);
  }

  private static String generateToken() {
    return RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
  }

  public static String hashToken(String token) {
    return DigestUtils.sha256Hex(token);
  }

}
