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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetrics;
import org.apache.streampipes.connect.management.health.AdapterHealthCheck;
import org.apache.streampipes.connect.management.health.AdapterOperationLock;
import org.apache.streampipes.manager.assets.AssetManager;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.resource.management.PermissionResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkerAdministrationManagement {

  private static final Logger LOG = LoggerFactory.getLogger(WorkerAdministrationManagement.class);
  private static final int MAX_RETRIES = 7;

  private final IAdapterStorage adapterDescriptionStorage;

  private final AdapterHealthCheck adapterHealthCheck;

  public WorkerAdministrationManagement(
      IAdapterStorage adapterStorage,
      AdapterMetrics adapterMetrics,
      AdapterResourceManager adapterResourceManager,
      DataStreamResourceManager dataStreamResourceManager
  ) {
    this.adapterHealthCheck = new AdapterHealthCheck(
        adapterStorage,
        new AdapterMasterManagement(
            adapterStorage,
            adapterResourceManager,
            dataStreamResourceManager,
            adapterMetrics
        )
    );
    this.adapterDescriptionStorage = CouchDbStorageManager.INSTANCE.getAdapterDescriptionStorage();
  }

  public void checkAndRestore(int retryCount) {
    if (AdapterOperationLock.INSTANCE.isLocked()) {
      LOG.info("Adapter operation already in progress, {}/{}", (retryCount + 1), MAX_RETRIES);
      if (retryCount <= MAX_RETRIES) {
        try {
          TimeUnit.MILLISECONDS.sleep(3000);
          retryCount++;
          checkAndRestore(retryCount);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } else {
        LOG.info("Max retries for running adapter operations reached, will do unlock which might cause conflicts...");
        AdapterOperationLock.INSTANCE.unlock();
        this.adapterHealthCheck.checkAndRestoreAdapters();
      }
    } else {
      AdapterOperationLock.INSTANCE.lock();
      this.adapterHealthCheck.checkAndRestoreAdapters();
      AdapterOperationLock.INSTANCE.unlock();
    }
  }

  public void performAdapterMigrations(List<SpServiceTag> tags) {
    var installedAdapters = CouchDbStorageManager.INSTANCE.getAdapterDescriptionStorage().findAll();
    var adminSid = new SpResourceManager().manageUsers().getAdminUser().getPrincipalId();
    installedAdapters.stream()
        .filter(adapter -> tags.stream().anyMatch(tag -> tag.getValue().equals(adapter.getAppId())))
        .forEach(adapter -> {
          if (!AssetManager.existsAssetDir(adapter.getAppId())) {
            try {
              LOG.info("Updating assets for adapter {}", adapter.getAppId());
              AssetManager.storeAsset(SpServiceUrlProvider.ADAPTER, adapter.getAppId());
            } catch (IOException | NoServiceEndpointsAvailableException e) {
              LOG.error(
                  "Could not fetch asset for adapter {}, please try to manually update this adapter.",
                  adapter.getAppId(),
                  e);
            }
          }
          var permissionStorage = CouchDbStorageManager.INSTANCE.getPermissionStorage();
          var elementId = adapter.getElementId();
          var permissions = permissionStorage.getUserPermissionsForObject(elementId);
          if (permissions.isEmpty()) {
            LOG.info("Adding default permission for adapter {}", adapter.getAppId());
            new PermissionResourceManager()
                .createDefault(elementId, AdapterDescription.class, adminSid, true);
          }
        });
  }
}
