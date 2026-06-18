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
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.explorer.IChartStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Objects;

public class CachedChartStorage implements IChartStorage {

  static final String CACHE_NAME = "dataExplorerWidgets";
  static final String FIND_ALL_CACHE_NAME = "dataExplorerWidgetsAll";

  private static final Logger LOG = LoggerFactory.getLogger(CachedChartStorage.class);
  private static final String FIND_ALL_CACHE_KEY = "all";
  private static final TypeReference<List<DataExplorerWidgetModel>> WIDGET_LIST_TYPE = new TypeReference<>() {
  };

  private final IChartStorage delegate;
  private final Cache cache;
  private final Cache findAllCache;
  private final ObjectMapper objectMapper;

  public CachedChartStorage(IChartStorage delegate,
                            CacheManager cacheManager) {
    this(delegate, cacheManager, JacksonSerializer.getObjectMapper());
  }

  CachedChartStorage(IChartStorage delegate,
                     CacheManager cacheManager,
                     ObjectMapper objectMapper) {
    this.delegate = delegate;
    this.cache = Objects.requireNonNull(cacheManager.getCache(CACHE_NAME));
    this.findAllCache = Objects.requireNonNull(cacheManager.getCache(FIND_ALL_CACHE_NAME));
    this.objectMapper = objectMapper;
  }

  @Override
  public List<DataExplorerWidgetModel> findAll() {
    var cachedElements = getCachedElements();
    if (cachedElements != null) {
      return cachedElements;
    }

    var elements = delegate.findAll();
    put(elements);
    return elements;
  }

  @Override
  public Tuple2<Boolean, String> persist(DataExplorerWidgetModel element) {
    var result = delegate.persist(element);
    if (Boolean.TRUE.equals(result.k)) {
      evict(element.getElementId());
      evict(result.v);
      evictFindAll();
    }
    return result;
  }

  @Override
  public DataExplorerWidgetModel getElementById(String id) {
    var cachedElement = getCachedElement(id);
    if (cachedElement != null) {
      return cachedElement;
    }

    var element = delegate.getElementById(id);
    if (element != null) {
      put(element);
    }
    return element;
  }

  @Override
  public DataExplorerWidgetModel updateElement(DataExplorerWidgetModel element) {
    var updatedElement = delegate.updateElement(element);
    evict(element.getElementId());
    if (updatedElement != null) {
      evict(updatedElement.getElementId());
    }
    evictFindAll();
    return updatedElement;
  }

  @Override
  public void deleteElement(DataExplorerWidgetModel element) {
    delegate.deleteElement(element);
    evict(element.getElementId());
    evictFindAll();
  }

  @Override
  public void deleteElementById(String id) {
    delegate.deleteElementById(id);
    evict(id);
    evictFindAll();
  }

  private List<DataExplorerWidgetModel> getCachedElements() {
    try {
      var cachedValue = findAllCache.get(FIND_ALL_CACHE_KEY, String.class);
      return cachedValue == null
          ? null
          : objectMapper.readValue(cachedValue, WIDGET_LIST_TYPE);
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not read data explorer widgets from cache", e);
      evictFindAll();
      return null;
    }
  }

  private DataExplorerWidgetModel getCachedElement(String id) {
    try {
      var cachedValue = cache.get(id, String.class);
      return cachedValue == null
          ? null
          : objectMapper.readValue(cachedValue, DataExplorerWidgetModel.class);
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not read data explorer widget {} from cache", id, e);
      evict(id);
      return null;
    }
  }

  private void put(DataExplorerWidgetModel element) {
    try {
      cache.put(element.getElementId(), objectMapper.writeValueAsString(element));
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not write data explorer widget {} to cache", element.getElementId(), e);
    }
  }

  private void put(List<DataExplorerWidgetModel> elements) {
    try {
      findAllCache.put(FIND_ALL_CACHE_KEY, objectMapper.writeValueAsString(elements));
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not write data explorer widgets to cache", e);
    }
  }

  private void evict(String id) {
    if (id == null) {
      return;
    }

    try {
      cache.evict(id);
    } catch (RuntimeException e) {
      LOG.warn("Could not evict data explorer widget {} from cache", id, e);
    }
  }

  private void evictFindAll() {
    try {
      findAllCache.evict(FIND_ALL_CACHE_KEY);
    } catch (RuntimeException e) {
      LOG.warn("Could not evict data explorer widgets from cache", e);
    }
  }
}
