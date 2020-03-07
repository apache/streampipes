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
package org.apache.streampipes.manager.execution.http;

import org.apache.streampipes.config.consul.ConsulSpConfig;
import org.apache.streampipes.container.util.ConsulUtil;
import org.apache.streampipes.manager.node.AvailableNodesFetcher;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.node.NodeInfo;

import java.util.Optional;

public class InvocableEntityUrlGenerator extends EndpointUrlGenerator<InvocableStreamPipesEntity> {

    protected static final String SINK_IDENTIFIER = "sec";
    protected static final String PROCESSOR_IDENTIFIER = "sepa";

    private static final String DEFAULT_NODE_ID = "default";
    private static final String COLON = ":";
    private static final String PE_PORT_KEY = "SP_PORT";

    public InvocableEntityUrlGenerator(InvocableStreamPipesEntity pipelineElement) {
        super(pipelineElement);
    }

    @Override
    public String generateStartPipelineEndpointUrl() {
        return URLPREFIX
                + getHost()
                + SLASH
                + getIdentifier()
                + SLASH
                + pipelineElement.getAppId();
    }

    @Override
    public String generateStopPipelineEndpointUrl() {
        return generateStartPipelineEndpointUrl()
                + SLASH
                + pipelineElement.getDeploymentRunningInstanceId();
    }

    private String getHost() {
        if (pipelineElement.getDeploymentTargetNodeId() == null ||
                pipelineElement.getDeploymentTargetNodeId().equals(DEFAULT_NODE_ID)) {
            return defaultHost();
        } else {
            Optional<NodeInfo> nodeInfoOpt = getNodeInfo();
            if (nodeInfoOpt.isPresent()) {
                NodeInfo nodeInfo = nodeInfoOpt.get();
                // TODO: get port from Consul
                String route = ConsulSpConfig.SERVICE_ROUTE_PREFIX
                        + pipelineElement.getElementEndpointServiceName()
                        + SLASH
                        + ConsulSpConfig.BASE_PREFIX
                        + SLASH
                        + nodeInfo.getNodeControllerId()
                        + SLASH
                        + PE_PORT_KEY;

                return nodeInfo.getNodeMetadata().getNodeHost()
                        + COLON
                        + ConsulUtil.getPortForService(route);
            } else {
                return defaultHost();
            }
        }
    }

    private String defaultHost() {
        return pipelineElement.getElementEndpointHostname()
                + COLON
                + pipelineElement.getElementEndpointPort();
    }

    private String getIdentifier() {
        return pipelineElement instanceof DataProcessorInvocation ? PROCESSOR_IDENTIFIER : SINK_IDENTIFIER;
    }

    private Optional<NodeInfo> getNodeInfo() {
        return new AvailableNodesFetcher()
                .fetchNodes()
                .stream()
                .filter(node -> node.getNodeControllerId().equals(pipelineElement.getDeploymentTargetNodeId()))
                .findFirst();
    }

}
