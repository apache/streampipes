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

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lightcouch.CouchDbClient;
import org.lightcouch.View;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;

public class AdapterInstanceStorageImpl extends DefaultCrudStorage<AdapterDescription> implements IAdapterStorage {

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
                .toList();
    }

    @Override
    public List<AdapterDescription> getAdapterPaginator(String startItem, String endItem, int limit, String view,
            boolean descending) {
        String uri = "paginator/by_" + view;
        var dbClient = couchDbClientSupplier.get();
        var viewBuilder = dbClient.view(uri)
                .includeDocs(true)
                .limit(limit)
                .descending(descending);

        if (startItem != null && !startItem.isEmpty()) {
            viewBuilder = applyStartKey(viewBuilder, view, startItem);
        }

        if (endItem != null && !endItem.isEmpty()) {
            viewBuilder = viewBuilder.endKey(endItem);
        }

        return viewBuilder.query(AdapterDescription.class);
    }

    private View applyStartKey(View viewBuilder, String view, String startItem) {
        try {
            if ("createdAt".equals(view)) {
                long startItemLong = Long.parseLong(startItem);
                return viewBuilder.startKey(startItemLong);
            }

            if (startItem.startsWith("[") && startItem.endsWith("]")) {
                ObjectMapper mapper = new ObjectMapper();
                Object[] startKeyArray = mapper.readValue(startItem, Object[].class);
                return viewBuilder.startKey(startKeyArray);
            }

            return viewBuilder.startKey(startItem);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid startItem format for 'createdAt'", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid startItem format for compound key", e);
        }
    }

    @Override
    public List<AdapterDescription> getItemsByCategoryPaginated(String category, String startDocId,
            int limit, boolean descending) {
        List<AdapterDescription> resultList = new ArrayList<>();

        try {
            String url = buildCategoryPaginatedUrl(category, startDocId, limit);
            HttpURLConnection conn = createAuthenticatedConnection(url);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed with HTTP code: " + conn.getResponseCode());
            }

            try (Reader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                resultList = parseAdapterDescriptions(reader, couchDbClientSupplier.get().getGson());
            }

        } catch (IOException e) {
            System.err.println("I/O error during CouchDB request: " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("Runtime exception: " + e.getMessage());
            e.printStackTrace();
        }

        return resultList;
    }

    private String buildCategoryPaginatedUrl(String category, String startDocId, int limit)
            throws UnsupportedEncodingException {
        String dbName = "adapterinstance";
        String designDoc = "paginator";
        String viewName = "by_category";

        String startKey = startDocId != null && !startDocId.isEmpty()
                ? "[\"" + category + "\", \"" + startDocId + "\"]"
                : "[\"" + category + "\"]";

        String endKey = "[\"" + category + "\", \"\ufff0\"]";

        CouchDbClient dbClient = couchDbClientSupplier.get();
        URI baseUri = dbClient.getBaseUri();

        return String.format(
                "http://%s:%d/%s/_design/%s/_view/%s?startkey=%s&endkey=%s&limit=%d&include_docs=true",
                baseUri.getHost(),
                baseUri.getPort(),
                dbName,
                designDoc,
                viewName,
                URLEncoder.encode(startKey, StandardCharsets.UTF_8),
                URLEncoder.encode(endKey, StandardCharsets.UTF_8),
                limit);
    }

    private HttpURLConnection createAuthenticatedConnection(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        String username = Environments.getEnvironment().getCouchDbUsername().getValueOrDefault();
        String password = Environments.getEnvironment().getCouchDbPassword().getValueOrDefault();
        String authHeader = Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Basic " + authHeader);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        return conn;
    }

    private List<AdapterDescription> parseAdapterDescriptions(Reader reader, Gson gson) {
        List<AdapterDescription> result = new ArrayList<>();
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

        if (root.has("rows")) {
            JsonArray rows = root.getAsJsonArray("rows");

            for (JsonElement rowElem : rows) {
                JsonObject rowObj = rowElem.getAsJsonObject();
                JsonElement docElem = rowObj.get("doc");

                if (docElem != null && !docElem.isJsonNull()) {
                    docElem.getAsJsonObject().addProperty("@class",
                            "org.apache.streampipes.model.connect.adapter.AdapterDescription");

                    AdapterDescription adapter = gson.fromJson(docElem, AdapterDescription.class);
                    result.add(adapter);
                }
            }
        }

        return result;
    }

}
