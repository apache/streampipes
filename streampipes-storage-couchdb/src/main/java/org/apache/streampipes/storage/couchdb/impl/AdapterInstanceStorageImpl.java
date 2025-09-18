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

package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Base64;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class AdapterInstanceStorageImpl extends DefaultCrudStorage<AdapterDescription> implements IAdapterStorage {

    private static final Logger LOG = LoggerFactory.getLogger(AdapterInstanceStorageImpl.class.getCanonicalName());

    public AdapterInstanceStorageImpl() {
        super(Utils::getCouchDbAdapterInstanceClient, AdapterDescription.class);
    }

    @Override
    public AdapterDescription getFirstAdapterByAppId(String appId) {
        return this.findAll()
                .stream()
                .filter(p -> p.getAppId().equals(appId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<AdapterDescription> getAdaptersByAppId(String appId) {
        return this.findAll()
                .stream()
                .filter(p -> p.getAppId().equals(appId))
                .toList();
    }

    @Override
    public List<AdapterDescription> findAll() {
        List<AdapterDescription> adapters = findAll("paginator/non_design_docs");
        return adapters.stream()
                .filter(adapter -> adapter.getDescription() != null)
                .toList();
    }

    @Override
    public List<AdapterDescription> getAdapterPaginator(String startItem, String endItem, int limit, String view,
            boolean descending) {
        long startItemLong = 0L; // default value
        String uri = "paginator/by_" + view;

        LOG.info(startItem);

        if (startItem == null || startItem.isEmpty()) {
            return couchDbClientSupplier
                    .get()
                    .view(uri)
                    .includeDocs(true)
                    .limit(limit)
                    .descending(descending)
                    .query(AdapterDescription.class);
        }

        var buildCall = couchDbClientSupplier
                .get()
                .view(uri)
                .includeDocs(true)
                .limit(limit);

        if ("createdAt".equals(view)) {
            try {
                startItemLong = Long.parseLong(startItem);
                buildCall = buildCall.startKey(startItemLong);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid startItem format for 'createdAt'", e);
            }
        } else if (startItem.startsWith("[") && startItem.endsWith("]")) {
            try {
                // Assuming the startItem is a JSON array in string form
                LOG.info("Starting Object Thinf");
                ObjectMapper objectMapper = new ObjectMapper();
                Object[] startKeyArray = objectMapper.readValue(startItem, Object[].class);
                LOG.info("Array Start Key");
                 LOG.info("Array Start Key: " + Arrays.toString(startKeyArray));
                buildCall = buildCall.startKey(startKeyArray);

            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid startItem format for compound key", e);
            }
        } else {
            LOG.info(startItem);
            buildCall = buildCall.startKey(startItem);
        }

        if (endItem != null && !endItem.isEmpty()) {

            LOG.info("added end key");
            LOG.info(endItem);
             buildCall = buildCall.endKey(endItem);
            
        }

        return buildCall
                .descending(descending)
                .query(AdapterDescription.class);
    }

    @Override
public List<AdapterDescription> getItemsByCategoryPaginated(String category, String startDocId, int limit, boolean descending) {

    // Does not use LightCouchDB, as the current functionality of the URI Parser runs into issues by querying endKey with arrays. 

    List<AdapterDescription> resultList = new ArrayList<>();

    try {
        // Extract the necessary data form the couch DB Instance
        CouchDbClient dbClient = couchDbClientSupplier.get();//new CouchDbClient();  // or however you're managing it
        Gson gson =  dbClient.getGson();
        URI baseUri = dbClient.getBaseUri();

        // Log the base URI to check its structure
        LOG.info("Base URI: " + baseUri.toString());

        // Extract the host and port from the URI
        String host = baseUri.getHost();
        int port = baseUri.getPort();
        String userInfo = baseUri.getUserInfo(); // Returns "admin:admin"
        LOG.info(userInfo);
        String[] parts = userInfo != null ? userInfo.split(":") : new String[] { "admin", "admin" };

        String username = parts[0];
        String password = parts.length > 1 ? parts[1] : "";

        String authHeader = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

        String dbName = "adapterinstance";
        String designDoc = "paginator";
        String viewName = "by_category";
      
       

        // Query parameters
       //Check if startDocId exists 
        String startKey;
       if (startDocId != null && !startDocId.isEmpty()) {
        LOG.info("WE HABE A STARTSD" + startDocId);
    startKey = "[\"" + category + "\", \"" + startDocId + "\"]";
} else {
    LOG.info("OnlyCat" + category);
    startKey = "[\"" + category + "\"]";
}
        startKey = URLEncoder.encode(startKey);
        //String startKey = URLEncoder.encode("[\"" + category + "\"]", StandardCharsets.UTF_8);
        String endKey = URLEncoder.encode("[\"" + category + "\", \"\ufff0\"]", StandardCharsets.UTF_8);

        // Construct full URL
        String urlStr = String.format(
                "http://%s:%d/%s/_design/%s/_view/%s?startkey=%s&endkey=%s&limit=%d&include_docs=true",
                host, port, dbName, designDoc, viewName, startKey, endKey, limit
        );
        LOG.info("StartKey" + startKey.toString());
        LOG.info("urlStr" +  urlStr.toString());

        // HTTP request setup
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Basic " + authHeader);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed with HTTP code: " + responseCode);
        }

        // ✅ Parse response using Gson
        try (Reader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            if (root.has("rows")) {
                JsonArray rows = root.getAsJsonArray("rows");

                for (JsonElement rowElem : rows) {
                    JsonObject rowObj = rowElem.getAsJsonObject();
                    JsonElement docElem = rowObj.get("doc");

                    if (docElem != null && !docElem.isJsonNull()) {
                        // Optional: enforce @class for polymorphic deserialization if needed
                        docElem.getAsJsonObject().addProperty("@class", "org.apache.streampipes.model.connect.adapter.AdapterDescription");

                        AdapterDescription adapter = gson.fromJson(docElem, AdapterDescription.class);
                        resultList.add(adapter);
                    }
                }
            }
        }

    } catch (IOException e) {
        System.err.println("I/O error during CouchDB request: " + e.getMessage());
        e.printStackTrace();
    } catch (RuntimeException e) {
        System.err.println("Runtime exception: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        System.err.println("Unexpected exception: " + e.getMessage());
        e.printStackTrace();
    }

    return resultList;
    }

    
}
