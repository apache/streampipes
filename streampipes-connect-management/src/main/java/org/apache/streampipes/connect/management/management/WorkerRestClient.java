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
import org.apache.streampipes.connect.management.util.WorkerPaths;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationResult;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
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

  public void invokeStreamAdapter(String baseUrl,
                                  String elementId) throws AdapterException {
    var adapterStreamDescription = getAndDecryptAdapter(elementId);
    var url = baseUrl + WorkerPaths.getStreamInvokePath();

    startAdapter(url, adapterStreamDescription);
    updateStreamAdapterStatus(adapterStreamDescription.getElementId(), true);
  }

  public void stopStreamAdapter(String baseUrl,
                                AdapterDescription adapterStreamDescription) throws AdapterException {
    String url = baseUrl + WorkerPaths.getStreamStopPath();

    var ad = getAdapterDescriptionById(new AdapterInstanceStorageImpl(), adapterStreamDescription.getElementId());

    stopAdapter(ad, url);
    updateStreamAdapterStatus(adapterStreamDescription.getElementId(), false);
  }

  public List<AdapterDescription> getAllRunningAdapterInstanceDescriptions(String url) throws AdapterException {
    try {
      var responseString = requestManager.requestRunningAdapters(url).responseBody();

      return JacksonSerializer.getObjectMapper().readValue(responseString, List.class);
    } catch (IOException e) {
      throw new AdapterException("List of running adapters could not be fetched from: " + url);
    }
  }

  private void startAdapter(String url,
                            AdapterDescription ad) throws AdapterException {
    LOG.debug("Trying to start adapter on endpoint {} ", url);
    triggerAdapterStateChange(ad, url, "started");
  }


  private void stopAdapter(AdapterDescription ad,
                           String url) throws AdapterException {

    LOG.debug("Trying to stop adapter on endpoint {} ", url);
    triggerAdapterStateChange(ad, url, "stopped");
  }

  private void triggerAdapterStateChange(AdapterDescription ad,
                                         String url,
                                         String action) throws AdapterException {
    try {
      String adapterDescription = JacksonSerializer.getObjectMapper().writeValueAsString(ad);

      var response =
          triggerPost(url, ad.getCorrespondingDataStreamElementId(), adapterDescription);
      var responseString = response.responseBody();

      if (response.statusCode() != HttpStatus.SC_OK) {
        var exception = getSerializer().readValue(responseString, AdapterException.class);
        throw new AdapterException(exception.getMessage(), exception.getCause());
      }
    } catch (IOException e) {
      LOG.error("Adapter was not {} successfully", action, e);
      throw new AdapterException("Adapter was not " + action + " successfully with url " + url, e);
    }
  }

  private ExtensionServiceOperationResult triggerPost(String url,
                                                      String elementId,
                                                      String payload) throws IOException {
    return requestManager.requestAdapterStateChange(url, elementId, payload);
  }

  public RuntimeOptionsResponse getConfiguration(String baseUrl,
                                                 String appId,
                                                 RuntimeOptionsRequest runtimeOptionsRequest)
      throws AdapterException, SpConfigurationException {
    String url = baseUrl + WorkerPaths.getRuntimeResolvablePath(appId);

    try {
      String payload = JacksonSerializer.getObjectMapper().writeValueAsString(runtimeOptionsRequest);
      var response = requestManager.requestRuntimeOptions(url, payload);
      String responseString = response.responseBody();

      if (response.statusCode() == HttpStatus.SC_OK) {
        return getSerializer().readValue(responseString, RuntimeOptionsResponse.class);
      } else {
        var exception = getSerializer().readValue(responseString, SpConfigurationException.class);
        throw new SpConfigurationException(exception.getMessage(), exception.getCause());
      }
    } catch (IOException e) {
      throw new AdapterException("Could not resolve runtime configurations from " + url, e);
    }
  }

  public String getAssets(String workerPath) throws AdapterException {
    String url = workerPath + "/assets";
    LOG.info("Trying to Assets from endpoint: " + url);

    try {
      var response = requestManager.requestAdapterAssets(url);

      if (!response.isSuccess()) {
        throw new AdapterException("Could not get assets endpoint: " + url);
      }
      return response.responseBody();
    } catch (IOException e) {
      LOG.error(e.getMessage());
      throw new AdapterException("Could not get assets endpoint: " + url);
    }

  }

  public byte[] getIconAsset(String baseUrl) throws AdapterException {
    String url = baseUrl + "/assets/icon";

    try {
      var response = requestManager.requestAdapterIconAsset(url);
      if (!response.isSuccess()) {
        throw new AdapterException("Could not get icon endpoint: " + url);
      }
      return response.responseBytes();
    } catch (IOException e) {
      LOG.error(e.getMessage());
      throw new AdapterException("Could not get icon endpoint: " + url);
    }
  }

  public String getDocumentationAsset(String baseUrl) throws AdapterException {
    String url = baseUrl + "/assets/documentation";

    try {
      var response = requestManager.requestAdapterDocumentationAsset(url);
      if (!response.isSuccess()) {
        throw new AdapterException("Could not get documentation endpoint: " + url);
      }
      return response.responseBody();
    } catch (IOException e) {
      LOG.error(e.getMessage());
      throw new AdapterException("Could not get documentation endpoint: " + url);
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
