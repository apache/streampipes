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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;
import java.util.Optional;

public class DescriptionManagement {

  private final WorkerRestClient workerRestClient;
  private final AdapterResourceManager adapterResourceManager;

  public DescriptionManagement(WorkerRestClient workerRestClient,
                               AdapterResourceManager adapterResourceManager) {
    this.workerRestClient = workerRestClient;
    this.adapterResourceManager = adapterResourceManager;
  }

  public List<AdapterDescription> getAdapters() {
    IAdapterStorage adapterStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterDescriptionStorage();
    return adapterStorage.findAll();
  }

  public Optional<AdapterDescription> getAdapter(String id) {
    return getAdapters().stream()
        .filter(desc -> desc.getAppId().equals(id))
        .findFirst();
  }

  public void deleteAdapterDescription(String id) throws SpRuntimeException {
    var adapterStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterDescriptionStorage();
    var adapter = adapterStorage.getElementById(id);
    if (!isAdapterUsed(adapter)) {
      adapterStorage.deleteElementById(id);
    } else {
      throw new SpRuntimeException("This adapter is used by an existing instance and cannot be deleted");
    }
  }

  public String getAssets(SpServiceRegistration service,
                          String appId) throws AdapterException {
    return workerRestClient.getAssets(service, appId);
  }

  public byte[] getIconAsset(SpServiceRegistration service,
                             String appId) throws AdapterException {
    return workerRestClient.getIconAsset(service, appId);
  }

  public String getDocumentationAsset(SpServiceRegistration service,
                                      String appId) throws AdapterException {
    return workerRestClient.getDocumentationAsset(service, appId);
  }

  private boolean isAdapterUsed(AdapterDescription adapter) {
    var allAdapters = adapterResourceManager.getDb().findAll();

    return allAdapters
        .stream()
        .anyMatch(runningAdapter -> runningAdapter.getAppId().equals(adapter.getAppId()));
  }

}
