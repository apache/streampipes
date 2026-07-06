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
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CachedSpCoreConfigurationStorageTest {

  private ISpCoreConfigurationStorage delegate;
  private CachedSpCoreConfigurationStorage storage;

  @BeforeEach
  void setUp() {
    delegate = mock(ISpCoreConfigurationStorage.class);
    var cacheManager = new ConcurrentMapCacheManager(CachedSpCoreConfigurationStorage.CACHE_NAME);
    storage = new CachedSpCoreConfigurationStorage(delegate, cacheManager);
  }

  @Test
  void getCachesSerializedConfigurationCopy() {
    var configuration = makeConfiguration("assets");
    when(delegate.get()).thenReturn(configuration);

    var firstResult = storage.get();
    firstResult.setAssetDir("changed");
    var secondResult = storage.get();

    assertNotSame(firstResult, secondResult);
    assertEquals("assets", secondResult.getAssetDir());
    verify(delegate, times(1)).get();
  }

  @Test
  void existsCachesBooleanResult() {
    when(delegate.exists()).thenReturn(true);

    assertTrue(storage.exists());
    assertTrue(storage.exists());

    verify(delegate, times(1)).exists();
  }

  @Test
  void updateElementClearsAndRefreshesCaches() {
    var initialConfiguration = makeConfiguration("initial");
    var updatedConfiguration = makeConfiguration("updated");
    when(delegate.get()).thenReturn(initialConfiguration);
    when(delegate.exists()).thenReturn(true);
    when(delegate.updateElement(updatedConfiguration)).thenReturn(updatedConfiguration);

    storage.get();
    storage.exists();
    var updateResult = storage.updateElement(updatedConfiguration);
    var getResult = storage.get();

    assertEquals("updated", updateResult.getAssetDir());
    assertEquals("updated", getResult.getAssetDir());
    assertTrue(storage.exists());
    verify(delegate).updateElement(updatedConfiguration);
    verify(delegate, times(1)).get();
    verify(delegate, times(1)).exists();
  }

  @Test
  void deleteElementClearsAndCachesMissingConfiguration() {
    when(delegate.exists()).thenReturn(true);

    assertTrue(storage.exists());
    storage.deleteElement();

    assertFalse(storage.exists());
    verify(delegate).deleteElement();
    verify(delegate, times(1)).exists();
  }

  private SpCoreConfiguration makeConfiguration(String assetDir) {
    var configuration = new SpCoreConfiguration();
    configuration.setAssetDir(assetDir);
    configuration.setConfigured(true);
    return configuration;
  }
}
