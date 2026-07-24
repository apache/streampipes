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

import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class CachedSpCoreConfigurationStorage implements ISpCoreConfigurationStorage {

  static final String CACHE_NAME = "coreConfiguration";

  private static final String CORE_CONFIGURATION_KEY = "query:core-configuration";
  private static final String EXISTS_KEY = "query:exists";

  private final ISpCoreConfigurationStorage delegate;
  private final SerializedCache cache;
  private final JavaType coreConfigurationType;
  private final JavaType booleanType;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

  public CachedSpCoreConfigurationStorage(ISpCoreConfigurationStorage delegate,
                                          CacheManager cacheManager) {
    this(delegate, cacheManager, JacksonSerializer.getObjectMapper());
  }

  CachedSpCoreConfigurationStorage(ISpCoreConfigurationStorage delegate,
                                   CacheManager cacheManager,
                                   ObjectMapper objectMapper) {
    this.delegate = delegate;
    this.cache = new SerializedCache(
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME)),
        objectMapper
    );
    this.coreConfigurationType = objectMapper.getTypeFactory().constructType(SpCoreConfiguration.class);
    this.booleanType = objectMapper.getTypeFactory().constructType(Boolean.class);
  }

  @Override
  public boolean exists() {
    return getOrLoad(EXISTS_KEY, booleanType, delegate::exists);
  }

  @Override
  public void createElement(SpCoreConfiguration element) {
    var writeLock = lock.writeLock();
    writeLock.lock();
    try {
      delegate.createElement(element);
      cache.clear();
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public SpCoreConfiguration get() {
    return getOrLoad(CORE_CONFIGURATION_KEY, coreConfigurationType, delegate::get);
  }

  @Override
  public SpCoreConfiguration updateElement(SpCoreConfiguration element) {
    var writeLock = lock.writeLock();
    writeLock.lock();
    try {
      var result = delegate.updateElement(element);
      cache.clear();
      cache.put(CORE_CONFIGURATION_KEY, result, coreConfigurationType);
      cache.put(EXISTS_KEY, true, booleanType);
      return result;
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public void deleteElement() {
    var writeLock = lock.writeLock();
    writeLock.lock();
    try {
      delegate.deleteElement();
      cache.clear();
      cache.put(EXISTS_KEY, false, booleanType);
    } finally {
      writeLock.unlock();
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
}
