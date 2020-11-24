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

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.connect.adapter.GroundingService;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.config.ConnectContainerConfig;
import org.apache.streampipes.connect.container.master.util.AdapterEncryptionService;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.rest.shared.util.JsonLdUtils;
import org.apache.streampipes.storage.couchdb.impl.AdapterStorageImpl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public class AdapterMasterManagement {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterManagement.class);

  public static void startAllStreamAdapters(ConnectWorkerContainer connectWorkerContainer) throws AdapterException {
    AdapterStorageImpl adapterStorage = new AdapterStorageImpl();
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

  public String addAdapter(AdapterDescription ad, String baseUrl, AdapterStorageImpl
          adapterStorage, String username)
          throws AdapterException {

    // Add EventGrounding to AdapterDescription
    EventGrounding eventGrounding = GroundingService.createEventGrounding();
    ad.setEventGrounding(eventGrounding);

    String uuid = UUID.randomUUID().toString();

//    String newId = ConnectContainerConfig.INSTANCE.getConnectContainerMasterUrl() + "api/v1/" + username + "/master/sources/" + uuid;
    String newId = ConnectContainerConfig.INSTANCE.getBackendApiUrl() + "api/v2/connect/" + username + "/master/sources/" + uuid;

    ad.setElementId(newId);


    AdapterDescription encryptedAdapterDescription =
            new AdapterEncryptionService(new Cloner().adapterDescription(ad)).encrypt();
    // store in db
    adapterStorage.storeAdapter(encryptedAdapterDescription);

    // start when stream adapter
    if (ad instanceof AdapterStreamDescription) {
      // TODO
      WorkerRestClient.invokeStreamAdapter(baseUrl, (AdapterStreamDescription) ad);
      LOG.info("Start adapter");
//            SpConnect.startStreamAdapter((AdapterStreamDescription) ad, baseUrl);
    }

    // backend url is used to install data source in streampipes
    String backendBaseUrl = ConnectContainerConfig.INSTANCE.getBackendApiUrl() + "api/v2/";
    String requestUrl = backendBaseUrl + "noauth/users/" + username + "/element";

    LOG.info("Install source (source URL: " + newId + " in backend over URL: " + requestUrl);

    installDataSource(requestUrl, newId);

    return new SourcesManagement().getAdapterDataSource(newId).getElementId();
  }

  public boolean installDataSource(String requestUrl, String elementIdUrl) throws AdapterException {

    try {
      String responseString = Request.Post(requestUrl)
              .bodyForm(
                      Form.form()
                              .add("uri", elementIdUrl)
                              .add("publicElement", "true").build())
              .connectTimeout(1000)
              .socketTimeout(100000)
              .execute().returnContent().asString();

      LOG.info(responseString);
    } catch (IOException e) {
      LOG.error("Error while installing data source: " + requestUrl, e);
      throw new AdapterException();
    }

    return true;
  }

  public AdapterDescription getAdapter(String id, AdapterStorageImpl adapterStorage) throws AdapterException {

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
    AdapterStorageImpl adapterStorage = new AdapterStorageImpl();
    boolean isStreamAdapter = isStreamAdapter(id, adapterStorage);

    if (isStreamAdapter) {
      stopStreamAdapter(id, baseUrl, adapterStorage);
    }
    AdapterDescription ad = adapterStorage.getAdapter(id);
    String username = ad.getUserName();

    adapterStorage.deleteAdapter(id);

    String backendBaseUrl = ConnectContainerConfig.INSTANCE.getBackendApiUrl() + "api/v2/noauth/users/" + username + "/element/delete";

    String elementUrl = ad.getUri();

    String responseString = null;
    LOG.info("Delete data source in backend with request URL: " + backendBaseUrl);
    try {
      responseString = Request.Post(backendBaseUrl)
              .connectTimeout(1000)
              .socketTimeout(100000)
              .bodyForm(Form.form()
                      .add("uri", elementUrl).build())
              .execute().returnContent().asString();
    } catch (IOException e) {
      e.printStackTrace();
      responseString = e.toString();
    }

    LOG.info("Response of the deletion request" + responseString);
  }

  public List<AdapterDescription> getAllAdapters(AdapterStorageImpl adapterStorage) throws AdapterException {

    List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();

    if (allAdapters == null) {
      throw new AdapterException("Could not get all adapters");
    }

    return allAdapters;
  }

  public static void stopSetAdapter(String adapterId, String baseUrl, AdapterStorageImpl adapterStorage) throws AdapterException {

    AdapterSetDescription ad = (AdapterSetDescription) adapterStorage.getAdapter(adapterId);

    WorkerRestClient.stopSetAdapter(baseUrl, ad);
  }

  public static void stopStreamAdapter(String adapterId, String baseUrl, AdapterStorageImpl adapterStorage) throws AdapterException {
    AdapterStreamDescription ad = (AdapterStreamDescription) adapterStorage.getAdapter(adapterId);

    WorkerRestClient.stopStreamAdapter(baseUrl, ad);
  }

  public static boolean isStreamAdapter(String id, AdapterStorageImpl adapterStorage) {
    AdapterDescription ad = adapterStorage.getAdapter(id);

    return ad instanceof AdapterStreamDescription;
  }

  private static <T> String toJsonLd(T object) {
    JsonLdUtils.toJsonLD(object);
    String s = JsonLdUtils.toJsonLD(object);

    if (s == null) {
      LOG.error("Could not serialize Object " + object + " into json ld");
    }

    return s;
  }
}
