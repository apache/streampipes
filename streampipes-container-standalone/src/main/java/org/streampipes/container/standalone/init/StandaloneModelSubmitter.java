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

package org.streampipes.container.standalone.init;


import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.streampipes.container.api.*;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.init.ModelSubmitter;
import org.streampipes.container.model.PeConfig;
import org.streampipes.container.util.ConsulUtil;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public abstract class StandaloneModelSubmitter extends ModelSubmitter {


    public void init(PeConfig peConfig) {

        DeclarersSingleton.getInstance()
                .setHostName(peConfig.getHost());
        DeclarersSingleton.getInstance()
                .setPort(peConfig.getPort());

        URI baseUri = UriBuilder
                .fromUri(DeclarersSingleton.getInstance().getBaseUri())
                .build();

        ResourceConfig config = new ResourceConfig(getApiClasses());

        Server server = JettyHttpContainerFactory.createServer(baseUri, config);

        ConsulUtil.registerPeService(
                peConfig.getId(),
                peConfig.getHost(),
                peConfig.getPort()
        );

    }

    private Set<Class<?>> getApiClasses() {
        Set<Class<? extends Object>> allClasses = new HashSet<>();

        allClasses.add(Element.class);
        allClasses.add(InvocableElement.class);
        allClasses.add(SecElement.class);
        allClasses.add(SepaElement.class);
        allClasses.add(SepElement.class);
        allClasses.add(WelcomePage.class);
        allClasses.add(PipelineTemplateElement.class);

        return  allClasses;
    }

}
