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

import org.apache.streampipes.model.node.PipelineElementDockerContainer;
import org.apache.streampipes.node.controller.container.management.container.DockerOrchestratorManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/node/container")
public class NodeControllerResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPipelineElementContainer(){
        return Response
                .ok()
                .entity(DockerOrchestratorManager.getInstance().list())
                .build();
    }

    @POST
    @Path("/deploy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deployPipelineElementContainer(PipelineElementDockerContainer container) {
        return Response
                .ok()
                .entity(DockerOrchestratorManager.getInstance().deploy(container))
                .build();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerPipelineElementInConsul(String message) {
        // TODO implement

//        HttpClient client = HttpClients.custom().build();
//        HttpUriRequest request = RequestBuilder.put()
//                .setUri("http://localhost:8500/v1/agent/service/register")
//                .setEntity(new StringEntity(message))
//                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
//                .build();
//        client.execute(request);

        return Response
                .ok()
                .status(Response.Status.OK)
                .build();
    }

    @POST
    @Path("/invoke")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response invokePipelineElement(String appId) {
        // TODO implement
        return Response
                .ok()
                .build();
    }

    @POST
    @Path("/detach")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response detachPipelineElement(String appId) {
        // TODO implement
        return Response
                .ok()
                .build();
    }

    @DELETE
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removePipelineElementContainer(PipelineElementDockerContainer container) {
        return Response
                .ok(DockerOrchestratorManager.getInstance().remove(container))
                .build();
    }
}
