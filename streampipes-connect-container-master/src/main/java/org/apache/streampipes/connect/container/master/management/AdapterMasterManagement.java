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

package org.apache.streampipes.connect.container.master.management;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.connect.adapter.GroundingService;
import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.container.master.util.AdapterEncryptionService;
import org.apache.streampipes.connect.container.master.util.Utils;
import org.apache.streampipes.manager.storage.UserService;
import org.apache.streampipes.manager.verification.DataStreamVerifier;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorageCache;
import org.apache.streampipes.storage.couchdb.impl.AdapterStorageImpl;
import org.apache.streampipes.storage.management.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import static org.apache.streampipes.manager.storage.UserManagementService.getUserService;


public class AdapterMasterManagement {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterManagement.class);

  private IAdapterStorage adapterStorage;
  private WorkerUrlProvider workerUrlProvider;

  public AdapterMasterManagement() {
    this.adapterStorage = getAdapterStorage();
    this.workerUrlProvider = new WorkerUrlProvider();
  }

  public AdapterMasterManagement(IAdapterStorage adapterStorage) {
    this.adapterStorage = adapterStorage;
  }

  public void startAllStreamAdapters(ConnectWorkerContainer connectWorkerContainer) throws AdapterException, NoServiceEndpointsAvailableException {
    IAdapterStorage adapterStorage = getAdapterStorage();
    List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();

    for (AdapterDescription ad : allAdapters) {
      if (ad instanceof AdapterStreamDescription) {
        AdapterDescription decryptedAdapterDescription =
                new AdapterEncryptionService(new Cloner().adapterDescription(ad)).decrypt();
        String wUrl = workerUrlProvider.getWorkerUrlForAdapter(decryptedAdapterDescription);

        if (wUrl.equals(connectWorkerContainer.getServiceGroup())) {
          String url = Utils.addUserNameToApi(connectWorkerContainer.getServiceGroup(),
                  decryptedAdapterDescription.getUserName());

          WorkerRestClient.invokeStreamAdapter(url, (AdapterStreamDescription) decryptedAdapterDescription);
        }

      }
    }
  }

  public String addAdapter(AdapterDescription ad,
                           String endpointUrl,
                           String username)
          throws AdapterException {

    // Add EventGrounding to AdapterDescription
    EventGrounding eventGrounding = GroundingService.createEventGrounding();
    ad.setEventGrounding(eventGrounding);

    String uuid = UUID.randomUUID().toString();
    ad.setElementId(ad.getElementId() + ":" + uuid);
    ad.setCreatedAt(System.currentTimeMillis());
    ad.setSelectedEndpointUrl(endpointUrl);

    AdapterDescription encryptedAdapterDescription =
            new AdapterEncryptionService(new Cloner().adapterDescription(ad)).encrypt();
    // store in db
    String adapterId = adapterStorage.storeAdapter(encryptedAdapterDescription);

    // start when stream adapter
    if (ad instanceof AdapterStreamDescription) {
      // TODO
      WorkerRestClient.invokeStreamAdapter(endpointUrl, adapterId);
      LOG.info("Start adapter");
    }

    LOG.info("Install source (source URL: {} in backend", ad.getElementId());
    SpDataStream storedDescription = new SourcesManagement().getAdapterDataStream(ad.getElementId());
    installDataSource(storedDescription, username);

    return storedDescription.getElementId();
  }

  public void installDataSource(SpDataStream stream, String username) throws AdapterException {
    try {
      new DataStreamVerifier(stream).verifyAndAdd(username, true, true);
    } catch (SepaParseException e) {
      LOG.error("Error while installing data source: {}", stream.getElementId(), e);
      throw new AdapterException();
    }
  }

  public AdapterDescription getAdapter(String id) throws AdapterException {
    List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();

    if (allAdapters != null && id != null) {
      for (AdapterDescription ad : allAdapters) {
        if (id.equals(ad.getId())) {
          return ad;
        }
      }
    }

    throw new AdapterException("Could not find adapter with id: " + id);
  }

  public void deleteAdapter(String id) throws AdapterException {
    //        // IF Stream adapter delete it
    boolean isStreamAdapter = isStreamAdapter(id);
    AdapterDescription ad = adapterStorage.getAdapter(id);

    if (isStreamAdapter) {
      stopStreamAdapter(id, ad.getSelectedEndpointUrl());
    }
    String username = ad.getUserName();

    adapterStorage.deleteAdapter(id);

    UserService userService = getUserService();
    IPipelineElementDescriptionStorageCache requestor = StorageManager.INSTANCE.getPipelineElementStorage();

    if (requestor.getDataStreamById(ad.getElementId()) != null) {
      requestor.deleteDataStream(requestor.getDataStreamById(ad.getElementId()));
      userService.deleteOwnSource(username, ad.getElementId());
      requestor.refreshDataSourceCache();
    }
  }

  public List<AdapterDescription> getAllAdapters() throws AdapterException {

    List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();

    if (allAdapters == null) {
      throw new AdapterException("Could not get all adapters");
    }

    return allAdapters;
  }

  public void stopSetAdapter(String adapterId, String baseUrl, AdapterStorageImpl adapterStorage) throws AdapterException {

    AdapterSetDescription ad = (AdapterSetDescription) adapterStorage.getAdapter(adapterId);

    WorkerRestClient.stopSetAdapter(baseUrl, ad);
  }

  public void stopStreamAdapter(String adapterId, String baseUrl) throws AdapterException {
    AdapterDescription ad = adapterStorage.getAdapter(adapterId);

    if (!isStreamAdapter(adapterId)) {
      throw new AdapterException("Adapter " + adapterId + "is not a stream adapter.");
    } else {
      WorkerRestClient.stopStreamAdapter(baseUrl, (AdapterStreamDescription) ad);
    }
  }

  public void startStreamAdapter(String adapterId, String baseUrl) throws AdapterException {
    AdapterDescription ad = adapterStorage.getAdapter(adapterId);
    if (!isStreamAdapter(adapterId)) {
      throw new AdapterException("Adapter " + adapterId + "is not a stream adapter.");
    } else {
      ad.setSelectedEndpointUrl(baseUrl);
      adapterStorage.updateAdapter(ad);
      WorkerRestClient.invokeStreamAdapter(baseUrl, (AdapterStreamDescription) ad);
    }
  }

  public boolean isStreamAdapter(String id) {
    AdapterDescription ad = adapterStorage.getAdapter(id);

    return ad instanceof AdapterStreamDescription;
  }

  private IAdapterStorage getAdapterStorage() {
    return new AdapterStorageImpl();
  }

}
