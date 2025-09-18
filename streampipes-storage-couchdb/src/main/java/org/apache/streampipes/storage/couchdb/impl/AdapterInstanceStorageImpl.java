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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
                buildCall = buildCall.startKey(startKeyArray[0]);

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
            if (endItem.startsWith("[") && endItem.endsWith("]")) {
                try {
                    // Assuming the startItem is a JSON array in string form
                    LOG.info("Starting Object Thinf");
                    ObjectMapper objectMapper = new ObjectMapper();
                    // TODO CHANGED END TO START
                    Object[] endKeyArray = objectMapper.readValue(startItem, Object[].class);
                

                    LOG.info("Array End Key: " + Arrays.toString(endKeyArray));

                    buildCall = buildCall.endKey(endKeyArray[0]);



                } catch (IOException e) {
                    throw new IllegalArgumentException("Invalid startItem format for compound key", e);
                }
            } else {
                buildCall = buildCall.endKey(endItem);
            }
        }

        return buildCall
                .descending(descending)
                .query(AdapterDescription.class);
    }

    @Override
    public List<AdapterDescription> getItemsByCategoryPaginated(String category, String startDocId, int limit,
            boolean descending) {

        String viewName = "paginator/by_category";

        // Construct start key
        Object[] startKey = (startDocId != null && !startDocId.isEmpty())
                ? new Object[] { category } // startDocID
                : new Object[] { category };

        // Construct end key
        Object[] endKey = new Object[] { category,  "\ufff0" };

        //LOG.info("Category: " + category);
        LOG.info("StartDocId: " + startDocId);
        LOG.info("StartKey: " + Arrays.toString(startKey));
        LOG.info("EndKey: " + Arrays.toString(endKey));
        LOG.info("Descending: " + descending);

        var viewQuery = couchDbClientSupplier.get()
                .view(viewName)
                .includeDocs(true)
                .limit(limit)
                .descending(descending)
                .startKey(startKey)
                .endKey(endKey).query(AdapterDescription.class);

                // Manually filter to enforce exact match
        List<AdapterDescription> filtered = viewQuery.stream()
            .filter(doc -> doc.getCategory() != null && doc.getCategory().contains(category))
            .collect(Collectors.toList());

        return filtered;
    }
}
