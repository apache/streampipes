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


import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationResult;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.resource.management.secret.SecretProvider;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * This client can be used to interact with the adapter workers executing the adapter instances
 */
public class WorkerRestClient {

  private static final Logger LOG = LoggerFactory.getLogger(WorkerRestClient.class);
  private final ExtensionServiceRequestManager requestManager;
  private final SpResourceManager resourceManager;

  public WorkerRestClient(ExtensionServiceRequestManager requestManager,
                          SpResourceManager resourceManager) {
    this.requestManager = requestManager;
    this.resourceManager = resourceManager;
  }

  public void invokeStreamAdapter(SpServiceRegistration service,
                                  String elementId) throws AdapterException {
    var adapterStreamDescription = getAndDecryptAdapter(elementId);
    var requestTarget = ExtensionServiceRequestTargets.adapterStart(service);
    startAdapter(requestTarget, adapterStreamDescription);
    updateStreamAdapterStatus(adapterStreamDescription.getElementId(), true);
  }

  public void stopStreamAdapter(SpServiceRegistration service,
                                AdapterDescription adapterStreamDescription) throws AdapterException {
    var requestTarget = ExtensionServiceRequestTargets.adapterStop(service);
    var ad = getAdapterDescriptionById(resourceManager.manageAdapters().getDb(), adapterStreamDescription.getElementId());

    stopAdapter(requestTarget, ad);
    updateStreamAdapterStatus(adapterStreamDescription.getElementId(), false);
  }

  private void startAdapter(ExtensionServiceRequestTarget requestTarget,
                            AdapterDescription ad) throws AdapterException {
    LOG.debug("Trying to start adapter on endpoint {} ", requestTarget.serviceId());
    triggerAdapterStateChange(ad, requestTarget, "started");
  }


  private void stopAdapter(ExtensionServiceRequestTarget requestTarget,
                           AdapterDescription ad) throws AdapterException {

    LOG.debug("Trying to stop adapter on endpoint {} ", requestTarget.serviceId());
    triggerAdapterStateChange(ad, requestTarget, "stopped");
  }

  private void triggerAdapterStateChange(AdapterDescription ad,
                                         ExtensionServiceRequestTarget requestTarget,
                                         String action) throws AdapterException {
    try {
      String adapterDescription = JacksonSerializer.getObjectMapper().writeValueAsString(ad);

      var response =
          triggerPost(requestTarget, ad.getCorrespondingDataStreamElementId(), adapterDescription, resourceManager);
      var responseString = response.responseBody();

      if (response.statusCode() != HttpStatus.SC_OK) {
        LOG.error(
            "Adapter state change failed for adapter {}, service {}, action {}, status {}, response body {}",
            ad.getElementId(),
            requestTarget.serviceId(),
            action,
            response.statusCode(),
            responseString
        );
        var exception = getSerializer().readValue(responseString, AdapterException.class);
        throw new AdapterException(exception.getMessage(), exception.getCause());
      }
    } catch (IOException e) {
      LOG.error("Adapter was not {} successfully", action, e);
      throw new AdapterException("Adapter was not " + action + " successfully with serviceId " + requestTarget.serviceId(), e);
    }
  }

  private ExtensionServiceOperationResult triggerPost(ExtensionServiceRequestTarget requestTarget,
                                                      String elementId,
                                                      String payload,
                                                      SpResourceManager resourceManager) throws IOException {
    return requestManager.request(
        ExtensionServiceRequests.adapterStateChange(requestTarget, elementId, payload, resourceManager)
    );
  }

