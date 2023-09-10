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

import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.security.jwt.KeyGenerator;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;

import java.security.Key;

public class SpKeyResolver implements SigningKeyResolver {

  private final String tokenSecret;
  private final IUserStorage userStorage;

  public SpKeyResolver(String tokenSecret) {
    this.tokenSecret = tokenSecret;
    this.userStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI();
  }

  @Override
  public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
    String subject = claims.getSubject();
    Principal principal = getPrincipal(subject);
    if (principal == null) {
      return null;
    } else if (isRealUser(principal)) {
      return new KeyGenerator().makeKeyForSecret(jwsHeader.getAlgorithm(), this.tokenSecret, getPublicKeyFromConfig());
    } else {
      String decryptedSecret = SecretEncryptionManager.decrypt(((ServiceAccount) principal).getClientSecret());
      return new KeyGenerator().makeKeyForSecret(jwsHeader.getAlgorithm(), decryptedSecret, getPublicKeyFromConfig());
    }
  }

  @Override
  public Key resolveSigningKey(JwsHeader jwsHeader, String s) {
    return null;
  }

  private Principal getPrincipal(String username) {
    return userStorage.getUser(username);
  }

  private boolean isRealUser(Principal principal) {
    return principal instanceof UserAccount;
  }

  public String getPublicKeyFromConfig() {
    return StorageDispatcher
        .INSTANCE
        .getNoSqlStore()
        .getSpCoreConfigurationStorage()
        .get()
        .getLocalAuthConfig()
        .getPublicKey();
  }


}
