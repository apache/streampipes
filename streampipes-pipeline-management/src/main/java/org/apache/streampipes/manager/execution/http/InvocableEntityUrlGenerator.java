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

    private static final String COLON = ":";
    private static final String SLASH = "/";
    protected static final String SINK_IDENTIFIER = "sec";
    protected static final String PROCESSOR_IDENTIFIER = "sepa";
    private static final String DEFAULT_NODE_ID = "default";
    private static final String PE_PORT_KEY = "SP_PORT";
    private static final String PE_HOST_KEY = "SP_HOST";

    private static final String NODE_CONTROLLER_ROUTE = "node/container";

    public InvocableEntityUrlGenerator(InvocableStreamPipesEntity pipelineElement) {
        super(pipelineElement);
    }

    @Override
    public String generateInvokeEndpoint() {
        if (pipelineElement.getDeploymentTargetNodeId() == null ||
                pipelineElement.getDeploymentTargetNodeId().equals(DEFAULT_NODE_ID)) {
            // default deployments to primary pipeline element
//            return URLPREFIX
//                    + getHost()
//                    + SLASH
//                    + getIdentifier()
//                    + SLASH
//                    + pipelineElement.getAppId();
            return defaultHost();
        } else {
            // edge deployments to secondary pipeline element
            return URLPREFIX
                    + getHost()
                    + SLASH
                    + NODE_CONTROLLER_ROUTE
                    + "invoke"
                    + SLASH
                    + getIdentifier()
                    + SLASH
                    + pipelineElement.getAppId();
        }
    }

    @Override
    public String generateDetachEndpoint() {
        // TODO: handle stop requests
        return generateInvokeEndpoint()
                + SLASH
                + pipelineElement.getDeploymentRunningInstanceId();
    }

    private String getHost() {
        if (pipelineElement.getDeploymentTargetNodeId() == null ||
                pipelineElement.getDeploymentTargetNodeId().equals(DEFAULT_NODE_ID)) {
            return defaultHost();
        }
        else {
            if (deploymentTargetNodeRunning()) {

                String route = ConsulSpConfig.SERVICE_ROUTE_PREFIX
                        + pipelineElement.getElementEndpointServiceName()
                        + SLASH
                        + ConsulSpConfig.BASE_PREFIX
                        + SLASH
                        + ConsulSpConfig.SECONDARY_NODE_KEY
                        + SLASH
                        + pipelineElement.getDeploymentTargetNodeId()
                        + SLASH;

                String host = ConsulUtil.getStringValue(route + PE_HOST_KEY);
                int port = ConsulUtil.getIntValue(route + PE_PORT_KEY);

                // Necessary because secondary pipeline element description is not stored in backend
                // It uses information from primary pipeline element. Node controller will locally forward
                // request accordingly, thus fields must be correct.
                pipelineElement.setElementEndpointHostname(host);
                pipelineElement.setElementEndpointPort(port);
                pipelineElement.setBelongsTo(URLPREFIX + host + COLON + port + SLASH + getIdentifier() + SLASH + pipelineElement.getAppId());
                pipelineElement.setElementId(pipelineElement.getBelongsTo() + SLASH + pipelineElement.getDeploymentRunningInstanceId());

                return pipelineElement.getDeploymentTargetNodeHostname()
                        + COLON
                        + pipelineElement.getDeploymentTargetNodePort();
            }
            else {
                return defaultHost();
            }
        }
    }

    private String defaultHost() {
//        return pipelineElement.getElementEndpointHostname()
//                + COLON
//                + pipelineElement.getElementEndpointPort();
        return pipelineElement.getBelongsTo();
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

    private boolean deploymentTargetNodeRunning() {
        return new AvailableNodesFetcher()
                .fetchNodes()
                .stream()
                .anyMatch(node -> node.getNodeControllerId().equals(pipelineElement.getDeploymentTargetNodeId()));
    }

}
