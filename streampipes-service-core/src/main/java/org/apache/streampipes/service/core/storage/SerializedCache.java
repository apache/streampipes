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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import java.util.concurrent.atomic.AtomicBoolean;

class SerializedCache {

  private static final Logger LOG = LoggerFactory.getLogger(SerializedCache.class);

  private final Cache cache;
  private final ObjectMapper objectMapper;
  private final AtomicBoolean enabled = new AtomicBoolean(true);

  SerializedCache(Cache cache,
                  ObjectMapper objectMapper) {
    this.cache = cache;
    this.objectMapper = objectMapper;
  }

  <T> T get(String key,
            JavaType type) {
    if (key == null || !enabled.get()) {
      return null;
    }

    try {
      var cachedValue = cache.get(key, String.class);
      return cachedValue == null ? null : objectMapper.readValue(cachedValue, type);
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not read {} from cache {}", key, cache.getName(), e);
      evict(key);
      return null;
    }
  }

  void put(String key,
           Object value,
           JavaType type) {
    if (key == null || value == null || !enabled.get()) {
      return;
    }

    try {
      cache.put(key, objectMapper.writerFor(type).writeValueAsString(value));
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not write {} to cache {}", key, cache.getName(), e);
    }
  }

  String makeKey(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException | RuntimeException e) {
      LOG.warn("Could not serialize cache key for {}", cache.getName(), e);
      return null;
    }
  }

  void clear() {
    try {
      cache.clear();
      enabled.set(true);
    } catch (RuntimeException e) {
      enabled.set(false);
      LOG.error("Could not clear cache {}; caching has been disabled for this resource", cache.getName(), e);
    }
  }

  private void evict(String key) {
    try {
      cache.evict(key);
    } catch (RuntimeException e) {
      LOG.warn("Could not evict {} from cache {}", key, cache.getName(), e);
    }
  }
}
