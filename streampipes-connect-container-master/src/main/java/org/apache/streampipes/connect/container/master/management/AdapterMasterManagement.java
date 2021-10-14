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
import org.apache.streampipes.connect.container.master.util.WorkerPaths;
import org.apache.streampipes.manager.storage.UserService;
import org.apache.streampipes.manager.verification.DataStreamVerifier;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorageCache;
import org.apache.streampipes.storage.couchdb.impl.AdapterInstanceStorageImpl;
import org.apache.streampipes.storage.management.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.streampipes.manager.storage.UserManagementService.getUserService;

/**
 * This class is responsible for managing all the adapter instances which are executed on worker nodes
 */
public class AdapterMasterManagement {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterManagement.class);

  private IAdapterStorage adapterInstanceStorage;

  public AdapterMasterManagement() {
    this.adapterInstanceStorage = getAdapterInstanceStorage();
  }

  public AdapterMasterManagement(IAdapterStorage adapterStorage) {
    this.adapterInstanceStorage = adapterStorage;
  }

  public String addAdapter(AdapterDescription ad,
                           String username)
          throws AdapterException {

    // Create elementId for adapter
    // TODO centralized provisioning of element id
    String uuid = UUID.randomUUID().toString();
    ad.setElementId(ad.getElementId() + ":" + uuid);
    ad.setCreatedAt(System.currentTimeMillis());

    // Add EventGrounding to AdapterDescription
    EventGrounding eventGrounding = GroundingService.createEventGrounding();
    ad.setEventGrounding(eventGrounding);

    // Encrypt adapter description to store it in db
    AdapterDescription encryptedAdapterDescription =
            new AdapterEncryptionService(new Cloner().adapterDescription(ad)).encrypt();

    // store in db
    encryptedAdapterDescription.setRev(null);
    String elementId = adapterInstanceStorage.storeAdapter(encryptedAdapterDescription);

    // start when stream adapter
    if (ad instanceof AdapterStreamDescription) {
      startStreamAdapter(elementId);
    }

    // Create stream
    SpDataStream storedDescription = new SourcesManagement().createAdapterDataStream(ad);
    storedDescription.setCorrespondingAdapterId(elementId);
    installDataSource(storedDescription, username);
    LOG.info("Install source (source URL: {} in backend", ad.getElementId());

    return storedDescription.getElementId();
  }


  public AdapterDescription getAdapter(String elementId) throws AdapterException {
    List<AdapterDescription> allAdapters = adapterInstanceStorage.getAllAdapters();

    if (allAdapters != null && elementId != null) {
      for (AdapterDescription ad : allAdapters) {
        if (elementId.equals(ad.getElementId())) {
          return ad;
        }
      }
    }

    throw new AdapterException("Could not find adapter with id: " + elementId);
  }

  /**
   * First the adapter is stopped removed, then the corresponding data source is deleted
   * @param elementId
   * @throws AdapterException
   */
  public void deleteAdapter(String elementId) throws AdapterException {

    // Stop stream adapter
    if (isStreamAdapter(elementId)) {
      try {
        stopStreamAdapter(elementId);
      } catch (AdapterException e) {
        LOG.info("Could not stop adapter: " + elementId, e);
      }
    }

    // Delete adapter
    AdapterDescription ad = adapterInstanceStorage.getAdapter(elementId);
    adapterInstanceStorage.deleteAdapter(elementId);
    LOG.info("Successfully deleted adapter: " + elementId);

    // Delete data stream
    UserService userService = getUserService();
    IPipelineElementDescriptionStorageCache requestor = StorageManager.INSTANCE.getPipelineElementStorage();
    List<SpDataStream> streamsToDelete = requestor
            .getAllDataStreams()
            .stream()
            .filter(spDataStream -> elementId.equals(spDataStream.getCorrespondingAdapterId()))
            .collect(Collectors.toList());
    String username = ad.getUserName();
    if (streamsToDelete.size() > 0) {
      SpDataStream streamToDelete = streamsToDelete.get(0);
      requestor.deleteDataStream(streamToDelete);
      userService.deleteOwnSource(username, streamToDelete.getElementId());
      requestor.refreshDataSourceCache();
      LOG.info("Successfully deleted data stream: " + streamToDelete.getElementId());
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

  public void stopStreamAdapter(String elementId) throws AdapterException {
    AdapterDescription ad = adapterInstanceStorage.getAdapter(elementId);

    if (!isStreamAdapter(elementId)) {
      throw new AdapterException("Adapter " + elementId + "is not a stream adapter.");
    } else {
      WorkerRestClient.stopStreamAdapter(ad.getSelectedEndpointUrl(), (AdapterStreamDescription) ad);
    }
  }

  public void startStreamAdapter(String elementId) throws AdapterException {

    AdapterDescription ad = adapterInstanceStorage.getAdapter(elementId);

    if (!isStreamAdapter(ad)) {
      throw new AdapterException("Adapter " + elementId + "is not a stream adapter.");
    } else {

      try {
        // Find endpoint to start adapter on
        String baseUrl = WorkerPaths.findEndpointUrl(ad.getAppId());

        // Update selected endpoint URL of adapter
        ad.setSelectedEndpointUrl(baseUrl);
        adapterInstanceStorage.updateAdapter(ad);

        // Invoke adapter instance
        WorkerRestClient.invokeStreamAdapter(baseUrl, elementId);

        LOG.info("Started adapter " + elementId + " on: " + baseUrl);
      } catch (NoServiceEndpointsAvailableException e) {
        e.printStackTrace();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }
  }

  private void installDataSource(SpDataStream stream,
                                 String username) throws AdapterException {
    try {
      new DataStreamVerifier(stream).verifyAndAdd(username, true, true);
    } catch (SepaParseException e) {
      LOG.error("Error while installing data source: {}", stream.getElementId(), e);
      throw new AdapterException();
    }
  }

  private boolean isStreamAdapter(String id) {
    AdapterDescription adapterDescription = adapterInstanceStorage.getAdapter(id);
    return isStreamAdapter(adapterDescription);
  }

  private boolean isStreamAdapter(AdapterDescription adapterDescription) {
    return adapterDescription instanceof AdapterStreamDescription;
  }


  private IAdapterStorage getAdapterInstanceStorage() {
    return new AdapterInstanceStorageImpl();
  }

}
