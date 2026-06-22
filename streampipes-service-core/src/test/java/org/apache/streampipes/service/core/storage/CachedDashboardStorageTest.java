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

import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;

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

class CachedDashboardStorageTest {

  private static final String DASHBOARD_ID = "dashboard-id";

  private IDashboardStorage delegate;
  private CachedDashboardStorage storage;

  @BeforeEach
  void setUp() {
    delegate = mock(IDashboardStorage.class);
    var cacheManager = new ConcurrentMapCacheManager(CachedDashboardStorage.CACHE_NAME);
    storage = new CachedDashboardStorage(delegate, cacheManager);
  }

  @Test
  void findAllCachesSerializedCopies() {
    var dashboard = makeDashboard("Initial name");
    when(delegate.findAll()).thenReturn(List.of(dashboard));

    var firstResult = storage.findAll();
    firstResult.get(0).setName("Changed name");
    var secondResult = storage.findAll();

    assertNotSame(firstResult, secondResult);
    assertNotSame(firstResult.get(0), secondResult.get(0));
    assertEquals("Initial name", secondResult.get(0).getName());
    verify(delegate).findAll();
  }

  @Test
  void getElementByIdCachesSerializedCopy() {
    var dashboard = makeDashboard("Initial name");
    when(delegate.getElementById(DASHBOARD_ID)).thenReturn(dashboard);

    var firstResult = storage.getElementById(DASHBOARD_ID);
    firstResult.setName("Changed name");
    var secondResult = storage.getElementById(DASHBOARD_ID);

    assertNotSame(firstResult, secondResult);
    assertEquals("Initial name", secondResult.getName());
    verify(delegate).getElementById(DASHBOARD_ID);
  }

  @Test
  void updateElementClearsAllCaches() {
    var dashboard = makeDashboard("Initial name");
    var updatedDashboard = makeDashboard("Updated name");
    when(delegate.getElementById(DASHBOARD_ID)).thenReturn(dashboard, updatedDashboard);
    when(delegate.findAll()).thenReturn(List.of(dashboard), List.of(updatedDashboard));
    when(delegate.updateElement(updatedDashboard)).thenReturn(updatedDashboard);

    storage.getElementById(DASHBOARD_ID);
    storage.findAll();
    storage.updateElement(updatedDashboard);

    assertEquals("Updated name", storage.getElementById(DASHBOARD_ID).getName());
    assertEquals("Updated name", storage.findAll().get(0).getName());
    verify(delegate, times(2)).getElementById(DASHBOARD_ID);
    verify(delegate, times(2)).findAll();
  }

  private DashboardModel makeDashboard(String name) {
    var dashboard = new DashboardModel();
    dashboard.setElementId(DASHBOARD_ID);
    dashboard.setName(name);
    return dashboard;
  }
}
