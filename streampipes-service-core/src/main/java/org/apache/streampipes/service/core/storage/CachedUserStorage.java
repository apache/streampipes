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

import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.client.user.UserApiToken;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.user.IUserStorage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class CachedUserStorage implements IUserStorage {

  static final String CACHE_NAME = "users";

  private static final String ALL_USER_ACCOUNTS_KEY = "query:all-user-accounts";
  private static final String ALL_SERVICE_ACCOUNTS_KEY = "query:all-service-accounts";
  private static final String USER_ACCOUNT_USERNAME_KEY_PREFIX = "user-account:username:";
  private static final String SERVICE_ACCOUNT_USERNAME_KEY_PREFIX = "service-account:username:";
  private static final String USER_ACCOUNT_ID_KEY_PREFIX = "user-account:id:";
  private static final String SERVICE_ACCOUNT_ID_KEY_PREFIX = "service-account:id:";
  private static final String EXISTS_USERNAME_KEY_PREFIX = "exists:username:";

  private final IUserStorage delegate;
  private final SerializedCache cache;
  private final JavaType userAccountType;
  private final JavaType serviceAccountType;
  private final JavaType userAccountListType;
  private final JavaType serviceAccountListType;
  private final JavaType booleanType;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

  public CachedUserStorage(IUserStorage delegate,
                           CacheManager cacheManager) {
    this(delegate, cacheManager, makeCacheObjectMapper());
  }

  CachedUserStorage(IUserStorage delegate,
                    CacheManager cacheManager,
                    ObjectMapper objectMapper) {
    this.delegate = delegate;
    this.cache = new SerializedCache(
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME)),
        objectMapper
    );
    TypeFactory typeFactory = objectMapper.getTypeFactory();
    this.userAccountType = typeFactory.constructType(UserAccount.class);
    this.serviceAccountType = typeFactory.constructType(ServiceAccount.class);
    this.userAccountListType = typeFactory.constructCollectionType(List.class, UserAccount.class);
    this.serviceAccountListType = typeFactory.constructCollectionType(List.class, ServiceAccount.class);
    this.booleanType = typeFactory.constructType(Boolean.class);
  }

  @Override
  public List<Principal> getAllUsers() {
    var allUsers = new ArrayList<Principal>();
    allUsers.addAll(getAllUserAccounts());
    allUsers.addAll(getAllServiceAccounts());
    return allUsers;
  }

  @Override
  public List<UserAccount> getAllUserAccounts() {
    return getOrLoad(ALL_USER_ACCOUNTS_KEY, userAccountListType, delegate::getAllUserAccounts);
  }

  @Override
  public List<ServiceAccount> getAllServiceAccounts() {
    return getOrLoad(ALL_SERVICE_ACCOUNTS_KEY, serviceAccountListType, delegate::getAllServiceAccounts);
  }

  @Override
  public Principal getUser(String username) {
    var userAccount = getCachedUserAccount(username);
    if (userAccount != null) {
      return userAccount;
    }

    var serviceAccount = getCachedServiceAccount(username);
    if (serviceAccount != null) {
      return serviceAccount;
    }

    var user = delegate.getUser(username);
    cacheUser(user);
    return user;
  }

  @Override
  public UserAccount getUserAccount(String username) {
    return getOrLoad(
        key(USER_ACCOUNT_USERNAME_KEY_PREFIX, normalize(username)),
        userAccountType,
        () -> delegate.getUserAccount(username)
    );
  }

  @Override
  public ServiceAccount getServiceAccount(String username) {
    return getOrLoad(
        key(SERVICE_ACCOUNT_USERNAME_KEY_PREFIX, normalize(username)),
        serviceAccountType,
        () -> delegate.getServiceAccount(username)
    );
  }

  @Override
  public void storeUser(Principal user) {
    var writeLock = lock.writeLock();
    writeLock.lock();
    try {
      delegate.storeUser(user);
      cache.clear();
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public void updateUser(Principal user) {
    var writeLock = lock.writeLock();
    writeLock.lock();
    try {
      delegate.updateUser(user);
      cache.clear();
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public boolean checkUserExists(String username) {
    return getOrLoad(
        key(EXISTS_USERNAME_KEY_PREFIX, normalize(username)),
        booleanType,
        () -> delegate.checkUserExists(username)
    );
  }

  @Override
  public void deleteUser(String principalId) {
    var writeLock = lock.writeLock();
    writeLock.lock();
    try {
      delegate.deleteUser(principalId);
      cache.clear();
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public Principal getUserById(String principalId) {
    UserAccount userAccount = getOrLoad(
        key(USER_ACCOUNT_ID_KEY_PREFIX, principalId),
        userAccountType,
        () -> null
    );
    if (userAccount != null) {
      return userAccount;
    }

    ServiceAccount serviceAccount = getOrLoad(
        key(SERVICE_ACCOUNT_ID_KEY_PREFIX, principalId),
        serviceAccountType,
        () -> null
    );
    if (serviceAccount != null) {
      return serviceAccount;
    }

    var user = delegate.getUserById(principalId);
    cacheUser(user);
    return user;
  }

  @Override
  public boolean existsDatabase() {
    return delegate.existsDatabase();
  }

  private UserAccount getCachedUserAccount(String username) {
    return getOrLoad(
        key(USER_ACCOUNT_USERNAME_KEY_PREFIX, normalize(username)),
        userAccountType,
        () -> null
    );
  }

  private ServiceAccount getCachedServiceAccount(String username) {
    return getOrLoad(
        key(SERVICE_ACCOUNT_USERNAME_KEY_PREFIX, normalize(username)),
        serviceAccountType,
        () -> null
    );
  }

  private void cacheUser(Principal user) {
    if (user instanceof UserAccount userAccount) {
      cache.put(
          key(USER_ACCOUNT_USERNAME_KEY_PREFIX, normalize(userAccount.getUsername())),
          userAccount,
          userAccountType
      );
      cache.put(key(USER_ACCOUNT_ID_KEY_PREFIX, userAccount.getPrincipalId()), userAccount, userAccountType);
    } else if (user instanceof ServiceAccount serviceAccount) {
      cache.put(
          key(SERVICE_ACCOUNT_USERNAME_KEY_PREFIX, normalize(serviceAccount.getUsername())),
          serviceAccount,
          serviceAccountType
      );
      cache.put(
          key(SERVICE_ACCOUNT_ID_KEY_PREFIX, serviceAccount.getPrincipalId()),
          serviceAccount,
          serviceAccountType
      );
    }
  }

  private <ResultT> ResultT getOrLoad(String key,
                                      JavaType type,
                                      Supplier<ResultT> loader) {
    var readLock = lock.readLock();
    readLock.lock();
    try {
      var cachedValue = cache.<ResultT>get(key, type);
      if (cachedValue != null) {
        return cachedValue;
      }

      var value = loader.get();
      cache.put(key, value, type);
      return value;
    } finally {
      readLock.unlock();
    }
  }

  private String normalize(String value) {
    return value == null ? null : value.toLowerCase(Locale.ROOT);
  }

  private String key(String prefix,
                     String value) {
    return value == null ? null : prefix + value;
  }

  private static ObjectMapper makeCacheObjectMapper() {
    var objectMapper = JacksonSerializer.getObjectMapper();
    objectMapper.addMixIn(UserApiToken.class, CachedUserApiTokenMixin.class);
    return objectMapper;
  }

  private abstract static class CachedUserApiTokenMixin {

    @JsonIgnore(false)
    abstract String getHashedToken();
  }
}
