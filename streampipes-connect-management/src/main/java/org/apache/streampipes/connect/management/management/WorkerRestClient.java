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
import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.resource.management.secret.SecretProvider;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.impl.AdapterInstanceStorageImpl;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * This client can be used to interact with the adapter workers executing the adapter instances
 */
public class WorkerRestClient {

  private static final Logger logger = LoggerFactory.getLogger(WorkerRestClient.class);

  public static void invokeStreamAdapter(String endpointUrl,
                                         String elementId) throws AdapterException {
    var adapterStreamDescription = getAndDecryptAdapter(elementId);
    var url = endpointUrl + WorkerPaths.getStreamInvokePath();

    startAdapter(url, adapterStreamDescription);
    updateStreamAdapterStatus(adapterStreamDescription.getElementId(), true);
  }

  public static void stopStreamAdapter(String baseUrl,
                                       AdapterDescription adapterStreamDescription) throws AdapterException {
    String url = baseUrl + WorkerPaths.getStreamStopPath();

    var ad =
        getAdapterDescriptionById(new AdapterInstanceStorageImpl(), adapterStreamDescription.getElementId());

    stopAdapter(ad, url);
    updateStreamAdapterStatus(adapterStreamDescription.getElementId(), false);
  }

  public static List<AdapterDescription> getAllRunningAdapterInstanceDescriptions(String url) throws AdapterException {
    try {
      logger.info("Requesting all running adapter description instances: " + url);
      var responseString = ExtensionServiceExecutions
          .extServiceGetRequest(url)
          .execute().returnContent().asString();

      List<AdapterDescription> result = JacksonSerializer.getObjectMapper().readValue(responseString, List.class);

      return result;
    } catch (IOException e) {
      logger.error("List of running adapters could not be fetched", e);
      throw new AdapterException("List of running adapters could not be fetched from: " + url);
    }
  }

  public static void startAdapter(String url,
                                  AdapterDescription ad) throws AdapterException {
    logger.info("Trying to start adapter on endpoint {} ", url);
    triggerAdapterStateChange(ad, url, "started");
  }


  public static void stopAdapter(AdapterDescription ad,
                                 String url) throws AdapterException {

    logger.info("Trying to stop adapter on endpoint {} ", url);
    triggerAdapterStateChange(ad, url, "stopped");
  }

  private static void triggerAdapterStateChange(AdapterDescription ad,
                                                String url,
                                                String action) throws AdapterException {
    try {
      String adapterDescription = JacksonSerializer.getObjectMapper().writeValueAsString(ad);

      var response = triggerPost(url, ad.getElementId(), adapterDescription);
      var responseString = getResponseBody(response);

      if (response.getStatusLine().getStatusCode() != 200) {
        var exception = getSerializer().readValue(responseString, AdapterException.class);
        throw new AdapterException(exception.getMessage(), exception.getCause());
      }

      logger.info("Adapter {} on endpoint: " + url + " with Response: ", ad.getName() + responseString);

    } catch (IOException e) {
      logger.error("Adapter was not {} successfully", action, e);
      throw new AdapterException("Adapter was not " + action + " successfully with url " + url, e);
    }
  }

  private static String getResponseBody(HttpResponse response) throws IOException {
    return IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
  }

  private static HttpResponse triggerPost(String url,
                                          String elementId,
                                          String payload) throws IOException {
    var request = ExtensionServiceExecutions.extServicePostRequest(url, elementId, payload);
    return request.execute().returnResponse();
  }

  public static RuntimeOptionsResponse getConfiguration(String workerEndpoint,
                                                        String appId,
                                                        RuntimeOptionsRequest runtimeOptionsRequest)
      throws AdapterException, SpConfigurationException {
    String url = workerEndpoint + WorkerPaths.getRuntimeResolvablePath(appId);

    try {
      String payload = JacksonSerializer.getObjectMapper().writeValueAsString(runtimeOptionsRequest);
      var response = ExtensionServiceExecutions.extServicePostRequest(url, payload)
          .execute()
          .returnResponse();

      String responseString = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);

      if (response.getStatusLine().getStatusCode() == 200) {
        return getSerializer().readValue(responseString, RuntimeOptionsResponse.class);
      } else {
        var exception = getSerializer().readValue(responseString, SpConfigurationException.class);
        throw new SpConfigurationException(exception.getMessage(), exception.getCause());
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new AdapterException("Could not resolve runtime configurations from " + url);
    }
  }

  public static String getAssets(String workerPath) throws AdapterException {
    String url = workerPath + "/assets";
    logger.info("Trying to Assets from endpoint: " + url);

    try {
      return Request.Get(url)
          .connectTimeout(1000)
          .socketTimeout(100000)
          .execute().returnContent().asString();
    } catch (IOException e) {
      logger.error(e.getMessage());
      throw new AdapterException("Could not get assets endpoint: " + url);
    }

  }

  public static byte[] getIconAsset(String baseUrl) throws AdapterException {
    String url = baseUrl + "/assets/icon";

    try {
      byte[] responseString = Request.Get(url)
          .connectTimeout(1000)
          .socketTimeout(100000)
          .execute().returnContent().asBytes();
      return responseString;
    } catch (IOException e) {
      logger.error(e.getMessage());
      throw new AdapterException("Could not get icon endpoint: " + url);
    }
  }

  public static String getDocumentationAsset(String baseUrl) throws AdapterException {
    String url = baseUrl + "/assets/documentation";

    try {
      return Request.Get(url)
          .connectTimeout(1000)
          .socketTimeout(100000)
          .execute().returnContent().asString();
    } catch (IOException e) {
      logger.error(e.getMessage());
      throw new AdapterException("Could not get documentation endpoint: " + url);
    }
  }


  private static AdapterDescription getAdapterDescriptionById(AdapterInstanceStorageImpl adapterStorage, String id) {
    AdapterDescription adapterDescription = null;
    List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();
    for (AdapterDescription a : allAdapters) {
      if (a.getElementId().endsWith(id)) {
        adapterDescription = a;
      }
    }

    return adapterDescription;
  }

  private static void updateStreamAdapterStatus(String adapterId,
                                                boolean running) {
    var adapter = getAndDecryptAdapter(adapterId);
    adapter.setRunning(running);
    encryptAndUpdateAdapter(adapter);
  }

  private static void encryptAndUpdateAdapter(AdapterDescription adapter) {
    AdapterDescription encryptedDescription = new Cloner().adapterDescription(adapter);
    SecretProvider.getEncryptionService().apply(encryptedDescription);
    getAdapterStorage().updateAdapter(encryptedDescription);
  }

  private static AdapterDescription getAndDecryptAdapter(String adapterId) {
    AdapterDescription adapter = getAdapterStorage().getAdapter(adapterId);
    SecretProvider.getDecryptionService().apply(adapter);
    return adapter;
  }

  private static IAdapterStorage getAdapterStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage();
  }

  private static ObjectMapper getSerializer() {
    return JacksonSerializer.getObjectMapper();
  }
}

