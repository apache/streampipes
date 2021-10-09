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

package org.apache.streampipes.connect.container.master.health;

import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.container.master.management.AdapterMasterManagement;
import org.apache.streampipes.connect.container.master.management.WorkerRestClient;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;

import java.util.*;

public class AdapterHealthCheck {

    public AdapterHealthCheck() {
    }

    // TODO how can I test this code?
    public void checkAndRestoreAdapters() {
        AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

        IAdapterStorage adapterStorage = CouchDbStorageManager.INSTANCE.getAdapterInstanceStorage();

        List<AdapterDescription> allAdapersToRecover = new ArrayList<>();

        // Get all adapters
        List<AdapterDescription> allRunningInstancesAdaperDescription = adapterStorage.getAllAdapters();

        Map<String, List<AdapterDescription>> groupByWorker = new HashMap<>();
        allRunningInstancesAdaperDescription.forEach(ad -> {
            String selectedEndpointUrl = ad.getSelectedEndpointUrl();
            if (groupByWorker.containsKey(selectedEndpointUrl)) {
                groupByWorker.get(selectedEndpointUrl).add(ad);
            } else {
                List<AdapterDescription> tmp = Arrays.asList(ad);
                groupByWorker.put(selectedEndpointUrl, tmp);
            }
        });

        groupByWorker.keySet().forEach(adapterEndpointUrl -> {
            try {
                List<AdapterDescription> allRunningInstancesOfOneWorker = WorkerRestClient.getAllRunningAdapterInstanceDescriptions("");
                // TODO Remove all running adapters from allRunningInstancesAdaperDescription
            } catch (AdapterException e) {
                e.printStackTrace();
            }
        });

        for (AdapterDescription adapterDescription : allAdapersToRecover) {
            // TODO how do I know there is a worker to start them?

            // Invoke the adapters
            try {
                adapterMasterManagement.startStreamAdapter(adapterDescription.getElementId());
            } catch (AdapterException e) {
                e.printStackTrace();
            }
        }

   }

}
