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

import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.user.IPermissionStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CachedPermissionStorage implements IPermissionStorage {

  static final String CACHE_NAME = "permissions";
  static final String FIND_ALL_CACHE_NAME = "permissionsAll";
  static final String BY_OBJECT_CACHE_NAME = "permissionsByObject";
  static final String BY_PRINCIPALS_CACHE_NAME = "objectPermissionsByPrincipals";

  private static final Logger LOG = LoggerFactory.getLogger(CachedPermissionStorage.class);
  private static final String FIND_ALL_CACHE_KEY = "all";
  private static final TypeReference<List<Permission>> PERMISSION_LIST_TYPE = new TypeReference<>() {
  };
  private static final TypeReference<Set<String>> OBJECT_PERMISSION_SET_TYPE = new TypeReference<>() {
  };

  private final IPermissionStorage delegate;
  private final Cache cache;
  private final Cache findAllCache;
  private final Cache byObjectCache;
  private final Cache byPrincipalsCache;
  private final ObjectMapper objectMapper;

  public CachedPermissionStorage(IPermissionStorage delegate,
                                 CacheManager cacheManager) {
    this(delegate, cacheManager, JacksonSerializer.getObjectMapper());
  }

  CachedPermissionStorage(IPermissionStorage delegate,
                          CacheManager cacheManager,
                          ObjectMapper objectMapper) {
    this.delegate = delegate;
    this.cache = getCache(cacheManager, CACHE_NAME);
    this.findAllCache = getCache(cacheManager, FIND_ALL_CACHE_NAME);
    this.byObjectCache = getCache(cacheManager, BY_OBJECT_CACHE_NAME);
    this.byPrincipalsCache = getCache(cacheManager, BY_PRINCIPALS_CACHE_NAME);
    this.objectMapper = objectMapper;
  }

  @Override
  public List<Permission> findAll() {
    var cachedPermissions = getPermissionList(findAllCache, FIND_ALL_CACHE_KEY);
    if (cachedPermissions != null) {
      return cachedPermissions;
    }

    var permissions = delegate.findAll();
    put(findAllCache, FIND_ALL_CACHE_KEY, permissions);
    return permissions;
  }

  @Override
  public Tuple2<Boolean, String> persist(Permission element) {
    var result = delegate.persist(element);
    if (Boolean.TRUE.equals(result.k)) {
      clearCaches();
    }
    return result;
  }

  @Override
  public Permission getElementById(String id) {
    var cachedPermission = getPermission(id);
    if (cachedPermission != null) {
      return cachedPermission;
    }

    var permission = delegate.getElementById(id);
    if (permission != null) {
      put(cache, id, permission);
    }
    return permission;
  }

  @Override
  public Permission updateElement(Permission element) {
    var updatedElement = delegate.updateElement(element);
    clearCaches();
    return updatedElement;
  }

  @Override
  public void deleteElement(Permission element) {
    delegate.deleteElement(element);
    clearCaches();
  }

  @Override
  public void deleteElementById(String id) {
    delegate.deleteElementById(id);
    clearCaches();
  }

  @Override
  public Set<String> getObjectPermissions(List<String> sids) {
    var cacheKey = makePrincipalCacheKey(sids);
    var cachedPermissions = getObjectPermissionSet(cacheKey);
    if (cachedPermissions != null) {
      return cachedPermissions;
    }

    var permissions = delegate.getObjectPermissions(sids);
    put(byPrincipalsCache, cacheKey, permissions);
    return permissions;
  }

  @Override
  public List<Permission> getUserPermissionsForObject(String objectInstanceId) {
    var cachedPermissions = getPermissionList(byObjectCache, objectInstanceId);
    if (cachedPermissions != null) {
      return cachedPermissions;
    }

    var permissions = delegate.getUserPermissionsForObject(objectInstanceId);
    put(byObjectCache, objectInstanceId, permissions);
    return permissions;
  }

  private Permission getPermission(String id) {
    try {
      var cachedValue = cache.get(id, String.class);
      return cachedValue == null ? null : objectMapper.readValue(cachedValue, Permission.class);
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not read permission {} from cache", id, e);
      evict(cache, id);
      return null;
    }
  }

  private List<Permission> getPermissionList(Cache targetCache,
                                             String key) {
    try {
      var cachedValue = targetCache.get(key, String.class);
      return cachedValue == null ? null : objectMapper.readValue(cachedValue, PERMISSION_LIST_TYPE);
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not read permissions from cache {}", targetCache.getName(), e);
      evict(targetCache, key);
      return null;
    }
  }

  private Set<String> getObjectPermissionSet(String key) {
    try {
      var cachedValue = byPrincipalsCache.get(key, String.class);
      return cachedValue == null ? null : objectMapper.readValue(cachedValue, OBJECT_PERMISSION_SET_TYPE);
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not read object permissions from cache", e);
      evict(byPrincipalsCache, key);
      return null;
    }
  }

  private String makePrincipalCacheKey(List<String> sids) {
    var sortedSids = new ArrayList<>(sids);
    sortedSids.sort(String::compareTo);
    try {
      return objectMapper.writeValueAsString(sortedSids);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Could not create permission cache key", e);
    }
  }

  private void put(Cache targetCache,
                   String key,
                   Object value) {
    try {
      targetCache.put(key, objectMapper.writeValueAsString(value));
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not write permissions to cache {}", targetCache.getName(), e);
    }
  }

  private void clearCaches() {
    clear(cache);
    clear(findAllCache);
    clear(byObjectCache);
    clear(byPrincipalsCache);
  }

  private void clear(Cache targetCache) {
    try {
      targetCache.clear();
    } catch (RuntimeException e) {
      LOG.warn("Could not clear permission cache {}", targetCache.getName(), e);
    }
  }

  private void evict(Cache targetCache,
                     String key) {
    try {
      targetCache.evict(key);
    } catch (RuntimeException e) {
      LOG.warn("Could not evict permission cache entry from {}", targetCache.getName(), e);
    }
  }

  private static Cache getCache(CacheManager cacheManager,
                                String cacheName) {
    return Objects.requireNonNull(cacheManager.getCache(cacheName));
  }
}
