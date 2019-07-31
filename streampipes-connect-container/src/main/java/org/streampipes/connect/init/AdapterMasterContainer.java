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
import org.streampipes.connect.config.ConnectContainerConfig;
import org.streampipes.connect.rest.master.AdapterResource;
import org.streampipes.connect.rest.master.AdapterTemplateResource;
import org.streampipes.connect.rest.master.DescriptionResource;
import org.streampipes.connect.rest.master.FileResource;
import org.streampipes.connect.rest.master.GuessResource;
import org.streampipes.connect.rest.master.RuntimeResolvableResource;
import org.streampipes.connect.rest.master.SourcesResource;
import org.streampipes.connect.rest.master.UnitResource;
import org.streampipes.connect.rest.master.WelcomePageMaster;
import org.streampipes.connect.rest.master.WorkerAdministrationResource;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

public class AdapterMasterContainer extends AdapterContainer {

    private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterContainer.class);

    public static void main(String... args) throws InterruptedException {

        String url = ConnectContainerConfig.INSTANCE.getConnectContainerMasterUrl();

        LOG.info("Started StreamPipes Connect Resource in MASTER mode");
        ResourceConfig config = new ResourceConfig(getMasterApiClasses());
        URI baseUri = UriBuilder
                .fromUri(url)
                .build();

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
        allClasses.add(WorkerAdministrationResource.class);
        allClasses.add(RuntimeResolvableResource.class);
        allClasses.addAll(getApiClasses());

        return allClasses;
    }



}
