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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class CachedAdapterStorage implements IAdapterStorage {

  static final String CACHE_NAME = "adapters";
  static final String FIND_ALL_CACHE_NAME = "adaptersAll";
  static final String BY_APP_ID_CACHE_NAME = "adaptersByAppId";

  private static final Logger LOG = LoggerFactory.getLogger(CachedAdapterStorage.class);
  private static final String FIND_ALL_CACHE_KEY = "all";
  private static final TypeReference<List<AdapterDescription>> ADAPTER_LIST_TYPE = new TypeReference<>() {
  };

  private final IAdapterStorage delegate;
  private final Cache cache;
  private final Cache findAllCache;
  private final Cache byAppIdCache;
  private final ObjectMapper objectMapper;

  public CachedAdapterStorage(IAdapterStorage delegate,
                              CacheManager cacheManager) {
    this(delegate, cacheManager, JacksonSerializer.getObjectMapper());
  }

  CachedAdapterStorage(IAdapterStorage delegate,
                       CacheManager cacheManager,
                       ObjectMapper objectMapper) {
    this.delegate = delegate;
    this.cache = getCache(cacheManager, CACHE_NAME);
    this.findAllCache = getCache(cacheManager, FIND_ALL_CACHE_NAME);
    this.byAppIdCache = getCache(cacheManager, BY_APP_ID_CACHE_NAME);
    this.objectMapper = objectMapper;
  }

  @Override
  public List<AdapterDescription> findAll() {
    var cachedAdapters = getAdapterList(findAllCache, FIND_ALL_CACHE_KEY);
    if (cachedAdapters != null) {
      return cachedAdapters;
    }

    var adapters = delegate.findAll();
    putAdapterList(findAllCache, FIND_ALL_CACHE_KEY, adapters);
    return adapters;
  }

  @Override
  public Tuple2<Boolean, String> persist(AdapterDescription element) {
    var result = delegate.persist(element);
    if (Boolean.TRUE.equals(result.k)) {
      clearCaches();
    }
    return result;
  }

  @Override
  public AdapterDescription getElementById(String id) {
    var cachedAdapter = getAdapter(id);
    if (cachedAdapter != null) {
      return cachedAdapter;
    }

    var adapter = delegate.getElementById(id);
    if (adapter != null) {
      putAdapter(cache, id, adapter);
    }
    return adapter;
  }

  @Override
  public AdapterDescription updateElement(AdapterDescription element) {
    var updatedElement = delegate.updateElement(element);
    clearCaches();
    return updatedElement;
  }

  @Override
  public void deleteElement(AdapterDescription element) {
    delegate.deleteElement(element);
    clearCaches();
  }

  @Override
  public void deleteElementById(String id) {
    delegate.deleteElementById(id);
    clearCaches();
  }

  @Override
  public AdapterDescription getFirstAdapterByAppId(String appId) {
    return getAdaptersByAppId(appId)
        .stream()
        .findFirst()
        .orElseThrow(NoSuchElementException::new);
  }

  @Override
  public List<AdapterDescription> getAdaptersByAppId(String appId) {
    var cachedAdapters = getAdapterList(byAppIdCache, appId);
    if (cachedAdapters != null) {
      return cachedAdapters;
    }

    var adapters = delegate.getAdaptersByAppId(appId);
    putAdapterList(byAppIdCache, appId, adapters);
    return adapters;
  }

  private AdapterDescription getAdapter(String id) {
    try {
      var cachedValue = cache.get(id, String.class);
      return cachedValue == null ? null : objectMapper.readValue(cachedValue, AdapterDescription.class);
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not read adapter {} from cache", id, e);
      evict(cache, id);
      return null;
    }
  }

  private List<AdapterDescription> getAdapterList(Cache targetCache,
                                                  String key) {
    try {
      var cachedValue = targetCache.get(key, String.class);
      return cachedValue == null ? null : objectMapper.readValue(cachedValue, ADAPTER_LIST_TYPE);
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not read adapters from cache {}", targetCache.getName(), e);
      evict(targetCache, key);
      return null;
    }
  }

  private void putAdapter(Cache targetCache,
                          String key,
                          AdapterDescription adapter) {
    try {
      targetCache.put(
          key,
          objectMapper.writerFor(AdapterDescription.class).writeValueAsString(adapter)
      );
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not write adapter to cache {}", targetCache.getName(), e);
    }
  }

  private void putAdapterList(Cache targetCache,
                              String key,
                              List<AdapterDescription> adapters) {
    try {
      targetCache.put(
          key,
          objectMapper.writerFor(ADAPTER_LIST_TYPE).writeValueAsString(adapters)
      );
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not write adapters to cache {}", targetCache.getName(), e);
    }
  }

  private void clearCaches() {
    clear(cache);
    clear(findAllCache);
    clear(byAppIdCache);
  }

  private void clear(Cache targetCache) {
    try {
      targetCache.clear();
    } catch (RuntimeException e) {
      LOG.warn("Could not clear adapter cache {}", targetCache.getName(), e);
    }
  }

  private void evict(Cache targetCache,
                     String key) {
    try {
      targetCache.evict(key);
    } catch (RuntimeException e) {
      LOG.warn("Could not evict adapter cache entry from {}", targetCache.getName(), e);
    }
  }

  private static Cache getCache(CacheManager cacheManager,
                                String cacheName) {
    return Objects.requireNonNull(cacheManager.getCache(cacheName));
  }
}
