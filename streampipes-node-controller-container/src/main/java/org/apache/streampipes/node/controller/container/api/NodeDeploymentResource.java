package org.apache.streampipes.node.controller.container.api;/*
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

import com.spotify.docker.client.exceptions.DockerException;
import org.apache.streampipes.model.node.PipelineElementDockerContainer;
import org.apache.streampipes.node.controller.container.deployment.PipelineElementDockerContainerManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/node/pe")
public class NodeDeploymentResource {

    @POST
    @Path("/container/deploy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deployPipelineElementContainer(PipelineElementDockerContainer container) {
        try {
            return Response
                    .ok()
                    .entity(PipelineElementDockerContainerManager.deploy(container))
                    .build();
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GET
    @Path("/container")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPipelineElementContainer(){
        return Response
                .ok()
                .entity(PipelineElementDockerContainerManager.getPipelineElementContainer())
                .build();
    }

    @POST
    @Path("/container/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopPipelineElementContainer(PipelineElementDockerContainer container) {

        return Response
                .ok(PipelineElementDockerContainerManager.stopAndRemove(container))
                .build();
    }

    @POST
    @Path("/container/processor/invoke")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response invokePipelineElement(String appId) {
        // TODO implement
        return Response
                .ok()
                .build();
    }

    @POST
    @Path("/container/processor/stop")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopPipelineElement(String appId) {
        // TODO implement
        return Response
                .ok()
                .build();
    }

    @POST
    @Path("/container/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerPipelineElementInConsul(String serviceId) {
        // TODO implement
        return Response
                .ok()
                .build();
    }
}
