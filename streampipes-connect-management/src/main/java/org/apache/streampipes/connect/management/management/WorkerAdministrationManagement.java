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

import org.apache.streampipes.connect.management.health.AdapterHealthCheck;
import org.apache.streampipes.connect.management.health.AdapterOperationLock;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkerAdministrationManagement {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterManagement.class);
  private static final int MAX_RETRIES = 7;

  private final IAdapterStorage adapterDescriptionStorage;

  private final AdapterHealthCheck adapterHealthCheck;

  public WorkerAdministrationManagement() {
    this.adapterHealthCheck = new AdapterHealthCheck();
    this.adapterDescriptionStorage = CouchDbStorageManager.INSTANCE.getAdapterDescriptionStorage();
  }

  public void register(List<AdapterDescription> availableAdapterDescription) {
    List<AdapterDescription> alreadyRegisteredAdapters = this.adapterDescriptionStorage.getAllAdapters();

    availableAdapterDescription.forEach(adapterDescription -> {

      // only install once adapter description per service group
      boolean alreadyInstalled =
          alreadyRegisteredAdapters.stream().anyMatch(a -> a.getAppId().equals(adapterDescription.getAppId()));
      if (!alreadyInstalled) {
        this.adapterDescriptionStorage.storeAdapter(adapterDescription);
      }
    });

    int retryCount = 0;
    checkAndRestore(retryCount);
  }

  private void checkAndRestore(int retryCount) {
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
}
