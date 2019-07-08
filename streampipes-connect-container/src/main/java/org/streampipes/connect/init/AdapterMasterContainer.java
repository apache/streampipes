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

package org.streampipes.connect.init;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.config.ConnectContainerConfig;
import org.streampipes.connect.management.master.AdapterMasterManagement;
import org.streampipes.connect.rest.master.*;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class AdapterMasterContainer extends AdapterContainer {

    private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterContainer.class);

    public static void main(String... args) throws InterruptedException {

        ConnectContainerConfig.INSTANCE.getConnectContainerWorkerUrl();

        LOG.info("Started StreamPipes Connect Resource in MASTER mode");
        ResourceConfig config = new ResourceConfig(getMasterApiClasses());
        URI baseUri = UriBuilder
                .fromUri(Config.getMasterBaseUrl())
                .build();

        boolean couchDbAvailable = true;

        do {

            // Start all installed adapters on restart of master
            try {
                AdapterMasterManagement.startAllStreamAdapters();
                couchDbAvailable = true;
            } catch (AdapterException e) {
                LOG.error("Could not start all installed stream adapters", e);
                couchDbAvailable = true;
            } catch (Exception e) {
                LOG.error("Could not connect to couch db. Try again in 2 seconds");
                couchDbAvailable = false;
                Thread.sleep(2000);
            }
        } while (!couchDbAvailable);


        Server server = JettyHttpContainerFactory.createServer(baseUri, config);

    }

    private static Set<Class<?>> getMasterApiClasses() {
        Set<Class<?>> allClasses = new HashSet<>();

        allClasses.add(WelcomePageMaster.class);
        allClasses.add(AdapterResource.class);
        allClasses.add(AdapterTemplateResource.class);
        allClasses.add(DescriptionResource.class);
        allClasses.add(SourcesResource.class);
        allClasses.add(GuessResource.class);
        allClasses.add(FileResource.class);
        allClasses.add(MultiPartFeature.class);
        allClasses.add(UnitResource.class);
        allClasses.addAll(getApiClasses());

        return allClasses;
    }



}
