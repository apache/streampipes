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
package org.apache.streampipes.container.extensions;

import io.undertow.server.handlers.proxy.mod_cluster.NodeConfig;
import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.connect.container.worker.management.MasterRestClient;
import org.apache.streampipes.connect.container.worker.management.NodeControllerRestClient;
import org.apache.streampipes.connect.init.AdapterDeclarerSingleton;
import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.init.ModelSubmitter;
import org.apache.streampipes.container.init.RunningInstances;
import org.apache.streampipes.container.locales.LabelGenerator;
import org.apache.streampipes.container.model.EdgeExtensionsConfig;
import org.apache.streampipes.container.model.ExtensionsConfig;
import org.apache.streampipes.container.util.ConsulUtil;
import org.apache.streampipes.container.util.NodeControllerUtil;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableAutoConfiguration
@Import({ ExtensionsResourceConfig.class })
public abstract class ExtensionsModelSubmitter extends ModelSubmitter<EdgeExtensionsConfig> {
    private static final Logger LOG =
            LoggerFactory.getLogger(ExtensionsModelSubmitter.class.getCanonicalName());

    private static final String PROTOCOL = "http://";
    private static final String SLASH = "/";
    private static final String COLON = ":";

    private static final String NODE_CONTROLLER_ID = "SP_NODE_CONTROLLER_ID";
    private static final String NODE_CONTROLLER_CONTAINER_HOST = "SP_NODE_CONTROLLER_CONTAINER_HOST";
    private static final String NODE_CONTROLLER_CONTAINER_PORT = "SP_NODE_CONTROLLER_CONTAINER_PORT";

