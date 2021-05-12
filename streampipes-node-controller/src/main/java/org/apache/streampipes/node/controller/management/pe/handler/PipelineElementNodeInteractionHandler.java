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
package org.apache.streampipes.node.controller.management.pe.handler;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.IHandler;
import org.apache.streampipes.node.controller.management.node.NodeManager;
import org.apache.streampipes.node.controller.management.pe.PipelineElementLifeCycleState;
import org.apache.streampipes.node.controller.management.pe.validator.RequirementsResourceValidator;
import org.apache.streampipes.node.controller.utils.HttpUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineElementNodeInteractionHandler implements IHandler<Boolean> {

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String BASE_ROUTE = "streampipes-backend/api/v2/users/admin@streampipes.org/nodes";

    private final NodeInfoDescription node;
    private final InvocableRegistration registration;
    private final PipelineElementLifeCycleState type;

    public PipelineElementNodeInteractionHandler(NodeInfoDescription node, PipelineElementLifeCycleState type) {
        this(node, null, type);
    }

    public PipelineElementNodeInteractionHandler(NodeInfoDescription node, InvocableRegistration registration,
                                                 PipelineElementLifeCycleState type) {
        this.node = node;
        this.registration = registration;
        this.type = type;
    }

    @Override
    public Boolean handle() {
        switch(type) {
            case REGISTER:
                return register();
            case DEREGISTER:
                return deregister();
            default:
                throw new SpRuntimeException("Life cycle step not supported" + type);
        }
    }

    private boolean register() {
        List<String> supportedPipelineElementAppIds = validate();

        NodeManager.getInstance()
                .getNodeInfoDescription()
                .setSupportedElements(supportedPipelineElementAppIds);

        String url = generateNodeManagementUpdateEndpoint();
        return HttpUtils.put(url, node);
    }

    private boolean deregister() {
        NodeManager.getInstance()
                .getNodeInfoDescription()
                .setSupportedElements(Collections.emptyList());

        String url = generateNodeManagementUpdateEndpoint();
        return HttpUtils.put(url, node);
    }


    private List<String> validate(){
        //Check if the node supports the entity (atm only hardware requirements; could be expanded to include
        // software requirements)
        List<ConsumableStreamPipesEntity> allPipelineElements = registration.getSupportedPipelineElements();
        List<ConsumableStreamPipesEntity> validPipelineElements =
                new RequirementsResourceValidator(node, allPipelineElements).validate();

        return validPipelineElements.stream()
                .map(NamedStreamPipesEntity::getAppId)
                .collect(Collectors.toList());
    }

    private String generateNodeManagementUpdateEndpoint() {
        return HTTP_PROTOCOL
                + NodeConfiguration.getBackendHost()
                + COLON
                + NodeConfiguration.getBackendPort()
                + SLASH
                + BASE_ROUTE
                + SLASH
                + NodeConfiguration.getNodeControllerId();
    }
}
