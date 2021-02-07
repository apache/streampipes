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

import com.google.gson.JsonObject;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.streampipes.model.client.user.RawUserApiToken;
import org.apache.streampipes.model.client.user.User;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.util.TokenUtil;
import org.lightcouch.CouchDbClient;

import java.util.List;

public class TokenService {

  public RawUserApiToken createAndStoreNewToken(String email, RawUserApiToken baseToken) {
    User user = getUserStorage().getUser(email);
    RawUserApiToken generatedToken = TokenUtil.createToken(baseToken.getTokenName());
    storeToken(user, generatedToken);
    generatedToken.setHashedToken("");
    return generatedToken;
  }

  private void storeToken(User user, RawUserApiToken generatedToken) {
    user.getUserApiTokens().add(TokenUtil.toUserToken(generatedToken));
    getUserStorage().updateUser(user);
  }

  private IUserStorage getUserStorage() {
    return StorageDispatcher.INSTANCE
            .getNoSqlStore()
            .getUserStorageAPI();
  }

  public User findUserForToken(String token) {
    CouchDbClient dbClient = Utils.getCouchDbUserClient();
    List<JsonObject> users = dbClient.view("users/token").key(token).includeDocs(true).query(JsonObject.class);
    if (users.size() != 1) {
      throw new AuthenticationException("None or too many users with matching token");
    }
    return getUserStorage().getUser(users.get(0).get("email").getAsString());
  }
}
