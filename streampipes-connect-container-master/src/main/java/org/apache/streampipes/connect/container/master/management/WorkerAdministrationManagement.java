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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.storage.couchdb.impl.ConnectionWorkerContainerStorageImpl;

import java.util.List;

public class WorkerAdministrationManagement {

    private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterManagement.class);

    private ConnectionWorkerContainerStorageImpl connectionWorkerContainerStorage;

    public WorkerAdministrationManagement() {
        this.connectionWorkerContainerStorage = new ConnectionWorkerContainerStorageImpl();
    }

    public void register(ConnectWorkerContainer connectWorker) {
        // Check if already registered

        List<ConnectWorkerContainer> allConnectWorkerContainers =
                this.connectionWorkerContainerStorage.getAllConnectWorkerContainers();

        boolean alreadyRegistered = false;
        for (ConnectWorkerContainer c : allConnectWorkerContainers) {
            if (c.getEndpointUrl().equals(connectWorker.getEndpointUrl())) {
                boolean adaptersChanged = false;

                for (AdapterDescription a : c.getAdapters()) {
                    if (!connectWorker.getAdapters().stream().anyMatch(ad -> ad.getAdapterId().equals(a.getAdapterId()))) {
                        adaptersChanged = true;
                    }
                }

                for (ProtocolDescription p : c.getProtocols()) {
                    if (!connectWorker.getProtocols().stream().anyMatch(pr -> pr.getAppId().equals(p.getAppId()))) {
                        adaptersChanged = true;
                    }
                }

                if (!adaptersChanged) {
                    alreadyRegistered = true;
                } else {
                    LOG.info("Remove old connect worker: " + connectWorker.getEndpointUrl());
                    this.connectionWorkerContainerStorage.deleteConnectWorkerContainer(c.getId());
                }
            }

        }

        // IF NOT REGISTERED
        // Store Connect Worker in DB
        if (!alreadyRegistered) {
            this.connectionWorkerContainerStorage.storeConnectWorkerContainer(connectWorker);
            LOG.info("Stored new connect worker: " + connectWorker.getEndpointUrl() + " in database");
        } else {
            try {
                AdapterMasterManagement.startAllStreamAdapters(connectWorker);
            } catch (AdapterException e) {
                LOG.error("Could not start adapters on worker: " + connectWorker.getEndpointUrl());
            }
        }
    }

    public String getWorkerUrl(String id) {
        String workerUrl = "";

        List<ConnectWorkerContainer> allConnectWorkerContainer = this.connectionWorkerContainerStorage.getAllConnectWorkerContainers();

        for (ConnectWorkerContainer connectWorkerContainer : allConnectWorkerContainer) {
            if (connectWorkerContainer.getProtocols().stream().anyMatch(p -> p.getAppId().equals(id))) {
                workerUrl = connectWorkerContainer.getEndpointUrl();
            } else if (connectWorkerContainer.getAdapters().stream().anyMatch(a -> a.getAppId().equals(id))) {
                workerUrl = connectWorkerContainer.getEndpointUrl();
            }
        }

        return workerUrl;
    }

    public String getWorkerUrl(String id, String deploymentTargetNodeId) {
        String workerUrl = "";

        List<ConnectWorkerContainer> allConnectWorkerContainer = this.connectionWorkerContainerStorage.getAllConnectWorkerContainers();

        for (ConnectWorkerContainer connectWorkerContainer : allConnectWorkerContainer) {
            if (connectWorkerContainer.getProtocols().stream().anyMatch(p -> p.getAppId().equals(id))
                    && connectWorkerContainer.getDeploymentTargetNodeId().equals(deploymentTargetNodeId)) {
                workerUrl = connectWorkerContainer.getEndpointUrl();
            } else if (connectWorkerContainer.getAdapters().stream().anyMatch(a -> a.getAppId().equals(id))
                    && connectWorkerContainer.getDeploymentTargetNodeId().equals(deploymentTargetNodeId)) {
                workerUrl = connectWorkerContainer.getEndpointUrl();
            }
        }

        return workerUrl;
    }
}
