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
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;
import java.util.Optional;

public class DescriptionManagement {

  public List<AdapterDescription> getAdapters() {
    IAdapterStorage adapterStorage = CouchDbStorageManager.INSTANCE.getAdapterDescriptionStorage();
    return adapterStorage.getAllAdapters();
  }

  public Optional<AdapterDescription> getAdapter(String id) {
    return getAdapters().stream()
        .filter(desc -> desc.getAppId().equals(id))
        .findFirst();
  }

  public void deleteAdapterDescription(String id) throws SpRuntimeException {
    var adapterStorage = CouchDbStorageManager.INSTANCE.getAdapterDescriptionStorage();
    var adapter = adapterStorage.getAdapter(id);
    if (!isAdapterUsed(adapter)) {
      adapterStorage.deleteAdapter(id);
    } else {
      throw new SpRuntimeException("This adapter is used by an existing instance and cannot be deleted");
    }
  }

  public String getAssets(String baseUrl) throws AdapterException {
    return WorkerRestClient.getAssets(baseUrl);
  }

  public byte[] getIconAsset(String baseUrl) throws AdapterException {
    return WorkerRestClient.getIconAsset(baseUrl);
  }

  public String getDocumentationAsset(String baseUrl) throws AdapterException {
    return WorkerRestClient.getDocumentationAsset(baseUrl);
  }

  private boolean isAdapterUsed(AdapterDescription adapter) {
    var allAdapters = StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage().getAllAdapters();

    return allAdapters
        .stream()
        .anyMatch(runningAdapter -> runningAdapter.getAppId().equals(adapter.getAppId()));
  }

}
