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

import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.connect.adapter.GroundingService;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.config.ConnectContainerConfig;
import org.apache.streampipes.connect.container.master.util.AdapterEncryptionService;
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
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import static org.apache.streampipes.manager.storage.UserManagementService.getUserService;


public class AdapterMasterManagement {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterManagement.class);

  private IAdapterStorage adapterStorage;

  public AdapterMasterManagement() {
    this.adapterStorage = getAdapterStorage();
  }

  public AdapterMasterManagement(IAdapterStorage adapterStorage) {
    this.adapterStorage = adapterStorage;
  }

  public void startAllStreamAdapters(ConnectWorkerContainer connectWorkerContainer) throws AdapterException {
    IAdapterStorage adapterStorage = getAdapterStorage();
    List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();

    for (AdapterDescription ad : allAdapters) {
      if (ad instanceof AdapterStreamDescription) {
        AdapterDescription decryptedAdapterDescription =
                new AdapterEncryptionService(new Cloner().adapterDescription(ad)).decrypt();
        String wUrl = new Utils().getWorkerUrl(decryptedAdapterDescription);

        if (wUrl.equals(connectWorkerContainer.getEndpointUrl())) {
          String url = Utils.addUserNameToApi(connectWorkerContainer.getEndpointUrl(),
                  decryptedAdapterDescription.getUserName());

          WorkerRestClient.invokeStreamAdapter(url, (AdapterStreamDescription) decryptedAdapterDescription);
        }

      }
    }
  }

  public String addAdapter(AdapterDescription ad,
                           String baseUrl,
                           String username)
          throws AdapterException {

    // Add EventGrounding to AdapterDescription
    EventGrounding eventGrounding = GroundingService.createEventGrounding();
    ad.setEventGrounding(eventGrounding);

    String uuid = UUID.randomUUID().toString();

//    String newId = ConnectContainerConfig.INSTANCE.getConnectContainerMasterUrl() + "api/v1/" + username + "/master/sources/" + uuid;
    String newId = ConnectContainerConfig.INSTANCE.getBackendApiUrl() + "api/v2/connect/" + username + "/master/sources/" + uuid;

    ad.setElementId(newId);
    ad.setCreatedAt(System.currentTimeMillis());

    AdapterDescription encryptedAdapterDescription =
            new AdapterEncryptionService(new Cloner().adapterDescription(ad)).encrypt();
    // store in db
    String adapterId = adapterStorage.storeAdapter(encryptedAdapterDescription);

    // start when stream adapter
    if (ad instanceof AdapterStreamDescription) {
      // TODO
      WorkerRestClient.invokeStreamAdapter(baseUrl, adapterId);
      LOG.info("Start adapter");
    }

    // backend url is used to install data source in streampipes
    String backendBaseUrl = ConnectContainerConfig.INSTANCE.getBackendApiUrl() + "api/v2/";
    String requestUrl = backendBaseUrl + "noauth/users/" + username + "/element";

    LOG.info("Install source (source URL: " + newId + " in backend over URL: " + requestUrl);
    SpDataStream storedDescription = new SourcesManagement().getAdapterDataStream(newId);
    installDataSource(storedDescription, username);

    return storedDescription.getElementId();
  }

  public void installDataSource(SpDataStream stream, String username) throws AdapterException {
    try {
      new DataStreamVerifier(stream).verifyAndAdd(username, true, true);
    } catch (SepaParseException e) {
      LOG.error("Error while installing data source: " + stream.getElementId(), e);
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

  public void deleteAdapter(String id, String baseUrl) throws AdapterException {
    //        // IF Stream adapter delete it
    boolean isStreamAdapter = isStreamAdapter(id);

    if (isStreamAdapter) {
      stopStreamAdapter(id, baseUrl);
    }
    AdapterDescription ad = adapterStorage.getAdapter(id);
    String username = ad.getUserName();

    adapterStorage.deleteAdapter(id);

    UserService userService = getUserService();
    IPipelineElementDescriptionStorageCache requestor = StorageDispatcher.INSTANCE.getTripleStore().getPipelineElementStorage();

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
