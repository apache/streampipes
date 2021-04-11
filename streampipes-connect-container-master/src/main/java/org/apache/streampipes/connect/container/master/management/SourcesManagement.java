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

import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.util.TransportFormatGenerator;
import org.apache.streampipes.connect.container.master.util.AdapterEncryptionService;
import org.apache.streampipes.container.html.JSONGenerator;
import org.apache.streampipes.container.html.model.DataSourceDescriptionHtml;
import org.apache.streampipes.container.html.model.Description;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.sdk.helpers.SupportedProtocols;
import org.apache.streampipes.storage.couchdb.impl.AdapterStorageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SourcesManagement {

    private Logger logger = LoggerFactory.getLogger(SourcesManagement.class);

    private AdapterStorageImpl adapterStorage;
    private String connectHost = null;

    public SourcesManagement(AdapterStorageImpl adapterStorage) {
        this.adapterStorage = adapterStorage;
    }

    public SourcesManagement() {
        this.adapterStorage = new AdapterStorageImpl();
    }

    public void addAdapter(String streamId, SpDataSet dataSet, String username) throws AdapterException {


        String newUrl = getAdapterUrl(streamId, username);
        AdapterSetDescription adapterDescription = (AdapterSetDescription) getAdapterDescriptionById(streamId);
        adapterDescription.setDataSet(dataSet);

        String newId = adapterDescription.getUri() + "/streams/" + dataSet.getDatasetInvocationId();
        adapterDescription.setUri(newId);
        adapterDescription.setId(newId);

        AdapterSetDescription decryptedAdapterDescription =
                (AdapterSetDescription) new Cloner().adapterDescription(adapterDescription);

        WorkerRestClient.invokeSetAdapter(newUrl, decryptedAdapterDescription);
    }

    public void detachAdapter(String streamId, String runningInstanceId, String username) throws AdapterException {
        AdapterSetDescription adapterDescription = (AdapterSetDescription) getAdapterDescriptionById(streamId);

        String newId = adapterDescription.getUri() + "/streams/" + runningInstanceId;
        adapterDescription.setUri(newId);
        adapterDescription.setId(newId);

        String newUrl = getAdapterUrl(streamId, username);
        WorkerRestClient.stopSetAdapter(newUrl, adapterDescription);
    }

    private String getAdapterUrl(String streamId, String username) {
        String appId = "";
        List<AdapterDescription> adapterDescriptions = this.adapterStorage.getAllAdapters();
        for (AdapterDescription ad : adapterDescriptions) {
            if (ad.getElementId().contains(streamId)) {
                appId = ad.getAppId();
            }
        }
        String workerUrl = new Utils().getWorkerUrlById(appId);

        return Utils.addUserNameToApi(workerUrl, username);

    }

    public String getAllAdaptersInstallDescription(String user) throws AdapterException {
//        String host = getConnectHost();

        List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();
        List<Description> allAdapterDescriptions = new ArrayList<>();

        for (AdapterDescription ad : allAdapters) {
            URI uri;
            String uriString = null;
            try {
                uriString = ad.getUri();
                uri = new URI(uriString);
            } catch (URISyntaxException e) {
                logger.error("URI for the sources endpoint is not correct: " + uriString, e);
                throw new AdapterException("Username " + user + " not allowed");
            }


            List<Description> streams = new ArrayList<>();
            Description d = new Description(ad.getName(), "", uri.toString());
            d.setType("set");
            streams.add(d);
            DataSourceDescriptionHtml dsd = new DataSourceDescriptionHtml("Adapter Stream",
                    "This stream is generated by an StreamPipes Connect adapter. ID of adapter: " + ad.getId(), uri.toString(), streams);
            dsd.setType("source");
            dsd.setAppId(ad.getAppId());
            dsd.setEditable(!(ad.isInternallyManaged()));
            allAdapterDescriptions.add(dsd);
        }

        JSONGenerator json = new JSONGenerator(allAdapterDescriptions);

        return json.buildJson();
    }

    private AdapterDescription getAdapterDescriptionById(String id) {
        AdapterDescription adapterDescription = null;
        List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();
        for (AdapterDescription a : allAdapters) {
            if (a.getUri().endsWith(id)) {
                adapterDescription = a;
            }
        }
        AdapterDescription decryptedAdapterDescription =
                new AdapterEncryptionService(new Cloner()
                        .adapterDescription(adapterDescription)).decrypt();

        return decryptedAdapterDescription;
    }

    public SpDataStream getAdapterDataStream(String id) throws AdapterException {

//        AdapterDescription adapterDescription = new AdapterStorageImpl().getAdapter(id);
        // get all Adapters and check id
        AdapterDescription adapterDescription = getAdapterDescriptionById(id);

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


//            String topic = adapterDescription.getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
//
//            TransportProtocol tp = Protocols.kafka(BackendConfig.INSTANCE.getKafkaHost(), BackendConfig.INSTANCE.getKafkaPort(), topic);
//            EventGrounding eg = new EventGrounding();
//            eg.setTransportProtocol(tp);
//
            ds.setEventGrounding(new EventGrounding(adapterDescription.getEventGrounding()));
        }

        String url = adapterDescription.getUri();

        ds.setName(adapterDescription.getName());
        ds.setDescription(adapterDescription.getDescription());
        ds.setCorrespondingAdapterId(adapterDescription.getAdapterId());
        ds.setInternallyManaged(true);

        ds.setUri(url);

        return ds;
    }

    public void setConnectHost(String connectHost) {
        this.connectHost = connectHost;
    }
}