    public void init(EdgeExtensionsConfig conf) {
        DeclarersSingleton.getInstance().setHostName(conf.getHost());
        DeclarersSingleton.getInstance().setPort(conf.getPort());

        DeclarersSingleton.getInstance().registerDataFormats(
                new JsonDataFormatFactory(),
                new CborDataFormatFactory(),
                new SmileDataFormatFactory(),
                new FstDataFormatFactory());

        DeclarersSingleton.getInstance().registerProtocols(
                new SpKafkaProtocolFactory(),
                new SpMqttProtocolFactory(),
                new SpJmsProtocolFactory());

        LOG.info("Starting StreamPipes Extensions Bundle");
        SpringApplication app = new SpringApplication(ExtensionsModelSubmitter.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", conf.getPort()));
        app.run();

        // check wether pipeline element is managed by node controller
        if (System.getenv(NODE_CONTROLLER_ID) != null) {
            // secondary
            // register pipeline element service via node controller
            NodeControllerUtil.register(
                    conf.getId(),
                    conf.getHost(),
                    conf.getPort(),
                    DeclarersSingleton.getInstance().getEpaDeclarers());

            String nodeControllerUrl = PROTOCOL + conf.getNodeControllerHost() + COLON + conf.getNodeControllerPort();

            boolean connected = false;
            while (!connected) {
                LOG.info("Trying to connect to the node controller: " + nodeControllerUrl);
                connected = NodeControllerRestClient.register(
                        nodeControllerUrl,
                        getContainerDescription(conf.getHost(), conf.getPort(), true));

                if (!connected) {
                    LOG.info("Retrying in 5 seconds");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            LOG.info("Successfully registered connect worker at the backend via node controller: " + nodeControllerUrl);

        } else {
            // primary
            ConsulUtil.registerPeService(
                    conf.getId(),
                    conf.getHost(),
                    conf.getPort());

            String backendUrl = PROTOCOL + conf.getBackendHost() + COLON + conf.getBackendPort() + "/streampipes-backend";

            boolean connected = false;
            while (!connected) {
                LOG.info("Trying to connect to master in backend: " + backendUrl);
                connected = MasterRestClient.register(backendUrl, getContainerDescription(conf.getHost(),
                        conf.getPort(),  false));

                if (!connected) {
                    LOG.info("Retrying in 5 seconds");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            LOG.info("Successfully registered adapter at master in backend: " + backendUrl);
        }
    }

    private ConnectWorkerContainer getContainerDescription(String connectWorkerHost, int connectWorkerPort,
                                                           boolean runsOnEdgeNode) {

        String endpointUrl = PROTOCOL + connectWorkerHost + COLON + connectWorkerPort + SLASH;

        if (runsOnEdgeNode) {
            String deploymentTargetNodeId = System.getenv(NODE_CONTROLLER_ID);
            String deploymentTargetNodeHostname = System.getenv(NODE_CONTROLLER_CONTAINER_HOST);
            int deploymentTargetNodePort = Integer.parseInt(System.getenv(NODE_CONTROLLER_CONTAINER_PORT));

            List<AdapterDescription> adapters = new ArrayList<>();
            for (Adapter a : AdapterDeclarerSingleton.getInstance().getAllAdapters()) {
                AdapterDescription desc = (AdapterDescription) rewrite(a.declareModel(), endpointUrl,
                        deploymentTargetNodeId, deploymentTargetNodeHostname, deploymentTargetNodePort,
                        connectWorkerHost, connectWorkerPort);
                adapters.add(desc);
            }

            List<ProtocolDescription> protocols = new ArrayList<>();
            for (Protocol p : AdapterDeclarerSingleton.getInstance().getAllProtocols()) {
                ProtocolDescription desc = (ProtocolDescription) rewrite(p.declareModel(), endpointUrl,
                        deploymentTargetNodeId, deploymentTargetNodeHostname, deploymentTargetNodePort,
                        connectWorkerHost, connectWorkerPort);
                protocols.add(desc);
            }

            return new ConnectWorkerContainer(endpointUrl, protocols, adapters, deploymentTargetNodeId,
                    deploymentTargetNodeHostname, deploymentTargetNodePort);
        } else {

            List<AdapterDescription> adapters = new ArrayList<>();
            for (Adapter a : AdapterDeclarerSingleton.getInstance().getAllAdapters()) {
                AdapterDescription desc = (AdapterDescription) rewrite(a.declareModel(), endpointUrl);
                adapters.add(desc);
            }

            List<ProtocolDescription> protocols = new ArrayList<>();
            for (Protocol p : AdapterDeclarerSingleton.getInstance().getAllProtocols()) {
                ProtocolDescription desc = (ProtocolDescription) rewrite(p.declareModel(), endpointUrl);
                protocols.add(desc);
            }

            return new ConnectWorkerContainer(endpointUrl, protocols, adapters);
        }
    }

    private NamedStreamPipesEntity rewrite(NamedStreamPipesEntity entity, String endpointUrl) {
        if (!(entity instanceof GenericAdapterDescription)) {
            if (entity instanceof  ProtocolDescription) {
                entity.setElementId(endpointUrl +  "protocol/" + entity.getElementId());
            } else if (entity instanceof  AdapterDescription) {
                entity.setElementId(endpointUrl + "adapter/" + entity.getElementId());
            }
        }

        // TODO remove after full internationalization support has been implemented
        if (entity.isIncludesLocales()) {
            LabelGenerator lg = new LabelGenerator(entity);
            try {
                entity = lg.generateLabels();
            } catch (IOException e) {
                LOG.error("Could not load labels for: " + entity.getAppId());
            }
        }
        return entity;
    }

    private NamedStreamPipesEntity rewrite(NamedStreamPipesEntity entity, String endpointUrl,
                                           String deploymentTargetNodeId, String deploymentTargetNodeHostname,
                                           int deploymentTargetNodePort, String connectWorkerHost,
                                           int connectWorkerPort) {
        if (!(entity instanceof GenericAdapterDescription)) {
            if (entity instanceof  ProtocolDescription) {
                entity.setElementId(endpointUrl +  "protocol/" + entity.getElementId());
                ((ProtocolDescription) entity).setDeploymentTargetNodeId(deploymentTargetNodeId);
                ((ProtocolDescription) entity).setDeploymentTargetNodeHostname(deploymentTargetNodeHostname);
                ((ProtocolDescription) entity).setDeploymentTargetNodePort(deploymentTargetNodePort);
                ((ProtocolDescription) entity).setElementEndpointHostname(connectWorkerHost);
                ((ProtocolDescription) entity).setElementEndpointPort(connectWorkerPort);
            } else if (entity instanceof  AdapterDescription) {
                entity.setElementId(endpointUrl + "adapter/" + entity.getElementId());
                ((AdapterDescription) entity).setDeploymentTargetNodeId(deploymentTargetNodeId);
                ((AdapterDescription) entity).setDeploymentTargetNodeHostname(deploymentTargetNodeHostname);
                ((AdapterDescription) entity).setDeploymentTargetNodePort(deploymentTargetNodePort);
                ((AdapterDescription) entity).setElementEndpointHostname(connectWorkerHost);
                ((AdapterDescription) entity).setElementEndpointPort(connectWorkerPort);
            }
        }

        // TODO remove after full internationalization support has been implemented
        if (entity.isIncludesLocales()) {
            LabelGenerator lg = new LabelGenerator(entity);
            try {
                entity = lg.generateLabels();
            } catch (IOException e) {
                LOG.error("Could not load labels for: " + entity.getAppId());
            }
        }
        return entity;
    }

    @PreDestroy
    public void onExit() {
        LOG.info("Shutting down StreamPipes extensions container...");
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

}
