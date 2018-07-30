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

package org.streampipes.rest.impl.connect;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.model.modelconnect.AdapterSetDescription;
import org.streampipes.model.modelconnect.AdapterStreamDescription;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;
import org.streampipes.vocabulary.StreamPipes;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class SpConnect {

    private static final Logger logger = LoggerFactory.getLogger(SpConnectResource.class);

    public String addAdapter(AdapterDescription ad, String baseUrl, AdapterStorageImpl adapterStorage) {

        // store in db
        adapterStorage.storeAdapter(ad);


        // start when stream adapter
        if (ad instanceof AdapterStreamDescription) {
            SpConnect.startStreamAdapter((AdapterStreamDescription) ad, baseUrl);
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


        return SpConnectUtils.SUCCESS;
    }

    public boolean installDataSource(String requestUrl, String elementIdUrl) {

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
            e.printStackTrace();
        }

        return true;
    }


    public static boolean deleteDataSource(String requestUrl) {
        boolean response = true;

        String responseString = null;
        logger.info("Delete data source in backend with request URL: " + requestUrl);
        try {
            responseString = Request.Delete(requestUrl)
                   .connectTimeout(1000)
                   .socketTimeout(100000)
                   .execute().returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
            responseString = e.toString();
            response = false;
        }

        logger.info("Response of the deletion request" + responseString);
        return response;
    }

    public static boolean isStreamAdapter(String id, AdapterStorageImpl adapterStorage) {
        AdapterDescription ad = adapterStorage.getAdapter(id);

        return ad instanceof AdapterStreamDescription;
    }

    public static AdapterDescription getAdapterDescription(String ads) {


        AdapterDescription a = null;

        if (ads.contains("AdapterSetDescription")){
            JsonLdTransformer jsonLdTransformer = new JsonLdTransformer(StreamPipes.ADAPTER_SET_DESCRIPTION);
            a = getDescription(jsonLdTransformer, ads, AdapterSetDescription.class);
        } else {
            JsonLdTransformer jsonLdTransformer = new JsonLdTransformer(StreamPipes.ADAPTER_STREAM_DESCRIPTION);
            a = getDescription(jsonLdTransformer, ads, AdapterStreamDescription.class);
        }

        logger.info("Add Adapter Description " + a.getUri());

        return a;
    }

    public static <T> T getDescription(JsonLdTransformer jsonLdTransformer, String s, Class<T> theClass) {

        T a = null;

        try {
            a = jsonLdTransformer.fromJsonLd(s, theClass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return a;
    }

    public static String stopSetAdapter(String adapterId, String baseUrl, AdapterStorageImpl adapterStorage) {
        String url = baseUrl + "api/v1/stop/set";

        return stopAdapter(adapterId, adapterStorage, url);
    }

    public static String stopStreamAdapter(String adapterId, String baseUrl, AdapterStorageImpl adapterStorage) {
        String url = baseUrl + "api/v1/stop/stream";

        return stopAdapter(adapterId, adapterStorage, url);
    }

    private static String stopAdapter(String adapterId, AdapterStorageImpl adapterStorage, String url) {

        //Delete from database
        AdapterDescription ad = adapterStorage.getAdapter(adapterId);

        // Stop execution of adatper
         try {
            logger.info("Trying to stop adpater on endpoint: " + url);

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
            e.printStackTrace();

            return "Adapter was not stopped successfully";
        }

        return SpConnectUtils.SUCCESS;
    }


    public static String startStreamAdapter(AdapterStreamDescription asd, String baseUrl) {
        String url = baseUrl + "api/v1/invoke/stream";

        return postStartAdapter(url, asd);
    }

    public  String invokeAdapter(String streamId, SpDataSet dataSet, String baseUrl, AdapterStorageImpl adapterStorage) {
        String url = baseUrl + "api/v1/invoke/set";

        AdapterSetDescription adapterDescription = (AdapterSetDescription) adapterStorage.getAdapter(streamId);
        adapterDescription.setDataSet(dataSet);

        return postStartAdapter(url, adapterDescription);
    }

    private static String postStartAdapter(String url, AdapterDescription ad) {
        try {
            logger.info("Trying to start adpater on endpoint: " + url);

            // this ensures that all adapters have a valid uri otherwise the json-ld serializer fails
            if (ad.getUri() == null) {
                ad.setUri("https://streampipes.org/adapter/" + UUID.randomUUID());
            }
            String adapterDescription = toJsonLd(ad);

            String responseString = Request.Post(url)
                    .bodyString(adapterDescription, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            logger.info("Adapter started on endpoint: " + url + " with Response: " + responseString);

        } catch (IOException e) {
            e.printStackTrace();

            return "Adapter was not started successfully";
        }

        return SpConnectUtils.SUCCESS;
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
