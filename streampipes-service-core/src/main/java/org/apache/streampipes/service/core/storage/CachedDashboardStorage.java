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
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Objects;

public class CachedDashboardStorage implements IDashboardStorage {

  static final String CACHE_NAME = "dashboards";
  static final String FIND_ALL_CACHE_NAME = "dashboardsAll";

  private static final Logger LOG = LoggerFactory.getLogger(CachedDashboardStorage.class);
  private static final String FIND_ALL_CACHE_KEY = "all";
  private static final TypeReference<List<DashboardModel>> DASHBOARD_LIST_TYPE = new TypeReference<>() {
  };

  private final IDashboardStorage delegate;
  private final Cache cache;
  private final Cache findAllCache;
  private final ObjectMapper objectMapper;

  public CachedDashboardStorage(IDashboardStorage delegate,
                                CacheManager cacheManager) {
    this(delegate, cacheManager, JacksonSerializer.getObjectMapper());
  }

  CachedDashboardStorage(IDashboardStorage delegate,
                         CacheManager cacheManager,
                         ObjectMapper objectMapper) {
    this.delegate = delegate;
    this.cache = getCache(cacheManager, CACHE_NAME);
    this.findAllCache = getCache(cacheManager, FIND_ALL_CACHE_NAME);
    this.objectMapper = objectMapper;
  }

  @Override
  public List<DashboardModel> findAll() {
    var cachedDashboards = getDashboardList();
    if (cachedDashboards != null) {
      return cachedDashboards;
    }

    var dashboards = delegate.findAll();
    putDashboardList(dashboards);
    return dashboards;
  }

  @Override
  public Tuple2<Boolean, String> persist(DashboardModel element) {
    var result = delegate.persist(element);
    if (Boolean.TRUE.equals(result.k)) {
      clearCaches();
    }
    return result;
  }

  @Override
  public DashboardModel getElementById(String id) {
    var cachedDashboard = getDashboard(id);
    if (cachedDashboard != null) {
      return cachedDashboard;
    }

    var dashboard = delegate.getElementById(id);
    if (dashboard != null) {
      putDashboard(id, dashboard);
    }
    return dashboard;
  }

  @Override
  public DashboardModel updateElement(DashboardModel element) {
    var updatedElement = delegate.updateElement(element);
    clearCaches();
    return updatedElement;
  }

  @Override
  public void deleteElement(DashboardModel element) {
    delegate.deleteElement(element);
    clearCaches();
  }

  @Override
  public void deleteElementById(String id) {
    delegate.deleteElementById(id);
    clearCaches();
  }

  private DashboardModel getDashboard(String id) {
    try {
      var cachedValue = cache.get(id, String.class);
      return cachedValue == null ? null : objectMapper.readValue(cachedValue, DashboardModel.class);
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not read dashboard {} from cache", id, e);
      evict(cache, id);
      return null;
    }
  }

  private List<DashboardModel> getDashboardList() {
    try {
      var cachedValue = findAllCache.get(FIND_ALL_CACHE_KEY, String.class);
      return cachedValue == null ? null : objectMapper.readValue(cachedValue, DASHBOARD_LIST_TYPE);
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not read dashboards from cache", e);
      evict(findAllCache, FIND_ALL_CACHE_KEY);
      return null;
    }
  }

  private void putDashboard(String id,
                            DashboardModel dashboard) {
    try {
      cache.put(
          id,
          objectMapper.writerFor(DashboardModel.class).writeValueAsString(dashboard)
      );
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not write dashboard {} to cache", id, e);
    }
  }

  private void putDashboardList(List<DashboardModel> dashboards) {
    try {
      findAllCache.put(
          FIND_ALL_CACHE_KEY,
          objectMapper.writerFor(DASHBOARD_LIST_TYPE).writeValueAsString(dashboards)
      );
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not write dashboards to cache", e);
    }
  }

  private void clearCaches() {
    clear(cache);
    clear(findAllCache);
  }

  private void clear(Cache targetCache) {
    try {
      targetCache.clear();
    } catch (RuntimeException e) {
      LOG.warn("Could not clear dashboard cache {}", targetCache.getName(), e);
    }
  }

  private void evict(Cache targetCache,
                     String key) {
    try {
      targetCache.evict(key);
    } catch (RuntimeException e) {
      LOG.warn("Could not evict dashboard cache entry from {}", targetCache.getName(), e);
    }
  }

  private static Cache getCache(CacheManager cacheManager,
                                String cacheName) {
    return Objects.requireNonNull(cacheManager.getCache(cacheName));
  }
}
