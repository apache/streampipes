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

import org.apache.streampipes.commons.security.ServiceAccountSecret;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;

public class ServiceAccountSecretManager {

  private final IUserStorage userStorage;

  public ServiceAccountSecretManager(IUserStorage userStorage) {
    this.userStorage = userStorage;
  }

  public static String readSecret(ServiceAccount account) {
    return account.isSecretEncrypted()
        ? SecretEncryptionManager.decrypt(account.getClientSecret()) : account.getClientSecret();
  }

  public boolean usesLegacyDefault(String username) {
    var account = userStorage.getServiceAccount(username);
    return account != null && ServiceAccountSecret.isLegacyDefault(readSecret(account));
  }

  public void replaceLegacyDefault(String username, String replacement) {
    var account = userStorage.getServiceAccount(username);
    if (account != null && ServiceAccountSecret.isLegacyDefault(readSecret(account))) {
      ServiceAccountSecret.requireValid(replacement, "SP_INITIAL_SERVICE_USER_SECRET");
      account.setClientSecret(SecretEncryptionManager.encrypt(replacement));
      account.setSecretEncrypted(true);
      userStorage.updateUser(account);
    }
  }
}
