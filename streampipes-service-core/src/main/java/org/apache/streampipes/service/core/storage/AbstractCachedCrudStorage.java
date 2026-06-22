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
import org.apache.streampipes.model.shared.api.Storable;
import org.apache.streampipes.storage.api.core.CRUDStorage;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

abstract class AbstractCachedCrudStorage<T extends Storable, StorageT extends CRUDStorage<T>>
    implements CRUDStorage<T> {

  private static final String ALL_KEY = "query:all";
  private static final String ID_KEY_PREFIX = "id:";

  protected final StorageT delegate;

  private final SerializedCache cache;
  private final JavaType elementType;
  private final JavaType elementListType;
  private final TypeFactory typeFactory;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

  AbstractCachedCrudStorage(StorageT delegate,
                            CacheManager cacheManager,
                            String cacheName,
                            ObjectMapper objectMapper,
                            Class<T> elementClass) {
    this.delegate = delegate;
    this.cache = new SerializedCache(
        Objects.requireNonNull(cacheManager.getCache(cacheName)),
        objectMapper
    );
    this.typeFactory = objectMapper.getTypeFactory();
    this.elementType = typeFactory.constructType(elementClass);
    this.elementListType = typeFactory.constructCollectionType(List.class, elementClass);
  }

  @Override
  public List<T> findAll() {
    return getOrLoad(ALL_KEY, elementListType, delegate::findAll);
  }

  @Override
  public Tuple2<Boolean, String> persist(T element) {
    var writeLock = lock.writeLock();
    writeLock.lock();
    try {
      var result = delegate.persist(element);
      if (Boolean.TRUE.equals(result.k)) {
        cache.clear();
      }
      return result;
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public T getElementById(String id) {
    return getOrLoad(key(ID_KEY_PREFIX, id), elementType, () -> delegate.getElementById(id));
  }

  @Override
  public T updateElement(T element) {
    var writeLock = lock.writeLock();
    writeLock.lock();
    try {
      var updatedElement = delegate.updateElement(element);
      cache.clear();
      return updatedElement;
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public void deleteElement(T element) {
    var writeLock = lock.writeLock();
    writeLock.lock();
    try {
      delegate.deleteElement(element);
      cache.clear();
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public void deleteElementById(String id) {
    var writeLock = lock.writeLock();
    writeLock.lock();
    try {
      delegate.deleteElementById(id);
      cache.clear();
    } finally {
      writeLock.unlock();
    }
  }

  protected <ResultT> ResultT getOrLoad(String key,
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

  protected JavaType type(Class<?> targetClass) {
    return typeFactory.constructType(targetClass);
  }

  protected JavaType listType(Class<?> targetClass) {
    return typeFactory.constructCollectionType(List.class, targetClass);
  }

  protected JavaType setType(Class<?> targetClass) {
    return typeFactory.constructCollectionType(java.util.Set.class, targetClass);
  }

  protected String makeKey(Object value) {
    return cache.makeKey(value);
  }

  protected String key(String prefix,
                       String value) {
    return value == null ? null : prefix + value;
  }
}
