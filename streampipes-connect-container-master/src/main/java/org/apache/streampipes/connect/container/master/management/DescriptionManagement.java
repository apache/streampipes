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

import org.apache.streampipes.connect.adapter.AdapterRegistry;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.model.generic.Format;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.apache.streampipes.model.connect.grounding.FormatDescriptionList;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescriptionList;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.storage.couchdb.impl.ConnectionWorkerContainerStorageImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DescriptionManagement {

    public ProtocolDescriptionList getProtocols() {
        ConnectionWorkerContainerStorageImpl connectionWorkerContainerStorage = new ConnectionWorkerContainerStorageImpl();

        List<ConnectWorkerContainer> allWorkerContainter = connectionWorkerContainerStorage.getAllConnectWorkerContainers();

        ProtocolDescriptionList result = new ProtocolDescriptionList();

        for (ConnectWorkerContainer connectWorkerContainer : allWorkerContainter) {
            result.getList().addAll(connectWorkerContainer.getProtocols());
        }

        return result;
    }

    public FormatDescriptionList getFormats() {
        Map<String, Format> allFormats = AdapterRegistry.getAllFormats();

        FormatDescriptionList result = new FormatDescriptionList();

        for (Format f : allFormats.values()) {
           result.getList().add(f.declareModel());
        }

        return result;
    }

    public AdapterDescriptionList getAdapters() {
        ConnectionWorkerContainerStorageImpl connectionWorkerContainerStorage = new ConnectionWorkerContainerStorageImpl();

        List<ConnectWorkerContainer> allWorkerContainter = connectionWorkerContainerStorage.getAllConnectWorkerContainers();

        AdapterDescriptionList result = new AdapterDescriptionList();

        for (ConnectWorkerContainer connectWorkerContainer : allWorkerContainter) {
           result.getList().addAll(connectWorkerContainer.getAdapters());
        }

        return result;
    }

    public Optional<AdapterDescription> getAdapter(String id) {
        return getAdapters().getList().stream()
                .filter(desc -> desc.getAppId().equals(id))
                .findFirst();
    }

    public Optional<ProtocolDescription> getProtocol(String id) {
        return getProtocols().getList().stream()
                .filter(desc -> desc.getAppId().equals(id))
                .findFirst();
    }

    public String getAdapterAssets(AdapterDescription desc, String baseUrl) throws AdapterException {
        return WorkerRestClient.getAdapterAssets(baseUrl, desc);
    }

    public byte[] getAdapterIconAsset(AdapterDescription desc, String baseUrl) throws AdapterException {
        return WorkerRestClient.getAdapterIconAsset(baseUrl, desc);
    }

    public String getAdapterDocumentationAsset(AdapterDescription desc, String baseUrl) throws AdapterException {
        return WorkerRestClient.getAdapterDocumentationAsset(baseUrl, desc);
    }

    public String getProtocolAssets(ProtocolDescription desc, String baseUrl) throws AdapterException {
        return WorkerRestClient.getProtocolAssets(baseUrl, desc);
    }

    public byte[] getProtocolIconAsset(ProtocolDescription desc, String baseUrl) throws AdapterException {
        return WorkerRestClient.getProtocolIconAsset(baseUrl, desc);
    }

    public String getProtocolDocumentationAsset(ProtocolDescription desc, String baseUrl) throws AdapterException {
        return WorkerRestClient.getProtocolDocumentationAsset(baseUrl, desc);
    }
}
