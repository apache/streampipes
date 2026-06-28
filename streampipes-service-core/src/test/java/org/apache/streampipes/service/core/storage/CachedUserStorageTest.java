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
package org.apache.streampipes.service.core.storage;

import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.client.user.UserApiToken;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.user.IUserStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CachedUserStorageTest {

  private static final String USERNAME = "user";
  private static final String USER_ID = "user-id";
  private static final String HASHED_TOKEN = "hashed-token";
  private static final String SERVICE_ACCOUNT_NAME = "service";
  private static final String SERVICE_ACCOUNT_ID = "service-id";

  private IUserStorage delegate;
  private CachedUserStorage storage;

  @BeforeEach
  void setUp() {
    delegate = mock(IUserStorage.class);
    var cacheManager = new ConcurrentMapCacheManager(CachedUserStorage.CACHE_NAME);
    storage = new CachedUserStorage(delegate, cacheManager);
  }

  @Test
  void getUserCachesSerializedConcretePrincipalCopy() {
    var user = makeUser("Initial name");
    when(delegate.getUser(USERNAME)).thenReturn(user);

    var firstResult = (UserAccount) storage.getUser(USERNAME);
    firstResult.setFullName("Changed name");
    var secondResult = (UserAccount) storage.getUser(USERNAME);

    assertNotSame(firstResult, secondResult);
    assertEquals("Initial name", secondResult.getFullName());
    verify(delegate, times(1)).getUser(USERNAME);
  }

  @Test
  void getUserAccountPreservesApiTokenHashInCache() throws JsonProcessingException {
    var user = makeUser("User name");
    user.setUserApiTokens(List.of(new UserApiToken("token-id", "token-name", HASHED_TOKEN)));
    when(delegate.getUserAccount(USERNAME)).thenReturn(user);

    var publicJson = JacksonSerializer.getObjectMapper().writeValueAsString(user);
    var firstResult = storage.getUserAccount(USERNAME);
    var secondResult = storage.getUserAccount(USERNAME);

    assertEquals(HASHED_TOKEN, firstResult.getUserApiTokens().get(0).getHashedToken());
    assertEquals(HASHED_TOKEN, secondResult.getUserApiTokens().get(0).getHashedToken());
    assertEquals(false, publicJson.contains(HASHED_TOKEN));
    verify(delegate, times(1)).getUserAccount(USERNAME);
  }

  @Test
  void getUserAccountStoresApiTokenHashInCacheJson() throws JsonProcessingException {
    var user = makeUser("User name");
    user.setUserApiTokens(List.of(new UserApiToken("token-id", "token-name", HASHED_TOKEN)));
    when(delegate.getUserAccount(USERNAME)).thenReturn(user);
    var cacheManager = new ConcurrentMapCacheManager(CachedUserStorage.CACHE_NAME);
    var storage = new CachedUserStorage(delegate, cacheManager);

    var result = storage.getUserAccount(USERNAME);
    var cachedJson = cacheManager
        .getCache(CachedUserStorage.CACHE_NAME)
        .get("user-account:username:user", String.class);
    var cachedDocument = JacksonSerializer.getObjectMapper().readTree(cachedJson);

    assertEquals(HASHED_TOKEN, result.getUserApiTokens().get(0).getHashedToken());
    assertEquals(HASHED_TOKEN, cachedDocument.get("userApiTokens")
                                             .get(0)
                                             .get("hashedToken")
                                             .asText());
  }

  @Test
  void getUserByIdCachesServiceAccount() {
    var serviceAccount = makeServiceAccount();
    when(delegate.getUserById(SERVICE_ACCOUNT_ID)).thenReturn(serviceAccount);

    var firstResult = storage.getUserById(SERVICE_ACCOUNT_ID);
    var secondResult = storage.getUserById(SERVICE_ACCOUNT_ID);

    assertInstanceOf(ServiceAccount.class, firstResult);
    assertInstanceOf(ServiceAccount.class, secondResult);
    assertNotSame(firstResult, secondResult);
    assertEquals(SERVICE_ACCOUNT_NAME, secondResult.getUsername());
    verify(delegate, times(1)).getUserById(SERVICE_ACCOUNT_ID);
  }

  @Test
  void updateUserClearsUserCaches() {
    var user = makeUser("Initial name");
    var updatedUser = makeUser("Updated name");
    when(delegate.getUser(USERNAME)).thenReturn(user, updatedUser);
    when(delegate.checkUserExists(USERNAME)).thenReturn(true, false);

    storage.getUser(USERNAME);
    storage.checkUserExists(USERNAME);
    storage.updateUser(updatedUser);

    var result = (UserAccount) storage.getUser(USERNAME);
    var exists = storage.checkUserExists(USERNAME);

    assertEquals("Updated name", result.getFullName());
    assertEquals(false, exists);
    verify(delegate).updateUser(updatedUser);
    verify(delegate, times(2)).getUser(USERNAME);
    verify(delegate, times(2)).checkUserExists(USERNAME);
  }

  @Test
  void getAllUsersCombinesCachedAccountTypes() {
    var user = makeUser("User name");
    var serviceAccount = makeServiceAccount();
    when(delegate.getAllUserAccounts()).thenReturn(List.of(user));
    when(delegate.getAllServiceAccounts()).thenReturn(List.of(serviceAccount));

    var firstResult = storage.getAllUsers();
    var secondResult = storage.getAllUsers();

    assertEquals(2, firstResult.size());
    assertEquals(2, secondResult.size());
    assertInstanceOf(UserAccount.class, secondResult.get(0));
    assertInstanceOf(ServiceAccount.class, secondResult.get(1));
    verify(delegate, times(1)).getAllUserAccounts();
    verify(delegate, times(1)).getAllServiceAccounts();
  }

  private UserAccount makeUser(String fullName) {
    var user = UserAccount.from(USERNAME, "password", Set.of("ROLE_USER"));
    user.setPrincipalId(USER_ID);
    user.setFullName(fullName);
    return user;
  }

  private ServiceAccount makeServiceAccount() {
    var serviceAccount = ServiceAccount.from(SERVICE_ACCOUNT_NAME, "secret", Set.of("ROLE_SERVICE"));
    serviceAccount.setPrincipalId(SERVICE_ACCOUNT_ID);
    return serviceAccount;
  }
}
