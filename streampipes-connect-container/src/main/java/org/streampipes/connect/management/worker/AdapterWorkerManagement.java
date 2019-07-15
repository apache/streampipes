/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.management.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.RunningAdapterInstances;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.model.generic.GenericAdapter;
import org.streampipes.connect.adapter.model.generic.Protocol;
import org.streampipes.connect.config.ConnectContainerConfig;
import org.streampipes.connect.init.AdapterDeclarerSingleton;
import org.streampipes.connect.management.AdapterUtils;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.connect.adapter.*;

import java.util.Collection;

public class AdapterWorkerManagement {

    private static final Logger logger = LoggerFactory.getLogger(AdapterWorkerManagement.class);

    public Collection<Protocol> getAllProtocols() {
        return AdapterDeclarerSingleton.getInstance().getAllProtocols();
    }

    public Collection<Adapter> getAllAdapters() {
        return AdapterDeclarerSingleton.getInstance().getAllAdapters();
    }

    public void invokeStreamAdapter(AdapterStreamDescription adapterStreamDescription) throws AdapterException {


        Adapter adapter = AdapterDeclarerSingleton.getInstance().getAdapter(adapterStreamDescription.getAppId());

        Protocol protocol = null;
        if (adapterStreamDescription instanceof GenericAdapterStreamDescription) {
            protocol = AdapterDeclarerSingleton.getInstance().getProtocol(((GenericAdapterStreamDescription) adapterStreamDescription).getProtocolDescription().getElementId());
            ((GenericAdapter) adapter).setProtocol(protocol);
        }

        RunningAdapterInstances.INSTANCE.addAdapter(adapterStreamDescription.getUri(), adapter);
        adapter.startAdapter();

    }

    public void stopStreamAdapter(AdapterStreamDescription adapterStreamDescription) throws AdapterException {
        stopAdapter(adapterStreamDescription);

    }

    public void invokeSetAdapter (AdapterSetDescription adapterSetDescription) throws AdapterException {

        Adapter adapter = AdapterDeclarerSingleton.getInstance().getAdapter(adapterSetDescription.getAppId());

        Protocol protocol = null;
        if (adapterSetDescription instanceof GenericAdapterSetDescription) {
            protocol = AdapterDeclarerSingleton.getInstance().getProtocol(((GenericAdapterSetDescription) adapterSetDescription).getProtocolDescription().getElementId());
            ((GenericAdapter) adapter).setProtocol(protocol);
        }

        SpDataSet dataSet = adapterSetDescription.getDataSet();

        RunningAdapterInstances.INSTANCE.addAdapter(adapterSetDescription.getUri(), adapter);

        adapter.changeEventGrounding(adapterSetDescription.getDataSet().getEventGrounding().getTransportProtocol());

        // Set adapters run the whole set in one thread, once all data is processed the corresponding preprocessing is stopped
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
                String url = AdapterUtils.getUrl(ConnectContainerConfig.INSTANCE.getBackendApiUrl(), dataSet.getCorrespondingPipeline());
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

        Adapter adapter = RunningAdapterInstances.INSTANCE.removeAdapter(adapterUri);

        if (adapter == null) {
            throw new AdapterException("Adapter with id " + adapterUri + " was not found in this container and cannot be stopped.");
        }

        adapter.stopAdapter();
    }

}
