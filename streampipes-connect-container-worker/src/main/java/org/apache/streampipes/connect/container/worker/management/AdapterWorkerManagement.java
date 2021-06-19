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

package org.apache.streampipes.connect.container.worker.management;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.connect.RunningAdapterInstances;
import org.apache.streampipes.connect.adapter.model.generic.GenericAdapter;
import org.apache.streampipes.connect.api.IAdapter;
import org.apache.streampipes.connect.api.IProtocol;
import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.container.worker.utils.AdapterUtils;
import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.connect.adapter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class AdapterWorkerManagement {

    private static final Logger logger = LoggerFactory.getLogger(AdapterWorkerManagement.class);

    public Collection<IProtocol> getAllProtocols() {
        return DeclarersSingleton.getInstance().getAllProtocols();
    }

    public IProtocol getProtocol(String id) {
        return DeclarersSingleton.getInstance().getProtocol(id);
    }

    public Collection<IAdapter> getAllAdapters() {
        return DeclarersSingleton.getInstance().getAllAdapters();
    }

    public IAdapter<?> getAdapter(String id) {
        return DeclarersSingleton.getInstance().getAdapter(id);
    }

    public void invokeStreamAdapter(AdapterStreamDescription adapterStreamDescription) throws AdapterException {


//        Adapter adapter = AdapterDeclarerSingleton.getInstance().getAdapter(adapterStreamDescription.getAppId());
       IAdapter<?> adapter = AdapterUtils.setAdapter(adapterStreamDescription);

        IProtocol protocol = null;
        if (adapterStreamDescription instanceof GenericAdapterStreamDescription) {
            //TODO Need to check with ElementId?
            //protocol = AdapterDeclarerSingleton.getInstance().getProtocol(((GenericAdapterStreamDescription) adapterStreamDescription).getProtocolDescription().getElementId());
            protocol = DeclarersSingleton.getInstance().getProtocol(((GenericAdapterStreamDescription) adapterStreamDescription).getProtocolDescription().getAppId());
            if (protocol == null) {
                protocol = DeclarersSingleton.getInstance().getProtocol(((GenericAdapterStreamDescription) adapterStreamDescription).getProtocolDescription().getAppId());
            }
            ((GenericAdapter) adapter).setProtocol(protocol);
        }

        RunningAdapterInstances.INSTANCE.addAdapter(adapterStreamDescription.getUri(), adapter);
        adapter.startAdapter();

    }

    public void stopStreamAdapter(AdapterStreamDescription adapterStreamDescription) throws AdapterException {
        stopAdapter(adapterStreamDescription);

    }

    public void invokeSetAdapter (AdapterSetDescription adapterSetDescription) throws AdapterException {

        IAdapter<?> adapter = AdapterUtils.setAdapter(adapterSetDescription);

        IProtocol protocol = null;
        if (adapterSetDescription instanceof GenericAdapterSetDescription) {
            protocol = DeclarersSingleton.getInstance().getProtocol(((GenericAdapterSetDescription) adapterSetDescription).getProtocolDescription().getAppId());
            ((GenericAdapter) adapter).setProtocol(protocol);
        }

        SpDataSet dataSet = adapterSetDescription.getDataSet();

        RunningAdapterInstances.INSTANCE.addAdapter(adapterSetDescription.getUri(), adapter);

        adapter.changeEventGrounding(adapterSetDescription.getDataSet().getEventGrounding().getTransportProtocol());

        // Set adapters run the whole set in one thread, once all data is processed the corresponding pipeline is stopped
        Runnable r = () -> {
            try {
                adapter.startAdapter();
            } catch (AdapterException e) {
                e.printStackTrace();
            }

            if (adapterSetDescription.isStopPipeline()) {

                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // TODO Service Discovery
                String url = AdapterUtils.getUrl(BackendConfig.INSTANCE.getBackendApiUrl(), dataSet.getCorrespondingPipeline());
                String result = AdapterUtils.stopPipeline(url);
                logger.info(result);

            }
        };

        new Thread(r).start();
    }

    public void stopSetAdapter (AdapterSetDescription adapterSetDescription) throws AdapterException {
        stopAdapter(adapterSetDescription);
    }

    private void stopAdapter(AdapterDescription adapterDescription) throws AdapterException {

        String adapterUri = adapterDescription.getUri();

        IAdapter<?> adapter = RunningAdapterInstances.INSTANCE.removeAdapter(adapterUri);

        if (adapter == null) {
            throw new AdapterException("Adapter with id " + adapterUri + " was not found in this container and cannot be stopped.");
        }

        adapter.stopAdapter();
    }

}
