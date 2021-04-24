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
package org.apache.streampipes.node.controller.management;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.node.controller.api.NodeControllerResourceConfig;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.container.DockerExtensionsContainer;
import org.apache.streampipes.node.controller.container.DockerKafkaContainer;
import org.apache.streampipes.node.controller.container.DockerMosquittoContainer;
import org.apache.streampipes.node.controller.container.DockerZookeeperContainer;
import org.apache.streampipes.node.controller.management.janitor.JanitorManager;
import org.apache.streampipes.node.controller.management.node.NodeManager;
import org.apache.streampipes.node.controller.management.orchestrator.DockerOrchestratorManager;
import org.apache.streampipes.node.controller.management.orchestrator.docker.DockerContainerDeclarerSingleton;
import org.apache.streampipes.node.controller.management.resource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


import javax.annotation.PreDestroy;
import java.util.Collections;

@Configuration
@EnableAutoConfiguration
@Import({ NodeControllerResourceConfig.class })
public abstract class NodeControllerSubmitter {
    private static final Logger LOG = LoggerFactory.getLogger(NodeControllerSubmitter.class.getCanonicalName());

    public void init() {

        LOG.info("Load node controller configuration");
        NodeConfiguration.loadConfigFromEnvironment();

        SpringApplication app = new SpringApplication(NodeControllerSubmitter.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", NodeConfiguration.getNodeControllerPort()));
        app.run();

        LOG.info("Load node info description");
        NodeManager.getInstance().init();

        LOG.info("Register node controller at StreamPipes cluster management");
        boolean success = NodeManager.getInstance().register();

        if (success) {
            LOG.info("Start resource manager");
            ResourceManager.getInstance().run();

            if (!"true".equals(System.getenv("SP_DEBUG"))) {

                LOG.info("Register container descriptions");
                DockerContainerDeclarerSingleton.getInstance()
                        .register(new DockerExtensionsContainer())
                        .register(new DockerMosquittoContainer())
                        .register(new DockerKafkaContainer())
                        .register(new DockerZookeeperContainer());

                LOG.info("Auto-deploy extensions and selected broker container");
                DockerOrchestratorManager.getInstance().init();

                LOG.info("Start janitor manager");
                JanitorManager.getInstance().run();
            }
        } else throw new SpRuntimeException("Could not register node controller at backend");
    }

    @PreDestroy
    public void onExit(){
    }

}
