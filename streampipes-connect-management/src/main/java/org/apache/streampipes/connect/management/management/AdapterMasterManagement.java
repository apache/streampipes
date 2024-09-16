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

  public AdapterMasterManagement(IAdapterStorage adapterInstanceStorage,
                                 AdapterResourceManager adapterResourceManager,
                                 DataStreamResourceManager dataStreamResourceManager,
                                 AdapterMetrics adapterMetrics
  ) {
    this.adapterInstanceStorage = adapterInstanceStorage;
    this.adapterMetrics = adapterMetrics;
    this.adapterResourceManager = adapterResourceManager;
    this.dataStreamResourceManager = dataStreamResourceManager;
  }

  public void addAdapter(AdapterDescription ad,
                           String adapterElementId,
                           String principalSid)
      throws AdapterException {

    // Create elementId for adapter
    var dataStreamElementId = ElementIdGenerator.makeElementId(SpDataStream.class);
    ad.setElementId(adapterElementId);
    ad.setCreatedAt(System.currentTimeMillis());
    ad.setCorrespondingDataStreamElementId(dataStreamElementId);

    // Add EventGrounding to AdapterDescription
    var eventGrounding = GroundingUtils.createEventGrounding();
    ad.setEventGrounding(eventGrounding);

    this.adapterResourceManager.encryptAndCreate(ad);

    // Create stream
    var storedDescription = new SourcesManagement().createAdapterDataStream(ad, dataStreamElementId);
    storedDescription.setCorrespondingAdapterId(adapterElementId);
    installDataSource(storedDescription, principalSid, true);
    LOG.info("Install source (source URL: {} in backend", ad.getElementId());
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
   * First the adapter is stopped removed, then the corresponding data source is deleted
   *
   * @param elementId The elementId of the adapter instance
   * @throws AdapterException when adapter can not be stopped
   */
  public void deleteAdapter(String elementId) throws AdapterException {

    // Stop stream adapter
    try {
      stopStreamAdapter(elementId);
    } catch (AdapterException e) {
      LOG.info("Could not stop adapter: " + elementId, e);
    }

    AdapterDescription adapter = adapterInstanceStorage.getElementById(elementId);
    // Delete adapter
    adapterResourceManager.delete(elementId);
    ExtensionsLogProvider.INSTANCE.remove(elementId);
    LOG.info("Successfully deleted adapter: " + elementId);

    // Delete data stream
    this.dataStreamResourceManager.delete(adapter.getCorrespondingDataStreamElementId());
    LOG.info("Successfully deleted data stream: " + adapter.getCorrespondingDataStreamElementId());
  }

  public List<AdapterDescription> getAllAdapterInstances() throws AdapterException {

    List<AdapterDescription> allAdapters = adapterInstanceStorage.findAll();

    if (allAdapters == null) {
      throw new AdapterException("Could not get all adapters");
    }

    return allAdapters;
  }

  public void stopStreamAdapter(String elementId) throws AdapterException {
    AdapterDescription ad = adapterInstanceStorage.getElementById(elementId);

    WorkerRestClient.stopStreamAdapter(ad.getSelectedEndpointUrl(), ad);
    ExtensionsLogProvider.INSTANCE.reset(elementId);

    // remove the adapter from the metrics manager so that
    // no metrics for this adapter are exposed anymore
    try {
      adapterMetrics.remove(ad.getElementId(), ad.getName());
    } catch (NoSuchElementException e) {
      LOG.error("Could not remove adapter metrics for adapter {}", ad.getName());
    }
  }

  public void startStreamAdapter(String elementId) throws AdapterException {

    var ad = adapterInstanceStorage.getElementById(elementId);

    try {
      // Find endpoint to start adapter on
      var baseUrl = new ExtensionsServiceEndpointGenerator().getEndpointBaseUrl(
          ad.getAppId(),
          SpServiceUrlProvider.ADAPTER,
          ad.getDeploymentConfiguration().getDesiredServiceTags()
      );

      // Update selected endpoint URL of adapter
      ad.setSelectedEndpointUrl(baseUrl);
      adapterInstanceStorage.updateElement(ad);

      // Invoke adapter instance
      WorkerRestClient.invokeStreamAdapter(baseUrl, elementId);

      // register the adapter at the metrics manager so that the AdapterHealthCheck can send metrics
      adapterMetrics.register(ad.getElementId(), ad.getName());

      LOG.info("Started adapter " + elementId + " on: " + baseUrl);
    } catch (NoServiceEndpointsAvailableException e) {
      throw new AdapterException("Could not start adapter due to unavailable service endpoint", e);
    }
  }

  private void installDataSource(SpDataStream stream,
                                 String principalSid,
                                 boolean publicElement) throws AdapterException {
    try {
      new DataStreamVerifier(stream).verifyAndAdd(principalSid, publicElement);
    } catch (SepaParseException e) {
      LOG.error("Error while installing data source: {}", stream.getElementId(), e);
      throw new AdapterException();
    }
  }
}
