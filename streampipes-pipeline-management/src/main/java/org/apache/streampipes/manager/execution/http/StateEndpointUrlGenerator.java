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

public class StateEndpointUrlGenerator{
    protected static final String HTTP_PROTOCOL = "http://";
    protected static final String COLON = ":";
    protected static final String SLASH = "/";

    private static final String ELEMENT_ROUTE = "api/v2/node/element";
    private static final String STATE_ROUTE = "/state";
    private static final String DATA_PROCESSOR_PREFIX = "sepa";

    private final InvocableStreamPipesEntity entity;

    public StateEndpointUrlGenerator(InvocableStreamPipesEntity entity){
        this.entity = entity;
    }

    public String generateStateEndpoint(){
        modifyInvocableElement();
        return HTTP_PROTOCOL + entity.getDeploymentTargetNodeHostname() + COLON + entity.getDeploymentTargetNodePort()
                + SLASH
                + ELEMENT_ROUTE
                + SLASH
                + DATA_PROCESSOR_PREFIX
                + SLASH
                + entity.getAppId()
                + SLASH
                + entity.getDeploymentRunningInstanceId()
                + STATE_ROUTE;
    }

    private void modifyInvocableElement() {
        // Necessary because secondary pipeline element description is not stored in backend
        // It uses information from primary pipeline element. Node controller will locally forward
        // request accordingly, thus fields must be correct.
        String route = ConsulSpConfig.SERVICE_ROUTE_PREFIX
                + entity.getElementEndpointServiceName()
                + SLASH
                + ConsulSpConfig.BASE_PREFIX
                + SLASH
                + ConsulSpConfig.SECONDARY_NODE_KEY
                + SLASH
                + entity.getDeploymentTargetNodeId()
                + SLASH;

        String host = ConsulUtil.getValueForRoute(route + "SP_HOST", String.class);
        int port = ConsulUtil.getValueForRoute(route + "SP_PORT", Integer.class);
        entity.setElementEndpointHostname(host);
        entity.setElementEndpointPort(port);
        entity.setBelongsTo(HTTP_PROTOCOL + host + COLON + port + SLASH + DATA_PROCESSOR_PREFIX + SLASH + entity.getAppId());
        entity.setElementId(entity.getBelongsTo() + SLASH + entity.getDeploymentRunningInstanceId());
    }

}
