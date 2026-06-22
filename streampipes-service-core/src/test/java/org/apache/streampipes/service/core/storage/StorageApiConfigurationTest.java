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

import org.apache.streampipes.storage.couchdb.impl.connect.AdapterInstanceStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.explorer.ChartStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineStorageImpl;

import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class StorageApiConfigurationTest {

  private final ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
      CachedChartStorage.CACHE_NAME,
      CachedPermissionStorage.CACHE_NAME,
      CachedAdapterStorage.CACHE_NAME,
      CachedDashboardStorage.CACHE_NAME,
      CachedPipelineStorage.CACHE_NAME,
      CachedDataLakeMeasureStorage.CACHE_NAME
  );

  @Test
  void enablesStorageCaches() {
    var configuration = new StorageApiConfiguration(true, true, true, true, true, true);

    assertInstanceOf(CachedChartStorage.class, configuration.chartStorage(cacheManager));
    assertInstanceOf(CachedPermissionStorage.class, configuration.permissionStorage(cacheManager));
    assertInstanceOf(CachedAdapterStorage.class, configuration.adapterStorage(cacheManager));
    assertInstanceOf(CachedDashboardStorage.class, configuration.dashboardStorage(cacheManager));
    assertInstanceOf(CachedPipelineStorage.class, configuration.pipelineStorage(cacheManager));
    assertInstanceOf(CachedDataLakeMeasureStorage.class, configuration.datasetStorage(cacheManager));
  }

  @Test
  void configuresStorageCachesIndependently() {
    var configuration = new StorageApiConfiguration(false, true, false, true, false, true);

    assertInstanceOf(ChartStorageImpl.class, configuration.chartStorage(cacheManager));
    assertInstanceOf(CachedPermissionStorage.class, configuration.permissionStorage(cacheManager));
    assertInstanceOf(AdapterInstanceStorageImpl.class, configuration.adapterStorage(cacheManager));
    assertInstanceOf(CachedDashboardStorage.class, configuration.dashboardStorage(cacheManager));
    assertInstanceOf(PipelineStorageImpl.class, configuration.pipelineStorage(cacheManager));
    assertInstanceOf(CachedDataLakeMeasureStorage.class, configuration.datasetStorage(cacheManager));
  }
}
