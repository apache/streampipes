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

import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.client.user.UserApiToken;
import org.apache.streampipes.storage.api.user.IUserStorage;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenServiceTest {

  private static final String USERNAME = "user";
  private static final String HASHED_TOKEN = "hashed-token";

  @Test
  void hasValidTokenReturnsFalseForTokenWithoutHash() {
    var user = makeUser(new UserApiToken("token-id", "token-name", null));

    var result = new TokenService().hasValidToken(USERNAME, HASHED_TOKEN, new TestUserStorage(user));

    assertFalse(result);
  }

  @Test
  void hasValidTokenMatchesHash() {
    var user = makeUser(new UserApiToken("token-id", "token-name", HASHED_TOKEN));

    var result = new TokenService().hasValidToken(USERNAME, HASHED_TOKEN, new TestUserStorage(user));

    assertTrue(result);
  }

  private UserAccount makeUser(UserApiToken userApiToken) {
    var user = UserAccount.from(USERNAME, "password", Set.of("ROLE_USER"));
    user.setUserApiTokens(List.of(userApiToken));
    return user;
  }

  private static class TestUserStorage implements IUserStorage {

    private final UserAccount user;

    private TestUserStorage(UserAccount user) {
      this.user = user;
    }

    @Override
    public List<Principal> getAllUsers() {
      return List.of(user);
    }

    @Override
    public List<UserAccount> getAllUserAccounts() {
      return List.of(user);
    }

    @Override
    public List<ServiceAccount> getAllServiceAccounts() {
      return List.of();
    }

    @Override
    public Principal getUser(String username) {
      return user;
    }

    @Override
    public UserAccount getUserAccount(String username) {
      return user;
    }

    @Override
    public ServiceAccount getServiceAccount(String username) {
      return null;
    }

    @Override
    public void storeUser(Principal user) {
    }

    @Override
    public void updateUser(Principal user) {
    }

    @Override
    public boolean checkUserExists(String username) {
      return true;
    }

    @Override
    public void deleteUser(String principalId) {
    }

    @Override
    public Principal getUserById(String principalId) {
      return user;
    }

    @Override
    public boolean existsDatabase() {
      return true;
    }
  }
}
