/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

package org.streampipes.connect.init;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.model.generic.Protocol;
import org.streampipes.connect.management.worker.MasterRestClient;
import org.streampipes.connect.rest.worker.GuessResource;
import org.streampipes.connect.rest.worker.RuntimeResolvableResource;
import org.streampipes.connect.rest.worker.WelcomePageWorker;
import org.streampipes.connect.rest.worker.WorkerResource;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.worker.ConnectWorkerContainer;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AdapterWorkerContainer extends AdapterContainer {

    private static final Logger LOG = LoggerFactory.getLogger(AdapterWorkerContainer.class);

    public void init(String workerUrl, String masterUrl) {

        ResourceConfig config = new ResourceConfig(getWorkerApiClasses());

        LOG.info("Started StreamPipes Connect Resource in WORKER mode");
        URI baseUri = UriBuilder
                .fromUri(workerUrl)
                .build();

        Server server = JettyHttpContainerFactory.createServer(baseUri, config);


        boolean connected = false;

        while (!connected) {
            LOG.info("Trying to connect to master: " + masterUrl);
            connected = MasterRestClient.register(masterUrl, getContainerDescription(workerUrl));

            if (!connected) {
                LOG.info("Retrying in 5 seconds");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        LOG.info("Successfully connected to master: " + masterUrl + " Worker is now running.");


    }

    private ConnectWorkerContainer getContainerDescription(String endpointUrl) {

        List<AdapterDescription> adapters = new ArrayList<>();
        for (Adapter a : AdapterDeclarerSingleton.getInstance().getAllAdapters()) {
            adapters.add(a.declareModel());
        }

        List<ProtocolDescription> protocols = new ArrayList<>();
        for (Protocol p : AdapterDeclarerSingleton.getInstance().getAllProtocols()) {
            protocols.add(p.declareModel());
        }

        ConnectWorkerContainer result = new ConnectWorkerContainer(endpointUrl, protocols, adapters);
        return result;
    }


    private static Set<Class<?>> getWorkerApiClasses() {
        Set<Class<?>> allClasses = new HashSet<>();

        allClasses.add(WelcomePageWorker.class);
        allClasses.add(GuessResource.class);
        allClasses.add(RuntimeResolvableResource.class);
        allClasses.add(WorkerResource.class);

        allClasses.addAll(getApiClasses());

        return allClasses;
    }
}
