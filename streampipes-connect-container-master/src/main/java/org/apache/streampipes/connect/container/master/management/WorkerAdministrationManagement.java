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

import org.apache.streampipes.connect.container.master.health.AdapterHealthCheck;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;
import org.apache.streampipes.storage.couchdb.impl.ConnectionWorkerContainerStorageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WorkerAdministrationManagement {

    private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterManagement.class);

    private ConnectionWorkerContainerStorageImpl connectionWorkerContainerStorage;
    private AdapterMasterManagement adapterMasterManagement;

    private IAdapterStorage adapterDescriptionStorage;

    private AdapterHealthCheck adapterHealthCheck;

    public WorkerAdministrationManagement() {
        this.connectionWorkerContainerStorage = new ConnectionWorkerContainerStorageImpl();
        this.adapterMasterManagement = new AdapterMasterManagement();
        this.adapterHealthCheck = new AdapterHealthCheck();
        this.adapterDescriptionStorage = CouchDbStorageManager.INSTANCE.getAdapterDescriptionStorage();
    }

    public void register(List<AdapterDescription> availableAdapterDescription) {

        List<AdapterDescription> alreadyRegisteredAdapters = this.adapterDescriptionStorage.getAllAdapters();

        availableAdapterDescription.forEach(adapterDescription -> {

            // only install once adapter description per service group
            boolean alreadyInstalled = alreadyRegisteredAdapters
                    .stream()
                    .anyMatch(a -> a.getAppId().equals(adapterDescription.getAppId()) && a.getCorrespondingServiceGroup().equals(adapterDescription.getCorrespondingServiceGroup()));
            if (!alreadyInstalled) {
                this.adapterDescriptionStorage.storeAdapter(adapterDescription);
            }
        });

//        this.adapterHealthCheck.checkAndRestoreAdapters();


        // Check if already registered

//        List<ConnectWorkerContainer> allConnectWorkerContainers =
//                this.connectionWorkerContainerStorage.getAllConnectWorkerContainers();
//
//        boolean alreadyRegistered = false;
//
//        // Delete old description if it was registred before
//        for (ConnectWorkerContainer c : allConnectWorkerContainers) {
//            if (c.getServiceGroup().equals(connectWorker.getServiceGroup())) {
//                boolean adaptersChanged = false;
//
//                for (AdapterDescription a : c.getAdapters()) {
//                    if (connectWorker.getAdapters().stream().noneMatch(ad -> ad.getElementId().equals(a.getElementId()))) {
//                        adaptersChanged = true;
//                    }
//                }
//
//                for (ProtocolDescription p : c.getProtocols()) {
//                    if (connectWorker.getProtocols().stream().noneMatch(pr -> pr.getAppId().equals(p.getAppId()))) {
//                        adaptersChanged = true;
//                    }
//                }
//
//                if (!adaptersChanged) {
//                    alreadyRegistered = true;
//                } else {
//                    LOG.info("Remove old connect worker: " + connectWorker.getServiceGroup());
//                    this.connectionWorkerContainerStorage.deleteConnectWorkerContainer(c.getId());
//                }
//            }
//        }
//
//
//
//        // TODO I am not sure if this is correct
//        // IF NOT REGISTERED
//        // Store Connect Worker in DB
//        if (!alreadyRegistered) {
//            this.connectionWorkerContainerStorage.storeConnectWorkerContainer(connectWorker);
//            LOG.info("Stored new connect worker: " + connectWorker.getServiceGroup() + " in database");
//        } else {
//            try {
//                this.adapterMasterManagement.startAllStreamAdapters(connectWorker);
//            } catch (AdapterException e) {
//                LOG.error("Could not start adapters on worker: " + connectWorker.getServiceGroup());
//            } catch (NoServiceEndpointsAvailableException e) {
//                LOG.error("Could not start adapter due to missing endpoint");
//            }
//        }
    }




}
