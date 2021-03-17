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
package org.apache.streampipes.node.controller.container;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.node.controller.container.management.orchestrator.docker.DockerContainerManager;
import org.apache.streampipes.node.controller.container.api.NodeControllerResourceConfig;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;
import org.apache.streampipes.node.controller.container.management.node.NodeManager;
import org.apache.streampipes.node.controller.container.management.janitor.JanitorManager;
import org.apache.streampipes.node.controller.container.management.pe.InvocableElementManager;
import org.apache.streampipes.node.controller.container.management.resource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;

@Configuration
@EnableAutoConfiguration
@Import({ NodeControllerResourceConfig.class })
public class NodeControllerInit {

    private static final Logger LOG =
            LoggerFactory.getLogger(NodeControllerInit.class.getCanonicalName());

    public static void main(String [] args) {

        NodeControllerConfig conf = NodeControllerConfig.INSTANCE;

        SpringApplication app = new SpringApplication(NodeControllerInit.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", conf.getNodeControllerPort()));
        app.run();

        LOG.info("Configured environment variables");
        System.getenv().entrySet().stream()
                .filter(e -> e.getKey().startsWith("SP_"))
                .forEach(System.out::println);

        LOG.info("Load node info description");
        NodeManager.getInstance().init();

        LOG.info("Register node controller at node management");
        boolean success = NodeManager.getInstance().register();

        if (success) {
            LOG.info("Start resource manager");
            ResourceManager.getInstance().run();

            if (!"true".equals(System.getenv("SP_DEBUG"))) {
                LOG.info("Auto-deploy extensions container");
                DockerContainerManager.getInstance().init();

//                LOG.info("Checking for pipeline elements to be started ...");
//                InvocableElementManager.getInstance().invokePipelineElementsOnSystemRebootOrRestart();

                LOG.info("Start janitor manager");
                JanitorManager.getInstance().run();
            }
        } else throw new SpRuntimeException("Could not register node controller at backend");
    }

    @PreDestroy
    public void onExit(){
    }
}
