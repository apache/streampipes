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

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.connect.adapter.util.TransportFormatGenerator;
import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.container.master.util.AdapterEncryptionService;
import org.apache.streampipes.connect.container.master.util.WorkerPaths;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.sdk.helpers.SupportedProtocols;
import org.apache.streampipes.storage.couchdb.impl.AdapterInstanceStorageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class SourcesManagement {

    private Logger logger = LoggerFactory.getLogger(SourcesManagement.class);

    private AdapterInstanceStorageImpl adapterInstanceStorage;
    private WorkerUrlProvider workerUrlProvider;

    public SourcesManagement(AdapterInstanceStorageImpl adapterStorage) {
      this.adapterInstanceStorage = adapterStorage;
      this.workerUrlProvider = new WorkerUrlProvider();
    }

    public SourcesManagement() {
       this(new AdapterInstanceStorageImpl());
    }

    public void addSetAdapter(SpDataSet dataSet) throws AdapterException, NoServiceEndpointsAvailableException {

        AdapterSetDescription ad = (AdapterSetDescription) getAndDecryptAdapter(dataSet.getCorrespondingAdapterId());
        ad.setDataSet(dataSet);
        ad.setElementId(ad.getElementId() + "/streams/" + dataSet.getDatasetInvocationId());

        try {
            String baseUrl = WorkerPaths.findEndpointUrl(ad.getAppId());
            WorkerRestClient.invokeSetAdapter(baseUrl, ad);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param streamId
     * @param runningInstanceId
     * @throws AdapterException
     * @throws NoServiceEndpointsAvailableException
     */
    public void detachAdapter(String streamId,
                              String runningInstanceId) throws AdapterException, NoServiceEndpointsAvailableException {
        AdapterSetDescription adapterDescription = (AdapterSetDescription) getAdapterDescriptionById(streamId);

        String newId = adapterDescription.getElementId() + "/streams/" + runningInstanceId;
        adapterDescription.setElementId(newId);

        String newUrl = getAdapterUrl(streamId);
        WorkerRestClient.stopSetAdapter(newUrl, adapterDescription);
    }

    private String getAdapterUrl(String streamId) throws NoServiceEndpointsAvailableException {
        String appId = "";
        List<AdapterDescription> adapterDescriptions = this.adapterInstanceStorage.getAllAdapters();
        for (AdapterDescription ad : adapterDescriptions) {
            if (ad.getElementId().contains(streamId)) {
                appId = ad.getAppId();
            }
        }

        return workerUrlProvider.getWorkerBaseUrl(appId);

    }

    private AdapterDescription getAdapterDescriptionById(String id) {
        AdapterDescription adapterDescription = null;
        List<AdapterDescription> allAdapters = adapterInstanceStorage.getAllAdapters();
        for (AdapterDescription a : allAdapters) {
            if (a.getElementId().equals(id)) {
                adapterDescription = a;
            }
        }
        AdapterDescription decryptedAdapterDescription =
                new AdapterEncryptionService(new Cloner()
                        .adapterDescription(adapterDescription)).decrypt();

        return decryptedAdapterDescription;
    }

    public SpDataStream createAdapterDataStream(AdapterDescription adapterDescription) {

        SpDataStream ds;
        if (adapterDescription instanceof AdapterSetDescription) {
            ds = ((AdapterSetDescription) adapterDescription).getDataSet();
            EventGrounding eg = new EventGrounding();
            eg.setTransportProtocols(Arrays.asList(SupportedProtocols.kafka(), SupportedProtocols.jms(),
                    SupportedProtocols.mqtt()));
            eg.setTransportFormats(Arrays.asList(TransportFormatGenerator.getTransportFormat()));
            ((SpDataSet) ds).setSupportedGrounding(eg);
        } else {
            ds = ((AdapterStreamDescription) adapterDescription).getDataStream();
            ds.setEventGrounding(new EventGrounding(adapterDescription.getEventGrounding()));
        }

        ds.setName(adapterDescription.getName());
        ds.setDescription(adapterDescription.getDescription());
        ds.setCorrespondingAdapterId(adapterDescription.getElementId());
        ds.setInternallyManaged(true);

        return ds;
    }

    private AdapterDescription getAndDecryptAdapter(String adapterId) {
        AdapterDescription adapter = this.adapterInstanceStorage.getAdapter(adapterId);
        return new AdapterEncryptionService(new Cloner().adapterDescription(adapter)).decrypt();
    }

}
