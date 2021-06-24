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

package org.apache.streampipes.service.extensions.base;

import org.apache.streampipes.container.base.StreamPipesServiceBase;
import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.model.SpServiceDefinition;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.net.UnknownHostException;

public abstract class StreamPipesExtensionsServiceBase extends StreamPipesServiceBase {

    private static final Logger LOG = LoggerFactory.getLogger(StreamPipesExtensionsServiceBase.class);

    public void init() {
        SpServiceDefinition serviceDef = provideServiceDefinition();
        init(serviceDef);
    }

    public void init(SpServiceDefinition serviceDef) {
        try {
        String host = getHostname();
        Integer port = getPort(serviceDef.getDefaultPort());
        String serviceId = serviceDef.getServiceGroup() + "-" + AUTO_GENERATED_SERVICE_ID;
        serviceDef.setServiceId(serviceId);
        DeclarersSingleton.getInstance().populate(host, port, serviceDef);

        startExtensionsService(this.getClass(), serviceDef);
        } catch (UnknownHostException e) {
            LOG.error("Could not auto-resolve host address - please manually provide the hostname using the SP_HOST environment variable");
        }
    }

    public SpServiceDefinition provideServiceDefinition() {
        return null;
    }

    public abstract void afterServiceRegistered(SpServiceDefinition serviceDef);

    public void startExtensionsService(Class<?> serviceClass,
                                       SpServiceDefinition serviceDef) throws UnknownHostException {
        this.startStreamPipesService(
                serviceClass,
                DefaultSpServiceGroups.EXT,
                serviceId(),
                serviceDef.getDefaultPort()
        );
        this.afterServiceRegistered(serviceDef);
    }

    @PreDestroy
    public abstract void onExit();

    public String serviceId() {
        return DeclarersSingleton.getInstance().getServiceId();
    }

}
