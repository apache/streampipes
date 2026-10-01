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

import org.apache.streampipes.manager.extensions.AvailableExtensionsProvider;
import org.apache.streampipes.manager.setup.InstallationConfiguration;
import org.apache.streampipes.service.core.PipelineManagementConfiguration;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.core.INoSqlStorage;
import org.apache.streampipes.storage.api.system.IImageStorage;
import org.apache.streampipes.storage.couchdb.impl.connect.AdapterDescriptionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.connect.AdapterInstanceStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.explorer.ChartStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.CoreConfigurationStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.PrivilegeStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.RoleStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.UserStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mockStatic;

class StorageApiConfigurationTest {

  private final ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
      CachedChartStorage.CACHE_NAME,
      CachedPermissionStorage.CACHE_NAME,
      CachedAdapterStorage.CACHE_NAME,
      CachedDashboardStorage.CACHE_NAME,
      CachedPipelineStorage.CACHE_NAME,
      CachedDatasetMetadataStorage.CACHE_NAME,
      CachedRoleStorage.CACHE_NAME,
      CachedUserGroupStorage.CACHE_NAME,
      CachedPrivilegeStorage.CACHE_NAME,
      CachedUserStorage.CACHE_NAME,
      CachedSpCoreConfigurationStorage.CACHE_NAME
  );

  @Test
  void registersUncachedStoragesAndDistinguishesAdapterDescriptions() {
    try (var clients = mockStatic(Utils.class);
         var context = new AnnotationConfigApplicationContext()) {
      context.registerBean(CacheManager.class, () -> cacheManager);
      context.register(StorageApiConfiguration.class, PipelineManagementConfiguration.class, AdapterStorages.class);
      context.refresh();

      var adapters = context.getBean(AdapterStorages.class);
      assertSame(context.getBean("adapterStorage"), adapters.instances());
      assertSame(context.getBean("adapterDescriptionStorage"), adapters.descriptions());
      assertInstanceOf(CachedAdapterStorage.class, adapters.instances());
      assertInstanceOf(AdapterDescriptionStorageImpl.class, adapters.descriptions());

      for (var method : INoSqlStorage.class.getDeclaredMethods()) {
        var storageType = method.getReturnType();
        if (storageType != IAdapterStorage.class && storageType != IImageStorage.class) {
          var storage = context.getBean(storageType);
          assertFalse(storage.getClass().getSimpleName().startsWith("Cached"), storageType.getSimpleName());
        }
      }
      assertEquals(IImageStorage.class, context.getType("imageStorage"));
      assertFalse(context.getBeanFactory().containsSingleton("imageStorage"));
      assertNotNull(context.getBean(InstallationConfiguration.class));
      assertNotNull(context.getBean(AvailableExtensionsProvider.class));
      clients.verifyNoInteractions();
    }
  }

  record AdapterStorages(IAdapterStorage instances,
                         @Qualifier("adapterDescriptionStorage") IAdapterStorage descriptions) {
  }

  @Test
  void enablesStorageCaches() {
    var configuration = new StorageApiConfiguration(true, true, true, true, true, true, true, true, true, true, true);

    assertInstanceOf(CachedChartStorage.class, configuration.chartStorage(cacheManager));
    assertInstanceOf(CachedPermissionStorage.class, configuration.permissionStorage(cacheManager));
    assertInstanceOf(CachedAdapterStorage.class, configuration.adapterStorage(cacheManager));
    assertInstanceOf(CachedDashboardStorage.class, configuration.dashboardStorage(cacheManager));
    assertInstanceOf(CachedPipelineStorage.class, configuration.pipelineStorage(cacheManager));
    assertInstanceOf(CachedDatasetMetadataStorage.class, configuration.datasetStorage(cacheManager));
    assertInstanceOf(CachedRoleStorage.class, configuration.roleStorage(cacheManager));
    assertInstanceOf(CachedUserGroupStorage.class, configuration.userGroupStorage(cacheManager));
    assertInstanceOf(CachedPrivilegeStorage.class, configuration.privilegeStorage(cacheManager));
    assertInstanceOf(CachedUserStorage.class, configuration.userStorage(cacheManager));
    assertInstanceOf(CachedSpCoreConfigurationStorage.class, configuration.coreConfigurationStorage(cacheManager));
  }

  @Test
  void configuresStorageCachesIndependently() {
    var configuration = new StorageApiConfiguration(false, true, false, true, false, true, false, true, false, false, false);

    assertInstanceOf(ChartStorageImpl.class, configuration.chartStorage(cacheManager));
    assertInstanceOf(CachedPermissionStorage.class, configuration.permissionStorage(cacheManager));
    assertInstanceOf(AdapterInstanceStorageImpl.class, configuration.adapterStorage(cacheManager));
    assertInstanceOf(CachedDashboardStorage.class, configuration.dashboardStorage(cacheManager));
    assertInstanceOf(PipelineStorageImpl.class, configuration.pipelineStorage(cacheManager));
    assertInstanceOf(CachedDatasetMetadataStorage.class, configuration.datasetStorage(cacheManager));
    assertInstanceOf(RoleStorageImpl.class, configuration.roleStorage(cacheManager));
    assertInstanceOf(CachedUserGroupStorage.class, configuration.userGroupStorage(cacheManager));
    assertInstanceOf(PrivilegeStorageImpl.class, configuration.privilegeStorage(cacheManager));
    assertInstanceOf(UserStorage.class, configuration.userStorage(cacheManager));
    assertInstanceOf(CoreConfigurationStorageImpl.class, configuration.coreConfigurationStorage(cacheManager));
  }
}
