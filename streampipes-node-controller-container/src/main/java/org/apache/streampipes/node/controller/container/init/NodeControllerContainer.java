package org.apache.streampipes.node.controller.container.init;
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

import org.apache.streampipes.container.util.ConsulUtil;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;
import org.apache.streampipes.node.controller.container.management.info.NodeInfoStorage;
import org.apache.streampipes.node.controller.container.management.node.NodeJanitorManager;
import org.apache.streampipes.node.controller.container.management.resource.ResourceManager;
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
@Import({ NodeControllerContainerResourceConfig.class })
public class NodeControllerContainer {

    private static final Logger LOG =
            LoggerFactory.getLogger(NodeControllerContainer.class.getCanonicalName());

    public static void main(String [] args) {

        NodeControllerConfig nodeConfig = NodeControllerConfig.INSTANCE;

        SpringApplication app = new SpringApplication(NodeControllerContainer.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", nodeConfig.getNodeControllerPort()));
        app.run();

        LOG.info("Load static node metadata");
        NodeInfoStorage.init();

        LOG.info("Start Node Janitor manager");
        NodeJanitorManager.getInstance().run();

        LOG.info("Start Node Resource manager");
        ResourceManager.getInstance().run();

        // registration with consul here
        ConsulUtil.registerNodeControllerService(
                nodeConfig.getNodeServiceId(),
                nodeConfig.getNodeHostName(),
                nodeConfig.getNodeControllerPort()
        );

    }

    @PreDestroy
    public void onExit(){
    }

}
