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
import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetrics;
import org.apache.streampipes.connect.management.util.GroundingUtils;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.monitoring.pipeline.ExtensionsLogProvider;
import org.apache.streampipes.manager.verification.DataStreamVerifier;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.util.ElementIdGenerator;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class is responsible for managing all the adapter instances which are executed on worker nodes
 */
public class AdapterMasterManagement {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterManagement.class);

  private final IAdapterStorage adapterInstanceStorage;
  private final AdapterMetrics adapterMetrics;
  private final AdapterResourceManager adapterResourceManager;

  private final DataStreamResourceManager dataStreamResourceManager;

  public AdapterMasterManagement(
      IAdapterStorage adapterInstanceStorage,
      AdapterResourceManager adapterResourceManager,
      DataStreamResourceManager dataStreamResourceManager,
      AdapterMetrics adapterMetrics
  ) {
    this.adapterInstanceStorage = adapterInstanceStorage;
    this.adapterMetrics = adapterMetrics;
    this.adapterResourceManager = adapterResourceManager;
    this.dataStreamResourceManager = dataStreamResourceManager;
  }

  public void addAdapter(
      AdapterDescription adapterDescription,
      String adapterId,
      String principalSid
  )
      throws AdapterException {

    // Create elementId for datastream
    var dataStreamElementId = ElementIdGenerator.makeElementId(SpDataStream.class);
    adapterDescription.setElementId(adapterId);
    adapterDescription.setCreatedAt(System.currentTimeMillis());
    adapterDescription.setCorrespondingDataStreamElementId(dataStreamElementId);

    // Add EventGrounding to AdapterDescription
    var eventGrounding = GroundingUtils.createEventGrounding();
    adapterDescription.setEventGrounding(eventGrounding);

    this.adapterResourceManager.encryptAndCreate(adapterDescription);

    // Stream is only created if the adpater is successfully stored
    createDataStreamForAdapter(adapterDescription, adapterId, dataStreamElementId, principalSid);
  }

  private void createDataStreamForAdapter(
      AdapterDescription adapterDescription,
      String adapterId,
      String streamId,
      String principalSid
  ) throws AdapterException {
    var storedDescription = new SourcesManagement()
        .createAdapterDataStream(adapterDescription, streamId);
    storedDescription.setCorrespondingAdapterId(adapterId);
    installDataSource(storedDescription, principalSid);
    LOG.info("Install source (source URL: {} in backend", adapterDescription.getElementId());
  }

  public AdapterDescription getAdapter(String elementId) throws AdapterException {
    List<AdapterDescription> allAdapters = adapterInstanceStorage.findAll();

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
   * This method deletes the adapter and the related resources inlcuding the data stream, and the asset links in the
   * asset model
   *
   * @param elementId The elementId of the adapter instance
   */
  public void deleteAdapter(String elementId) {

    var adapterDescription = getAdapterDescription(elementId);

    stopAdapterWithLogging(elementId);

    deleteAdaterFromCouchDbAndFromLoggingService(elementId);

    deleteCorrespondingDataStream(adapterDescription);
  }

  private void stopAdapterWithLogging(String elementId) {
    LOG.info("Attempting to stop adapter: {}", elementId);
    try {
      stopAdapter(elementId);
      LOG.info("Successfully stopped adapter with id: {}", elementId);
    } catch (AdapterException e) {
      LOG.error("Failed to stop adapter with id: {}", elementId, e);
    }
  }

  private void deleteAdaterFromCouchDbAndFromLoggingService(String elementId) {
    adapterResourceManager.delete(elementId);
    ExtensionsLogProvider.INSTANCE.remove(elementId);
    LOG.info("Successfully deleted adapter in couchdb: {}", elementId);
  }

  private void deleteCorrespondingDataStream(AdapterDescription adapterDescription) {
    var correspondingDataStreamElementId = adapterDescription.getCorrespondingDataStreamElementId();
    dataStreamResourceManager.delete(correspondingDataStreamElementId);
    LOG.info("Successfully deleted data stream in couchdb: {}", correspondingDataStreamElementId);
  }

  public List<AdapterDescription> getAllAdapterInstances() {
    return adapterInstanceStorage.findAll();
  }


  public void stopAdapter(String elementId) throws AdapterException {
    var adapterDescription = getAdapterDescription(elementId);

    WorkerRestClient.stopStreamAdapter(adapterDescription.getSelectedEndpointUrl(), adapterDescription);
    ExtensionsLogProvider.INSTANCE.reset(elementId);

    // remove the adapter from the metrics manager so that
    // no metrics for this adapter are exposed anymore
    try {
      adapterMetrics.remove(adapterDescription.getElementId(), adapterDescription.getName());
    } catch (NoSuchElementException e) {
      LOG.error("Could not remove adapter metrics for adapter {}", adapterDescription.getName());
    }
  }

  public void startStreamAdapter(String elementId) throws AdapterException {

    var adapterDescription = getAdapterDescription(elementId);

    try {
      // Find endpoint to start adapter on
      var baseUrl = new ExtensionsServiceEndpointGenerator().getEndpointBaseUrl(
          adapterDescription.getAppId(),
          SpServiceUrlProvider.ADAPTER,
          adapterDescription.getDeploymentConfiguration()
                            .getDesiredServiceTags()
      );

      // Update selected endpoint URL of adapter
      adapterDescription.setSelectedEndpointUrl(baseUrl);
      adapterInstanceStorage.updateElement(adapterDescription);

      // Invoke adapter instance
      WorkerRestClient.invokeStreamAdapter(baseUrl, elementId);

      // register the adapter at the metrics manager so that the AdapterHealthCheck can send metrics
      adapterMetrics.register(adapterDescription.getElementId(), adapterDescription.getName());

      LOG.info("Started adapter " + elementId + " on: " + baseUrl);
    } catch (NoServiceEndpointsAvailableException e) {
      throw new AdapterException("Could not start adapter due to unavailable service endpoint", e);
    }
  }

  private void installDataSource(
      SpDataStream stream,
      String principalSid
  ) throws AdapterException {
    try {
      new DataStreamVerifier(stream).verifyAndAdd(principalSid, false);
    } catch (SepaParseException e) {
      LOG.error("Error while installing data source: {}", stream.getElementId(), e);
      throw new AdapterException();
    }
  }

  private AdapterDescription getAdapterDescription(String elementId) {
    return adapterInstanceStorage.getElementById(elementId);
  }
}
