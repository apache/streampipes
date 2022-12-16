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

import org.apache.streampipes.model.client.user.RawUserApiToken;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.util.TokenUtil;

public class TokenService {

  public RawUserApiToken createAndStoreNewToken(String email,
                                                RawUserApiToken baseToken) {
    UserAccount user = getUserStorage().getUserAccount(email);
    RawUserApiToken generatedToken = TokenUtil.createToken(baseToken.getTokenName());
    storeToken(user, generatedToken);
    generatedToken.setHashedToken("");
    return generatedToken;
  }

  public boolean hasValidToken(String apiUser,
                               String hashedToken) {
    UserAccount userAccount = getUserStorage().getUserAccount(apiUser);
    if (userAccount == null) {
      return false;
    } else {
      return userAccount
          .getUserApiTokens()
          .stream()
          .anyMatch(t -> t.getHashedToken().equals(hashedToken));
    }
  }

  private void storeToken(UserAccount user, RawUserApiToken generatedToken) {
    user.getUserApiTokens().add(TokenUtil.toUserToken(generatedToken));
    getUserStorage().updateUser(user);
  }

  private IUserStorage getUserStorage() {
    return StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getUserStorageAPI();
  }
}
