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

import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.explorer.IChartStorage;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;
import org.apache.streampipes.storage.api.function.IFunctionStateStorage;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.couchdb.impl.connect.AdapterInstanceStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.explorer.ChartStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.explorer.DashboardStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.function.FunctionStateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.AssetStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.PermissionStorageImpl;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class StorageApiConfiguration {

  @Bean
  public IFunctionStateStorage functionStateStorage() {
    return new FunctionStateStorageImpl();
  }

  @Bean
  public IChartStorage chartStorage(CacheManager cacheManager) {
    return new CachedChartStorage(
        new ChartStorageImpl(),
        cacheManager
    );
  }

  @Bean
  public IPermissionStorage permissionStorage(CacheManager cacheManager) {
    return new CachedPermissionStorage(
        new PermissionStorageImpl("users/permissions"),
        cacheManager
    );
  }

  @Bean
  public IAdapterStorage adapterStorage(CacheManager cacheManager) {
    return new CachedAdapterStorage(
        new AdapterInstanceStorageImpl(),
        cacheManager
    );
  }

  @Bean
  public IDashboardStorage dashboardStorage(CacheManager cacheManager) {
    return new CachedDashboardStorage(
        new DashboardStorageImpl(),
        cacheManager
    );
  }

  @Bean
  public IAssetStorage assetStorage() {
    return new AssetStorageImpl();
  }
}
