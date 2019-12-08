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

package org.streampipes.container.standalone.init;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.init.ModelSubmitter;
import org.streampipes.container.init.RunningInstances;
import org.streampipes.container.model.PeConfig;
import org.streampipes.container.util.ConsulUtil;

import java.util.Collections;

import javax.annotation.PreDestroy;

@Configuration
@EnableAutoConfiguration
@Import({ PipelineElementContainerResourceConfig.class })
public abstract class StandaloneModelSubmitter extends ModelSubmitter {

    private static final Logger LOG =
            LoggerFactory.getLogger(StandaloneModelSubmitter.class.getCanonicalName());

    public void init(PeConfig peConfig) {

        DeclarersSingleton.getInstance()
                .setHostName(peConfig.getHost());
        DeclarersSingleton.getInstance()
                .setPort(peConfig.getPort());

        SpringApplication app = new SpringApplication(StandaloneModelSubmitter.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", peConfig.getPort()));
        app.run();

        ConsulUtil.registerPeService(
                peConfig.getId(),
                peConfig.getHost(),
                peConfig.getPort()
        );
    }

    @PreDestroy
    public void onExit() {
        LOG.info("Shutting down StreamPipes pipeline element container...");
        Integer runningInstancesCount = RunningInstances.INSTANCE.getRunningInstancesCount();

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
}
