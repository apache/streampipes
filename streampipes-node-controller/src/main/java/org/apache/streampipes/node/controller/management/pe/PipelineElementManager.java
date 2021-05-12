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
package org.apache.streampipes.node.controller.management.pe;

import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.pipeline.PipelineElementReconfigurationEntity;
import org.apache.streampipes.node.controller.management.node.NodeManager;
import org.apache.streampipes.node.controller.management.pe.handler.*;
import org.apache.streampipes.node.controller.management.pe.storage.RunningInvocableInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PipelineElementManager implements IPipelineElementLifeCycle {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineElementManager.class.getCanonicalName());

    private static PipelineElementManager instance = null;

    private PipelineElementManager() {}

    public static PipelineElementManager getInstance() {
        if (instance == null) {
            synchronized (PipelineElementManager.class) {
                if (instance == null)
                    instance = new PipelineElementManager();
            }
        }
        return instance;
    }

    @Override
    public void register(InvocableRegistration registration) {
        boolean consulRegistration = new ConsulInteractionHandler(registration, PipelineElementLifeCycleState.REGISTER).handle();
        if (consulRegistration) {
            LOG.info("Successfully registered pipeline element at consul");
        }

        NodeInfoDescription node = getNodeInfoDescription();
        boolean nodeRegistration =
                new PipelineElementNodeInteractionHandler(node, registration, PipelineElementLifeCycleState.REGISTER).handle();
        if (nodeRegistration) {
            LOG.info("Successfully registered and synchronized supported pipeline elements");
        }

        List<InvocableStreamPipesEntity> invokedPipelineElements = getAllInvocables();
        boolean invokedPipelineElementsOnReboot = new PipelineElementRestartOnReboot(invokedPipelineElements).handle();
        if (invokedPipelineElementsOnReboot) {
            LOG.info("Successfully restarted pipeline elements");
        }
    }

    @Override
    public Response invoke(InvocableStreamPipesEntity graph) {
        return new PipelineElementInteractionHandler(graph, PipelineElementLifeCycleState.INVOKE).handle();
    }

    @Override
    public Response detach(InvocableStreamPipesEntity graph, String runningInstanceId) {
        return new PipelineElementInteractionHandler(graph, PipelineElementLifeCycleState.DETACH, runningInstanceId).handle();
    }

    @Override
    public void deregister(){
        // TODO: unregister element from Consul

        NodeInfoDescription node = getNodeInfoDescription();
        boolean nodeRegistration = new PipelineElementNodeInteractionHandler(node, PipelineElementLifeCycleState.DEREGISTER).handle();
        if (nodeRegistration) {
            LOG.info("Successfully deregistered and synchronized pipeline elements");
        }
    }

    @Override
    public Response reconfigure(InvocableStreamPipesEntity graph,
                                PipelineElementReconfigurationEntity reconfigurationEntity) {
        return new PipelineElementReconfigurationHandler(graph, reconfigurationEntity).handle();
    }

    @Override
    public Response offload(InvocableStreamPipesEntity graph) {
        return new PipelineElementOffloadingHandler(graph).handle();
    }


    private List<InvocableStreamPipesEntity> getAllInvocables() {
        return RunningInvocableInstances.INSTANCE.getAll();
    }

    private NodeInfoDescription getNodeInfoDescription() {
        return NodeManager.getInstance().getNodeInfoDescription();
    }

}
