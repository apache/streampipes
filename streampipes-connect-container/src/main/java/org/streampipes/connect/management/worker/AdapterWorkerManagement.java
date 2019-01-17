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
import org.streampipes.connect.adapter.AdapterRegistry;
import org.streampipes.connect.config.ConnectContainerConfig;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.management.AdapterUtils;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterSetDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;

public class AdapterWorkerManagement {

    private static final Logger logger = LoggerFactory.getLogger(AdapterWorkerManagement.class);

    public void invokeStreamAdapter(AdapterStreamDescription adapterStreamDescription) throws AdapterException {

        Adapter adapter = AdapterRegistry.getAdapter(adapterStreamDescription);

        RunningAdapterInstances.INSTANCE.addAdapter(adapterStreamDescription.getUri(), adapter);
        adapter.startAdapter();

    }

    public void stopStreamAdapter(AdapterStreamDescription adapterStreamDescription) throws AdapterException {
        stopAdapter(adapterStreamDescription);

    }

    public void invokeSetAdapter (AdapterSetDescription adapterSetDescription) throws AdapterException {
        SpDataSet dataSet = adapterSetDescription.getDataSet();

        Adapter adapter = AdapterRegistry.getAdapter(adapterSetDescription);
        RunningAdapterInstances.INSTANCE.addAdapter(adapterSetDescription.getUri(), adapter);


        // Set adapters run the whole set in one thread, once all data is processed the corresponding pipeline is stopped
        Runnable r = () -> {
            try {
                adapter.startAdapter();
            } catch (AdapterException e) {
                e.printStackTrace();
            }

            // TODO wait till all components are done with their calculations
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String url = AdapterUtils.getUrl(ConnectContainerConfig.INSTANCE.getBackendApiUrl(), dataSet.getCorrespondingPipeline());
            String result = AdapterUtils.stopPipeline(url);
            logger.info(result);
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
