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
package org.apache.streampipes.node.controller.container.rest;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.container.transform.Transformer;
import org.apache.streampipes.container.util.Util;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.node.PipelineElementDockerContainer;
import org.apache.streampipes.node.controller.container.management.orchestrator.docker.DockerContainerOrchestrator;
import org.apache.streampipes.node.controller.container.management.pe.PipelineElementManager;
import org.apache.streampipes.node.controller.container.management.relay.EventRelayManager;
import org.apache.streampipes.node.controller.container.management.relay.RunningRelayInstances;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/node/container")
public class InvocableManagementResource<I extends InvocableStreamPipesEntity> extends AbstractNodeContainerResource{
    private static final Logger LOG = LoggerFactory.getLogger(InvocableManagementResource.class.getCanonicalName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPipelineElementContainer(){
        return ok(DockerContainerOrchestrator.getInstance().list());
    }

    @POST
    @Path("/deploy")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deployPipelineElementContainer(PipelineElementDockerContainer container) {
        return ok(DockerContainerOrchestrator.getInstance().deploy(container));
    }

    @POST
    @Path("/register")
    public void register(String body) {
        try {
            InvocableRegistration invocableRegistration = JacksonSerializer
                    .getObjectMapper()
                    .readValue(body, InvocableRegistration.class);

            // register pipeline elements at consul and node controller
            PipelineElementManager.getInstance().registerPipelineElements(invocableRegistration);
            LOG.info("Sucessfully registered pipeline element container");

        } catch (IOException e) {
            LOG.error("Could not register pipeline element container - " + e.toString());
        }
    }

    @POST
    @Path("/invoke/{identifier}/{elementId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response invoke(@PathParam("identifier") String identifier, @PathParam("elementId") String elementId, String payload) {

        // TODO implement
        String pipelineElementEndpoint;
        InvocableStreamPipesEntity graph;
        try {
            if (identifier.equals("sepa")) {
                graph = Transformer.fromJsonLd(DataProcessorInvocation.class, payload);

                // TODO: start event relay to remote broker
//                EventRelayManager eventRelayManager = new EventRelayManager(graph);
//                eventRelayManager.start();
//                RunningRelayInstances.INSTANCE.add(eventRelayManager.getRelayedTopic(), eventRelayManager);

                PipelineElementManager.getInstance().invokePipelineElement(graph.getBelongsTo(), payload);
            }
            else if (identifier.equals("sec")) {
                graph = Transformer.fromJsonLd(DataSinkInvocation.class, payload);
                pipelineElementEndpoint = graph.getBelongsTo();
                PipelineElementManager.getInstance().invokePipelineElement(pipelineElementEndpoint, payload);

            }
            //pipelineElementEndpoint = graph.getElementEndpointHostname() + COLON + graph.getElementEndpointPort() + "/" + identifier + "/" + elementId;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok();
    }

    // TODO move endpoint to /elementId/instances/runningInstanceId
    @DELETE
    @Path("{elementId}/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String detach(@PathParam("elementId") String elementId, @PathParam("runningInstanceId") String runningInstanceId) {

        LOG.info("receive stop request elementId={}, runningInstanceId={}", elementId, runningInstanceId);

        return Util.toResponseString(elementId, false, "Could not find the running instance with id: " + runningInstanceId);
    }

    @POST
    @Path("/detach")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response detachPipelineElement(String appId) throws SpRuntimeException {
        // TODO implement

        // TODO: stop event relay to remote broker
        EventRelayManager relay = RunningRelayInstances.INSTANCE.get(appId);
        assert relay != null;
        relay.stop();
        RunningRelayInstances.INSTANCE.remove(appId);

        return ok();
    }

    @DELETE
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removePipelineElementContainer(PipelineElementDockerContainer container) {
        PipelineElementManager.getInstance().unregisterPipelineElements();
        return ok(DockerContainerOrchestrator.getInstance().remove(container));
    }

}
