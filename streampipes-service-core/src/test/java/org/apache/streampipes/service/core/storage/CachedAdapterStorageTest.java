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

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CachedAdapterStorageTest {

  private static final String ADAPTER_ID = "adapter-id";
  private static final String APP_ID = "app-id";

  private IAdapterStorage delegate;
  private CachedAdapterStorage storage;

  @BeforeEach
  void setUp() {
    delegate = mock(IAdapterStorage.class);
    var cacheManager = new ConcurrentMapCacheManager(
        CachedAdapterStorage.CACHE_NAME,
        CachedAdapterStorage.FIND_ALL_CACHE_NAME,
        CachedAdapterStorage.BY_APP_ID_CACHE_NAME
    );
    storage = new CachedAdapterStorage(delegate, cacheManager);
  }

  @Test
  void getAdaptersByAppIdCachesSerializedCopies() {
    var adapter = makeAdapter("initial-app");
    when(delegate.getAdaptersByAppId(APP_ID)).thenReturn(List.of(adapter));

    var firstResult = storage.getAdaptersByAppId(APP_ID);
    firstResult.get(0).setAppId("changed-app");
    var secondResult = storage.getAdaptersByAppId(APP_ID);

    assertNotSame(firstResult, secondResult);
    assertNotSame(firstResult.get(0), secondResult.get(0));
    assertEquals("initial-app", secondResult.get(0).getAppId());
    verify(delegate, times(1)).getAdaptersByAppId(APP_ID);
  }

  @Test
  void getFirstAdapterByAppIdUsesAppIdCache() {
    var adapter = makeAdapter(APP_ID);
    when(delegate.getAdaptersByAppId(APP_ID)).thenReturn(List.of(adapter));

    var firstResult = storage.getFirstAdapterByAppId(APP_ID);
    var secondResult = storage.getFirstAdapterByAppId(APP_ID);

    assertEquals(ADAPTER_ID, firstResult.getElementId());
    assertEquals(ADAPTER_ID, secondResult.getElementId());
    verify(delegate, times(1)).getAdaptersByAppId(APP_ID);
  }

  @Test
  void updateElementClearsAllQueryCaches() {
    var adapter = makeAdapter("old-app");
    var updatedAdapter = makeAdapter("new-app");
    when(delegate.getElementById(ADAPTER_ID)).thenReturn(adapter, updatedAdapter);
    when(delegate.findAll()).thenReturn(List.of(adapter), List.of(updatedAdapter));
    when(delegate.getAdaptersByAppId(APP_ID)).thenReturn(List.of(adapter), List.of(updatedAdapter));
    when(delegate.updateElement(updatedAdapter)).thenReturn(updatedAdapter);

    storage.getElementById(ADAPTER_ID);
    storage.findAll();
    storage.getAdaptersByAppId(APP_ID);
    storage.updateElement(updatedAdapter);

    assertEquals("new-app", storage.getElementById(ADAPTER_ID).getAppId());
    assertEquals("new-app", storage.findAll().get(0).getAppId());
    assertEquals("new-app", storage.getAdaptersByAppId(APP_ID).get(0).getAppId());
    verify(delegate, times(2)).getElementById(ADAPTER_ID);
    verify(delegate, times(2)).findAll();
    verify(delegate, times(2)).getAdaptersByAppId(APP_ID);
  }

  private AdapterDescription makeAdapter(String appId) {
    var adapter = new AdapterDescription();
    adapter.setElementId(ADAPTER_ID);
    adapter.setAppId(appId);
    return adapter;
  }
}
