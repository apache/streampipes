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
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;

public class InvocableEntityUrlGenerator extends EndpointUrlGenerator<InvocableStreamPipesEntity> {

    private static final String DATA_PROCESSOR_PREFIX = "sepa";
    private static final String DATA_SINK_PREFIX = "sec";
    private static final String DEFAULT_TARGET_NODE_ID = "default";
    private static final String ELEMENT_ROUTE = "api/v2/node/element";

    public InvocableEntityUrlGenerator(InvocableStreamPipesEntity graph) {
        super(graph);
    }

    @Override
    public String generateInvokeEndpoint() {
        if (isDefaultTarget()) {
            // default deployments to primary pipeline element
            return getDefaultEndpoint();
        } else {
            // edge deployments to secondary pipeline element
            return getDeploymentTargetEndpoint(ELEMENT_ROUTE);
        }
    }

    @Override
    public String generateDetachEndpoint() {
        if (isDefaultTarget()) {
            // detach primary pipeline element
            return getDefaultEndpoint() + SLASH + graph.getDeploymentRunningInstanceId();
        } else {
            // detach edge deployments to secondary pipeline element
            return getDeploymentTargetEndpoint(ELEMENT_ROUTE) + SLASH + graph.getDeploymentRunningInstanceId();
        }
    }

    // Helper methods

    private boolean isDefaultTarget() {
        return graph.getDeploymentTargetNodeId() == null ||
                graph.getDeploymentTargetNodeId().equals(DEFAULT_TARGET_NODE_ID);
    }

    private String getDefaultEndpoint() {
        return graph.getBelongsTo();
    }

    private String getDeploymentTargetEndpoint(String route) {
        modifyInvocableElement();
        return HTTP_PROTOCOL + graph.getDeploymentTargetNodeHostname() + COLON + graph.getDeploymentTargetNodePort()
                + SLASH
                + route
                + SLASH
                + getIdentifier()
                + SLASH
                + graph.getAppId();
    }

    private void modifyInvocableElement() {
        // Necessary because secondary pipeline element description is not stored in backend
        // It uses information from primary pipeline element. Node controller will locally forward
        // request accordingly, thus fields must be correct.
        String route = ConsulSpConfig.SERVICE_ROUTE_PREFIX
                + graph.getElementEndpointServiceName()
                + SLASH
                + ConsulSpConfig.BASE_PREFIX
                + SLASH
                + ConsulSpConfig.SECONDARY_NODE_KEY
                + SLASH
                + graph.getDeploymentTargetNodeId()
                + SLASH;

        String host = ConsulUtil.getValueForRoute(route + "SP_HOST", String.class);
        int port = ConsulUtil.getValueForRoute(route + "SP_PORT", Integer.class);
        graph.setElementEndpointHostname(host);
        graph.setElementEndpointPort(port);
        graph.setBelongsTo(HTTP_PROTOCOL + host + COLON + port + SLASH + getIdentifier() + SLASH + graph.getAppId());
        graph.setElementId(graph.getBelongsTo() + SLASH + graph.getDeploymentRunningInstanceId());
    }

    private String getIdentifier() {
        return graph instanceof DataProcessorInvocation ? DATA_PROCESSOR_PREFIX : DATA_SINK_PREFIX;
    }

}
