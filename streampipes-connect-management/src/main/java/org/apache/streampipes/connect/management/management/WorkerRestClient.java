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
import org.apache.streampipes.manager.api.extensions.param.AdapterAssetDocumentationParameters;
import org.apache.streampipes.manager.api.extensions.param.AdapterAssetIconParameters;
import org.apache.streampipes.manager.api.extensions.param.AdapterAssetParameters;
import org.apache.streampipes.manager.api.extensions.param.AdapterRuntimeOptionsParameters;
import org.apache.streampipes.manager.api.extensions.param.AdapterStartParameters;
import org.apache.streampipes.manager.api.extensions.param.AdapterStopParameters;
import org.apache.streampipes.manager.api.extensions.param.ExtensionServiceOperationParameters;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.resource.management.secret.SecretProvider;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.impl.connect.AdapterInstanceStorageImpl;
import org.apache.streampipes.storage.management.StorageDispatcher;

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

  public WorkerRestClient(ExtensionServiceRequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void invokeStreamAdapter(SpServiceRegistration service,
                                  String elementId) throws AdapterException {
    var adapterStreamDescription = getAndDecryptAdapter(elementId);
    var params = new AdapterStartParameters();
    startAdapter(service, params, adapterStreamDescription);
    updateStreamAdapterStatus(adapterStreamDescription.getElementId(), true);
  }

  public void stopStreamAdapter(SpServiceRegistration service,
                                AdapterDescription adapterStreamDescription) throws AdapterException {
    var params = new AdapterStopParameters();
    var ad = getAdapterDescriptionById(new AdapterInstanceStorageImpl(), adapterStreamDescription.getElementId());

    stopAdapter(service, params, ad);
    updateStreamAdapterStatus(adapterStreamDescription.getElementId(), false);
  }

  private void startAdapter(SpServiceRegistration service,
                            ExtensionServiceOperationParameters params,
                            AdapterDescription ad) throws AdapterException {
    LOG.debug("Trying to start adapter on endpoint {} ", service.getSvcId());
    triggerAdapterStateChange(ad, service, params,"started");
  }


  private void stopAdapter(SpServiceRegistration service,
                           ExtensionServiceOperationParameters params,
                           AdapterDescription ad) throws AdapterException {

    LOG.debug("Trying to stop adapter on endpoint {} ", service.getSvcId());
    triggerAdapterStateChange(ad, service, params, "stopped");
  }

  private void triggerAdapterStateChange(AdapterDescription ad,
                                         SpServiceRegistration service,
                                         ExtensionServiceOperationParameters params,
                                         String action) throws AdapterException {
    try {
      String adapterDescription = JacksonSerializer.getObjectMapper().writeValueAsString(ad);

      var response =
          triggerPost(service, params, ad.getCorrespondingDataStreamElementId(), adapterDescription);
      var responseString = response.responseBody();

      if (response.statusCode() != HttpStatus.SC_OK) {
        var exception = getSerializer().readValue(responseString, AdapterException.class);
        throw new AdapterException(exception.getMessage(), exception.getCause());
      }
    } catch (IOException e) {
      LOG.error("Adapter was not {} successfully", action, e);
      throw new AdapterException("Adapter was not " + action + " successfully with serviceId " + service.getSvcId(), e);
    }
  }

  private ExtensionServiceOperationResult triggerPost(SpServiceRegistration service,
                                                      ExtensionServiceOperationParameters params,
                                                      String elementId,
                                                      String payload) throws IOException {
    return requestManager.requestAdapterStateChange(
        new ExtensionServiceRequestTarget(
            service.getServiceUrl(),
            service.getSvcId(),
            params
        ),
        elementId,
        payload
    );
  }

  public RuntimeOptionsResponse getConfiguration(SpServiceRegistration service,
                                                 String appId,
                                                 RuntimeOptionsRequest runtimeOptionsRequest)
      throws AdapterException, SpConfigurationException {

    try {
      String payload = JacksonSerializer.getObjectMapper().writeValueAsString(runtimeOptionsRequest);
      var requestTarget = new ExtensionServiceRequestTarget(
          service.getServiceUrl(),
          service.getSvcId(),
          new AdapterRuntimeOptionsParameters(appId)
      );
      var response = requestManager.requestRuntimeOptions(requestTarget, payload);
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
      var requestTarget = new ExtensionServiceRequestTarget(
          service.getServiceUrl(),
          service.getSvcId(),
          new AdapterAssetParameters(appId)
      );
      var response = requestManager.requestAdapterAssets(requestTarget);

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
      var requestTarget = new ExtensionServiceRequestTarget(
          service.getServiceUrl(),
          service.getSvcId(),
          new AdapterAssetIconParameters(appId)
      );
      var response = requestManager.requestAdapterIconAsset(requestTarget);
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
      var requestTarget = new ExtensionServiceRequestTarget(
          service.getServiceUrl(),
          service.getSvcId(),
          new AdapterAssetDocumentationParameters(appId)
      );
      var response = requestManager.requestAdapterDocumentationAsset(requestTarget);
      if (!response.isSuccess()) {
        throw new AdapterException("Could not get documentation endpoint: " + service.getServiceUrl());
      }
      return response.responseBody();
    } catch (IOException e) {
      LOG.error(e.getMessage());
      throw new AdapterException("Could not get documentation endpoint: " + service.getServiceUrl());
    }
  }


  private AdapterDescription getAdapterDescriptionById(AdapterInstanceStorageImpl adapterStorage, String id) {
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
    return StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage();
  }

  private ObjectMapper getSerializer() {
    return JacksonSerializer.getObjectMapper();
  }
}
