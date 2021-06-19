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

package org.apache.streampipes.container.standalone.init;


import org.apache.streampipes.container.declarer.Declarer;
import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.init.RunningInstances;
import org.apache.streampipes.container.model.PeConfig;
import org.apache.streampipes.container.util.ServiceDefinitionUtil;
import org.apache.streampipes.service.extensions.base.StreamPipesExtensionsServiceBase;
import org.apache.streampipes.svcdiscovery.SpServiceTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableAutoConfiguration
@Import({ PipelineElementContainerResourceConfig.class })
public abstract class StandaloneModelSubmitter extends StreamPipesExtensionsServiceBase<PeConfig> {

    private static final Logger LOG =
            LoggerFactory.getLogger(StandaloneModelSubmitter.class.getCanonicalName());

    @Deprecated
    public void init(PeConfig peConfig) {
        DeclarersSingleton.getInstance()
                .setHostName(peConfig.getHost());
        DeclarersSingleton.getInstance()
                .setPort(peConfig.getPort());

        try {
            startExtensionsService(this.getClass(),
                    peConfig.getId(),
                    DeclarersSingleton.getInstance().getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExit() {
        LOG.info("Shutting down StreamPipes pipeline element container...");
        int runningInstancesCount = RunningInstances.INSTANCE.getRunningInstancesCount();

        while (runningInstancesCount > 0) {
            LOG.info("Waiting for {} running pipeline elements to be stopped...", runningInstancesCount);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error("Could not pause current thread...");
            }
            runningInstancesCount = RunningInstances.INSTANCE.getRunningInstancesCount();
        }
    }

    @Override
    protected List<String> getServiceTags() {
        Collection<Declarer<?>> declarers = DeclarersSingleton.getInstance().getDeclarers().values();
        List<String> serviceTags = ServiceDefinitionUtil.extractAppIds(declarers);
        serviceTags.add(SpServiceTags.PE);

        return serviceTags;
    }

    @Override
    public void afterServiceRegistered() {

    }
}
