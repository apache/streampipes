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

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterSetDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.rest.shared.util.JsonLdUtils;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;

import java.io.IOException;
import java.util.UUID;

public class WorkerRestClient {

    private static final Logger logger = LoggerFactory.getLogger(WorkerRestClient.class);

    public static void invokeStreamAdapter(String baseUrl, AdapterStreamDescription adapterStreamDescription) throws AdapterException {

        String url = baseUrl + "worker/stream/invoke";

        startAdapter(url, adapterStreamDescription);
    }

    public static void stopStreamAdapter(String baseUrl, AdapterStreamDescription adapterStreamDescription) throws AdapterException {
        String url = baseUrl + "worker/stream/stop";

        stopAdapter(adapterStreamDescription.getId(), new AdapterStorageImpl(), url);
    }

    public static void invokeSetAdapter(String baseUrl, AdapterSetDescription adapterSetDescription) throws AdapterException {
        String url = baseUrl + "worker/set/invoke";

        startAdapter(url, adapterSetDescription);
    }

    public static void stopSetAdapter(String baseUrl, AdapterSetDescription adapterSetDescription) throws AdapterException {
        String url = baseUrl + "worker/set/stop";

        stopAdapter(adapterSetDescription.getAdapterId(), new AdapterStorageImpl(), url);
    }

    public static void startAdapter(String url, AdapterDescription ad) throws AdapterException {
        try {
            logger.info("Trying to start adpater on endpoint: " + url);

            // this ensures that all adapters have a valid uri otherwise the json-ld serializer fails
            if (ad.getUri() == null) {
                ad.setUri("https://streampipes.org/adapter/" + UUID.randomUUID());
            }

            String adapterDescription = JsonLdUtils.toJsonLD(ad);

            String responseString = Request.Post(url)
                    .bodyString(adapterDescription, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            logger.info("Adapter started on endpoint: " + url + " with Response: " + responseString);

        } catch (IOException e) {
            logger.error("Adapter did not start", e);
            throw new AdapterException("Adapter with URL: " + url + " did not start");
        }
    }


    public static void stopAdapter(String adapterId, AdapterStorageImpl adapterStorage, String url) throws AdapterException {

        //Delete from database
        AdapterDescription ad = adapterStorage.getAdapter(adapterId);

        // Stop execution of adatper
        try {
            logger.info("Trying to stopAdapter adpater on endpoint: " + url);

            // TODO quick fix because otherwise it is not serialized to json-ld
            if (ad.getUri() == null) {
                logger.error("Adapter uri is null this should not happen " + ad);
            }

            String adapterDescription = JsonLdUtils.toJsonLD(ad);

            // TODO change this to a delete request
            String responseString = Request.Post(url)
                    .bodyString(adapterDescription, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            logger.info("Adapter stopped on endpoint: " + url + " with Response: " + responseString);

        } catch (IOException e) {
            logger.error("Adapter was not stopped successfully", e);
            throw new AdapterException("Adapter was not stopped successfully with url: " + url);
        }

    }


}

