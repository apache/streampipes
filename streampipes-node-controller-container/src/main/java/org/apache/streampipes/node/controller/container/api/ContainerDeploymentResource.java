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
package org.apache.streampipes.node.controller.container.api;

import org.apache.streampipes.model.node.container.DockerContainer;
import org.apache.streampipes.node.controller.container.management.node.NodeManager;
import org.apache.streampipes.node.controller.container.management.orchestrator.ContainerDeploymentStatus;
import org.apache.streampipes.node.controller.container.management.orchestrator.docker.DockerConstants;
import org.apache.streampipes.node.controller.container.management.orchestrator.docker.DockerContainerManager;
import org.apache.streampipes.node.controller.container.management.pe.InvocableElementManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/v2/node/container")
public class ContainerDeploymentResource extends AbstractResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response getPipelineElementContainer(){
        return ok(DockerContainerManager.getInstance().list());
    }

    @POST
    @Path("/deploy")
    @Consumes(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response deployPipelineElementContainer(DockerContainer container) {
        ContainerDeploymentStatus status = DockerContainerManager.getInstance().deploy(container);

        if (status.getStatus() == DockerConstants.ContainerStatus.DEPLOYED) {
            NodeManager.getInstance().addToRegisteredContainers(status.getContainer());
        }
        return ok(status);
    }

    @DELETE
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response removePipelineElementContainer(DockerContainer container) {
        ContainerDeploymentStatus status = DockerContainerManager.getInstance().remove(container);

        if (status.getStatus() == DockerConstants.ContainerStatus.REMOVED) {
            InvocableElementManager.getInstance().unregister();
            NodeManager.getInstance().removeFromRegisteredContainers(status.getContainer());
        }
        return ok(status);
    }
}
