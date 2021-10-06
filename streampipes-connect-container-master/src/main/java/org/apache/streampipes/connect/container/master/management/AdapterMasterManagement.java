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
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
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
import org.apache.streampipes.storage.couchdb.impl.AdapterDescriptionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.AdapterInstanceStorageImpl;
import org.apache.streampipes.storage.management.StorageManager;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.apache.streampipes.manager.storage.UserManagementService.getUserService;


public class AdapterMasterManagement {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterManagement.class);

  private IAdapterStorage adapterInstanceStorage;
  private WorkerUrlProvider workerUrlProvider;

  public AdapterMasterManagement() {
    this.adapterInstanceStorage = getAdapterInstanceStorage();
    this.adapterDescriptionStorage = new AdapterDescriptionStorageImpl();
    this.workerUrlProvider = new WorkerUrlProvider();
  }

  public AdapterMasterManagement(IAdapterStorage adapterStorage) {
    this.adapterInstanceStorage = adapterStorage;
  }

  public void startAllStreamAdapters(ConnectWorkerContainer connectWorkerContainer) throws AdapterException, NoServiceEndpointsAvailableException {
    IAdapterStorage adapterStorage = getAdapterInstanceStorage();
    List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();

    for (AdapterDescription ad : allAdapters) {

      if (ad instanceof AdapterStreamDescription) {
        AdapterDescription decryptedAdapterDescription =
                new AdapterEncryptionService(new Cloner().adapterDescription(ad)).decrypt();

        if (decryptedAdapterDescription.getCorrespondingServiceGroup().equals(connectWorkerContainer.getServiceGroup())) {
          String wUrl = workerUrlProvider.getWorkerBaseUrl(decryptedAdapterDescription.getAppId());
          WorkerRestClient.invokeStreamAdapter(wUrl, (AdapterStreamDescription) decryptedAdapterDescription);
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
    String adapterId = adapterInstanceStorage.storeAdapter(encryptedAdapterDescription);

    // start when stream adapter
    if (ad instanceof AdapterStreamDescription) {
      WorkerRestClient.invokeStreamAdapter(endpointUrl, adapterId);
      LOG.info("Start adapter");
    }

    LOG.info("Install source (source URL: {} in backend", ad.getElementId());
    SpDataStream storedDescription = new SourcesManagement().getAdapterDataStream(ad.getElementId());
    storedDescription.setCorrespondingAdapterId(adapterId);
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
    List<AdapterDescription> allAdapters = adapterInstanceStorage.getAllAdapters();

    if (allAdapters != null && id != null) {
      for (AdapterDescription ad : allAdapters) {
        if (id.equals(ad.getElementId())) {
          return ad;
        }
      }
    }

    throw new AdapterException("Could not find adapter with id: " + id);
  }

  /**
   * First the adapter is stopped removed, then the according data source is deleted
   * @param elementId
   * @throws AdapterException
   */
  public void deleteAdapter(String elementId) throws AdapterException {
    // IF Stream adapter delete it
    boolean isStreamAdapter = isStreamAdapter(elementId);

    if (isStreamAdapter) {
      try {
        stopStreamAdapter(elementId);
      } catch (AdapterException e) {
        LOG.info("Could not stop adapter: " + elementId);
        LOG.info(e.toString());
      }
    }


    AdapterDescription ad = adapterInstanceStorage.getAdapter(elementId);
    String username = ad.getUserName();
    adapterInstanceStorage.deleteAdapter(elementId);
    LOG.info("Successfully deleted adapter: " + elementId);

    UserService userService = getUserService();
    IPipelineElementDescriptionStorageCache requestor = StorageManager.INSTANCE.getPipelineElementStorage();

    if (requestor.getDataStreamById(ad.getElementId()) != null) {
      requestor.deleteDataStream(requestor.getDataStreamById(ad.getElementId()));
      userService.deleteOwnSource(username, ad.getElementId());
      requestor.refreshDataSourceCache();
    }
  }

  public List<AdapterDescription> getAllAdapterInstances() throws AdapterException {

    List<AdapterDescription> allAdapters = adapterInstanceStorage.getAllAdapters();

    if (allAdapters == null) {
      throw new AdapterException("Could not get all adapters");
    }

    return allAdapters;
  }

  public List<AdapterDescription> getAllAdapterDescriptions() throws AdapterException {

    List<AdapterDescription> allAdapters = adapterInstanceStorage.getAllAdapters();

    if (allAdapters == null) {
      throw new AdapterException("Could not get all adapters");
    }

    return allAdapters;
  }

  public void stopSetAdapter(String adapterId, String baseUrl, AdapterInstanceStorageImpl adapterStorage) throws AdapterException {

    AdapterSetDescription ad = (AdapterSetDescription) adapterStorage.getAdapter(adapterId);

    WorkerRestClient.stopSetAdapter(baseUrl, ad);
  }

  public void stopStreamAdapter(String elementId) throws AdapterException {
    AdapterDescription ad = adapterInstanceStorage.getAdapter(elementId);

    if (!isStreamAdapter(elementId)) {
      throw new AdapterException("Adapter " + elementId + "is not a stream adapter.");
    } else {
      WorkerRestClient.stopStreamAdapter(ad.getSelectedEndpointUrl(), (AdapterStreamDescription) ad);
    }
  }

  public void startStreamAdapter(String elementId) throws AdapterException {
    // TODO ensure that adapter is not started twice

    AdapterDescription ad = adapterInstanceStorage.getAdapter(elementId);
    try {
      String endpointUrl = findEndpointUrl(ad);
      URI uri = new URI(endpointUrl);
      String baseUrl = uri.getScheme() + "://" + uri.getAuthority();
      if (!isStreamAdapter(elementId)) {
        throw new AdapterException("Adapter " + elementId + "is not a stream adapter.");
      } else {
        ad.setSelectedEndpointUrl(baseUrl);
        adapterInstanceStorage.updateAdapter(ad);

        AdapterDescription decryptedAdapterDescription =
                new AdapterEncryptionService(new Cloner().adapterDescription(ad)).decrypt();
        WorkerRestClient.invokeStreamAdapter(baseUrl, (AdapterStreamDescription) decryptedAdapterDescription);
      }
    } catch (NoServiceEndpointsAvailableException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }


  }

  public boolean isStreamAdapter(String id) {
    AdapterDescription adapterDescription = adapterInstanceStorage.getAdapter(id);
    return isStreamAdapter(adapterDescription);
  }

  public boolean isStreamAdapter(AdapterDescription adapterDescription) {
    return adapterDescription instanceof AdapterStreamDescription;
  }

  private IAdapterStorage getAdapterInstanceStorage() {
    return new AdapterInstanceStorageImpl();
  }

  private String findEndpointUrl(AdapterDescription adapterDescription) throws NoServiceEndpointsAvailableException {
    SpServiceUrlProvider serviceUrlProvider = SpServiceUrlProvider.ADAPTER;
    return new ExtensionsServiceEndpointGenerator(adapterDescription.getAppId(), serviceUrlProvider).getEndpointResourceUrl();
  }
}
