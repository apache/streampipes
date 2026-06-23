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

import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.explorer.IChartStorage;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;
import org.apache.streampipes.storage.api.explorer.IDataLakeMeasureStorage;
import org.apache.streampipes.storage.api.function.IFunctionStateStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.IFileMetadataStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.couchdb.impl.connect.AdapterInstanceStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.explorer.ChartStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.explorer.DashboardStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.explorer.DataLakeMeasureStorage;
import org.apache.streampipes.storage.couchdb.impl.function.FunctionStateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.AssetStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.CoreConfigurationStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.FileMetadataStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.PermissionStorageImpl;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class StorageApiConfiguration {

  private final boolean chartCacheEnabled;
  private final boolean permissionCacheEnabled;
  private final boolean adapterCacheEnabled;
  private final boolean dashboardCacheEnabled;
  private final boolean pipelineCacheEnabled;
  private final boolean dataLakeMeasureCacheEnabled;

  public StorageApiConfiguration(
      @Value("${streampipes.storage.cache.data-explorer-widgets.enabled:true}") boolean chartCacheEnabled,
      @Value("${streampipes.storage.cache.permissions.enabled:true}") boolean permissionCacheEnabled,
      @Value("${streampipes.storage.cache.adapters.enabled:true}") boolean adapterCacheEnabled,
      @Value("${streampipes.storage.cache.dashboards.enabled:true}") boolean dashboardCacheEnabled,
      @Value("${streampipes.storage.cache.pipelines.enabled:true}") boolean pipelineCacheEnabled,
      @Value("${streampipes.storage.cache.data-lake-measures.enabled:true}") boolean dataLakeMeasureCacheEnabled) {
    this.chartCacheEnabled = chartCacheEnabled;
    this.permissionCacheEnabled = permissionCacheEnabled;
    this.adapterCacheEnabled = adapterCacheEnabled;
    this.dashboardCacheEnabled = dashboardCacheEnabled;
    this.pipelineCacheEnabled = pipelineCacheEnabled;
    this.dataLakeMeasureCacheEnabled = dataLakeMeasureCacheEnabled;
  }

  @Bean
  public IFunctionStateStorage functionStateStorage() {
    return new FunctionStateStorageImpl();
  }

  @Bean
  public IChartStorage chartStorage(CacheManager cacheManager) {
    IChartStorage delegate = new ChartStorageImpl();
    return chartCacheEnabled ? new CachedChartStorage(delegate, cacheManager) : delegate;
  }

  @Bean
  public IPermissionStorage permissionStorage(CacheManager cacheManager) {
    IPermissionStorage delegate = new PermissionStorageImpl("users/permissions");
    return permissionCacheEnabled ? new CachedPermissionStorage(delegate, cacheManager) : delegate;
  }

  @Bean
  public IAdapterStorage adapterStorage(CacheManager cacheManager) {
    IAdapterStorage delegate = new AdapterInstanceStorageImpl();
    return adapterCacheEnabled ? new CachedAdapterStorage(delegate, cacheManager) : delegate;
  }

  @Bean
  public IDashboardStorage dashboardStorage(CacheManager cacheManager) {
    IDashboardStorage delegate = new DashboardStorageImpl();
    return dashboardCacheEnabled ? new CachedDashboardStorage(delegate, cacheManager) : delegate;
  }

  @Bean
  public IAssetStorage assetStorage() {
    return new AssetStorageImpl();
  }

  @Bean
  public ISpCoreConfigurationStorage coreConfigurationStorage() {
    return new CoreConfigurationStorageImpl();
  }

  @Bean
  public IFileMetadataStorage fileMetadataStorage() {
    return new FileMetadataStorageImpl();
  }

  @Bean
  public IPipelineStorage pipelineStorage(CacheManager cacheManager) {
    IPipelineStorage delegate = new PipelineStorageImpl();
    return pipelineCacheEnabled ? new CachedPipelineStorage(delegate, cacheManager) : delegate;
  }

  @Bean
  public IDataLakeMeasureStorage datasetStorage(CacheManager cacheManager) {
    IDataLakeMeasureStorage delegate = new DataLakeMeasureStorage(
        () -> Utils.getCouchDbGsonClient(Utils.DATA_LAKE_DB_NAME),
        DataLakeMeasure.class
    );
    return dataLakeMeasureCacheEnabled ? new CachedDataLakeMeasureStorage(delegate, cacheManager) : delegate;
  }
}