  public RuntimeOptionsResponse getConfiguration(SpServiceRegistration service,
                                                 String appId,
                                                 RuntimeOptionsRequest runtimeOptionsRequest,
                                                 SpResourceManager resourceManager)
      throws AdapterException, SpConfigurationException {

    try {
      String payload = JacksonSerializer.getObjectMapper().writeValueAsString(runtimeOptionsRequest);
      var requestTarget = ExtensionServiceRequestTargets.adapterRuntimeOptions(service, appId);
      var response = requestManager.request(
          ExtensionServiceRequests.runtimeOptions(requestTarget, payload, resourceManager)
      );
      String responseString = response.responseBody();

      if (response.statusCode() == HttpStatus.SC_OK) {
        return getSerializer().readValue(responseString, RuntimeOptionsResponse.class);
      } else {
        var exception = getSerializer().readValue(responseString, SpConfigurationException.class);
        throw new SpConfigurationException(exception.getMessage(), exception.getCause());
      }
    } catch (IOException e) {
      throw new AdapterException("Could not resolve runtime configurations from " + service.getSvcId(), e);
    }
  }

  public String getAssets(SpServiceRegistration service,
                          String appId) throws AdapterException {
    try {
      var requestTarget = ExtensionServiceRequestTargets.adapterAssets(service, appId);
      var response = requestManager.request(ExtensionServiceRequests.adapterAssets(requestTarget));

      if (!response.isSuccess()) {
        throw new AdapterException("Could not get assets endpoint: " + service.getServiceUrl());
      }
      return response.responseBody();
    } catch (IOException e) {
      LOG.error(e.getMessage());
      throw new AdapterException("Could not get assets endpoint: " + service.getServiceUrl());
    }

  }

  public byte[] getIconAsset(SpServiceRegistration service,
                             String appId) throws AdapterException {
    try {
      var requestTarget = ExtensionServiceRequestTargets.adapterIconAsset(service, appId);
      var response = requestManager.request(ExtensionServiceRequests.adapterIconAsset(requestTarget));
      if (!response.isSuccess()) {
        throw new AdapterException("Could not get icon endpoint: " + service.getServiceUrl());
      }
      return response.responseBytes();
    } catch (IOException e) {
      LOG.error(e.getMessage());
      throw new AdapterException("Could not get icon endpoint: " + service.getServiceUrl());
    }
  }

  public String getDocumentationAsset(SpServiceRegistration service,
                                      String appId) throws AdapterException {
    try {
      var requestTarget = ExtensionServiceRequestTargets.adapterDocumentationAsset(service, appId);
      var response = requestManager.request(ExtensionServiceRequests.adapterDocumentationAsset(requestTarget));
      if (!response.isSuccess()) {
        throw new AdapterException("Could not get documentation endpoint: " + service.getServiceUrl());
      }
      return response.responseBody();
    } catch (IOException e) {
      LOG.error(e.getMessage());
      throw new AdapterException("Could not get documentation endpoint: " + service.getServiceUrl());
    }
  }


  private AdapterDescription getAdapterDescriptionById(IAdapterStorage adapterStorage,
                                                       String id) {
    AdapterDescription adapterDescription = null;
    List<AdapterDescription> allAdapters = adapterStorage.findAll();
    for (AdapterDescription a : allAdapters) {
      if (a.getElementId().endsWith(id)) {
        adapterDescription = a;
      }
    }

    return adapterDescription;
  }

  private void updateStreamAdapterStatus(String adapterId,
                                         boolean running) {
    var adapter = getAndDecryptAdapter(adapterId);
    adapter.setRunning(running);
    encryptAndUpdateAdapter(adapter);
  }

  private void encryptAndUpdateAdapter(AdapterDescription adapter) {
    AdapterDescription encryptedDescription = new Cloner().adapterDescription(adapter);
    SecretProvider.getEncryptionService().apply(encryptedDescription);
    getAdapterStorage().updateElement(encryptedDescription);
  }

  private AdapterDescription getAndDecryptAdapter(String adapterId) {
    AdapterDescription adapter = getAdapterStorage().getElementById(adapterId);
    SecretProvider.getDecryptionService().apply(adapter);
    return adapter;
  }

  private IAdapterStorage getAdapterStorage() {
    return resourceManager.manageAdapters().getDb();
  }

  private ObjectMapper getSerializer() {
    return JacksonSerializer.getObjectMapper();
  }
}
