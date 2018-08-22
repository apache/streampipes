/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.management.master;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.connect.adapter.GroundingService;
import org.streampipes.connect.config.ConnectContainerConfig;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.rest.shared.util.JsonLdUtils;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;

import java.io.IOException;
import java.util.List;

import static org.streampipes.connect.rest.SpConnect.deleteDataSource;

public class AdapterMasterManagement {

    private static final Logger logger = LoggerFactory.getLogger(AdapterMasterManagement.class);

    public void addAdapter(AdapterDescription ad, String baseUrl, AdapterStorageImpl adapterStorage)
            throws AdapterException {

        // Add EventGrounding to AdapterDescription
        EventGrounding eventGrounding = GroundingService.createEventGrounding(
                ConnectContainerConfig.INSTANCE.getKafkaHost(), ConnectContainerConfig.INSTANCE.getKafkaPort(), null);
        ad.setEventGrounding(eventGrounding);

        // store in db
        adapterStorage.storeAdapter(ad);


        // start when stream adapter
        if (ad instanceof AdapterStreamDescription) {
            // TODO
            WorkerRestClient.invokeStreamAdapter(baseUrl, (AdapterStreamDescription) ad);
            System.out.println("Start adapter");
//            SpConnect.startStreamAdapter((AdapterStreamDescription) ad, baseUrl);
        }

        List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();
        String adapterCouchdbId = "";
        for (AdapterDescription a : allAdapters) {
           if (a.getAdapterId().equals(ad.getAdapterId())) {
               adapterCouchdbId = a.getId();
           }
        }

        String backendBaseUrl = "http://" + BackendConfig.INSTANCE.getBackendHost() + ":" + "8030" + "/streampipes-backend/api/v2/";
        String userName = ad.getUserName();
        String requestUrl = backendBaseUrl +  "noauth/users/" + userName + "/element";
        logger.info("Request URL: " + requestUrl);

        String elementUrl = backendBaseUrl + "adapter/all/" + adapterCouchdbId;
        logger.info("Element URL: " + elementUrl);

        installDataSource(requestUrl, elementUrl);

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

            logger.info(responseString);
        } catch (IOException e) {
            logger.error("Error while installing data source: " + requestUrl, e);
            throw new AdapterException();
        }

        return true;
    }

    public AdapterDescription getAdapter(String id, AdapterStorageImpl adapterStorage) throws AdapterException {

        List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();

        if (allAdapters != null) {
            for (AdapterDescription ad : allAdapters) {
                if (ad.getUri().equals(id)) {
                    return ad;
                }
            }
        }

        throw new AdapterException("Could not find adapter with id: " + id);
    }

    public void deleteAdapter(String id) throws AdapterException {
        //        // IF Stream adapter delete it
        AdapterStorageImpl adapterStorage = new AdapterStorageImpl();
        boolean isStreamAdapter = isStreamAdapter(id, adapterStorage);

        if (isStreamAdapter) {
            stopStreamAdapter(id, ConnectContainerConfig.INSTANCE.getConnectContainerUrl(), adapterStorage);
        }
        AdapterDescription ad = adapterStorage.getAdapter(id);
        String username = ad.getUserName();

        adapterStorage.deleteAdapter(id);

        String backendBaseUrl = "http://" + BackendConfig.INSTANCE.getBackendHost() + ":" + "8030" +
                "/streampipes-backend/api/v2/noauth/users/"+ username + "/element/";
        backendBaseUrl = backendBaseUrl + id;
        deleteDataSource(backendBaseUrl);


        boolean response = true;

        String responseString = null;
        logger.info("Delete data source in backend with request URL: " + backendBaseUrl);
        try {
            responseString = Request.Delete(backendBaseUrl)
                   .connectTimeout(1000)
                   .socketTimeout(100000)
                   .execute().returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
            responseString = e.toString();
            response = false;
        }

        logger.info("Response of the deletion request" + responseString);
    }

    public List<AdapterDescription> getAllAdapters(AdapterStorageImpl adapterStorage) throws AdapterException {

        List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();

        if (allAdapters == null) {
            throw new AdapterException("Could not get all adapters");
        }

        return allAdapters;
    }

    public static void stopSetAdapter(String adapterId, String baseUrl, AdapterStorageImpl adapterStorage) throws AdapterException {
        String url = baseUrl + "api/v1/stopAdapter/set";

        stopAdapter(adapterId, adapterStorage, url);
    }

    public static void stopStreamAdapter(String adapterId, String baseUrl, AdapterStorageImpl adapterStorage) throws AdapterException {
        String url = baseUrl + "api/v1/stopAdapter/stream";

        stopAdapter(adapterId, adapterStorage, url);
    }

    private static void stopAdapter(String adapterId, AdapterStorageImpl adapterStorage, String url) throws AdapterException {

        //Delete from database
        AdapterDescription ad = adapterStorage.getAdapter(adapterId);

        // Stop execution of adatper
         try {
            logger.info("Trying to stopAdapter adpater on endpoint: " + url);

            // TODO quick fix because otherwise it is not serialized to json-ld
             if (ad.getUri() == null) {
                 logger.error("Adapter uri is null this should not happen " + ad);
             }

            String adapterDescription = toJsonLd(ad);

            // TODO change this to a delete request
            String responseString = Request.Post(url)
                    .bodyString(adapterDescription, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            logger.info("Adapter stopped on endpoint: " + url + " with Response: " + responseString);

        } catch (IOException e) {
            logger.error("Error while stopping adapter with id: " + adapterId, e);
            throw new AdapterException();
        }

    }

    public static boolean isStreamAdapter(String id, AdapterStorageImpl adapterStorage) {
        AdapterDescription ad = adapterStorage.getAdapter(id);

        return ad instanceof AdapterStreamDescription;
    }

    private static <T> String toJsonLd(T object) {
        JsonLdUtils.toJsonLD(object);
        String s = JsonLdUtils.toJsonLD(object);

        if (s == null) {
            logger.error("Could not serialize Object " + object + " into json ld");
        }

        return s;
    }
}
