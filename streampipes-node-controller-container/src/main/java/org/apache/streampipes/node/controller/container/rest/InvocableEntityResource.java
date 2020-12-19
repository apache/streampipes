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

import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.container.transform.Transformer;
import org.apache.streampipes.model.SpDataStreamRelay;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.node.PipelineElementDockerContainer;
import org.apache.streampipes.node.controller.container.management.orchestrator.docker.DockerContainerOrchestrator;
import org.apache.streampipes.node.controller.container.management.pe.InvocableElementManager;
import org.apache.streampipes.node.controller.container.management.pe.RunningInvocableInstances;
import org.apache.streampipes.node.controller.container.management.relay.EventRelay;
import org.apache.streampipes.node.controller.container.management.relay.RunningRelayInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/api/v2/node/container")
public class InvocableEntityResource extends AbstractNodeContainerResource {
    private static final Logger LOG = LoggerFactory.getLogger(InvocableEntityResource.class.getCanonicalName());

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
    public void register(InvocableRegistration registration) {
        InvocableElementManager.getInstance().register(registration);
    }

    @POST
    @Path("/invoke/{identifier}/{elementId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String invoke(@PathParam("identifier") String identifier,
                           @PathParam("elementId") String elementId, String payload) {

        String endpoint;
        InvocableStreamPipesEntity graph;
        try {
            if (identifier.equals("sepa")) {
                graph = Transformer.fromJsonLd(DataProcessorInvocation.class, payload);
                endpoint = graph.getBelongsTo();
                TransportProtocol source = ((DataProcessorInvocation) graph)
                        .getOutputStream()
                        .getEventGrounding()
                        .getTransportProtocol();

                String strategy = ((DataProcessorInvocation) graph).getEventRelayStrategy();
                List<SpDataStreamRelay> dataStreamRelays = ((DataProcessorInvocation) graph).getOutputStreamRelays();

                Map<String, EventRelay> eventRelayMap = new HashMap<>();
                dataStreamRelays.forEach(r -> {
                    TransportProtocol target = r.getEventGrounding().getTransportProtocol();

                    EventRelay eventRelay = new EventRelay(source, target, strategy);
                    eventRelay.start();
                    eventRelayMap.put(r.getElementId(), eventRelay);
                });
                RunningRelayInstances.INSTANCE.add(graph.getDeploymentRunningInstanceId(), eventRelayMap);

                org.apache.streampipes.model.Response resp = InvocableElementManager.getInstance().invoke(endpoint,
                        payload);

                if (resp.isSuccess()) {
                    RunningInvocableInstances.INSTANCE.add(graph.getDeploymentRunningInstanceId(), graph);
                }

                return resp.toString();

            }
            // Currently no data sinks are registered
            else if (identifier.equals("sec")) {
                graph = Transformer.fromJsonLd(DataSinkInvocation.class, payload);
                endpoint = graph.getBelongsTo();

                org.apache.streampipes.model.Response resp = InvocableElementManager.getInstance()
                        .invoke(endpoint, payload);

                if (resp.isSuccess()) {
                    RunningInvocableInstances.INSTANCE.add(graph.getDeploymentRunningInstanceId(), graph);
                }

                return resp.toString();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @DELETE
    @Path("/detach/{identifier}/{elementId}/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String detach(@PathParam("identifier") String identifier, @PathParam("elementId") String elementId,
                         @PathParam("runningInstanceId") String runningInstanceId) {

        LOG.info("receive stop request elementId={}, runningInstanceId={}", elementId, runningInstanceId);

        // TODO store host and port locally to retrieve by runningInstanceId
        String endpoint = RunningInvocableInstances.INSTANCE.get(runningInstanceId).getBelongsTo();
        String resp = InvocableElementManager.getInstance().detach(endpoint + "/" + runningInstanceId);

        // Stop relay for invocable if existing
        Map<String, EventRelay> relay = RunningRelayInstances.INSTANCE.get(runningInstanceId);
        if (relay != null) {
            relay.values().forEach(EventRelay::stop);
        }

        RunningInvocableInstances.INSTANCE.remove(runningInstanceId);
        RunningRelayInstances.INSTANCE.remove(runningInstanceId);

        return resp;
    }

    @DELETE
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removePipelineElementContainer(PipelineElementDockerContainer container) {
        InvocableElementManager.getInstance().unregister();
        return ok(DockerContainerOrchestrator.getInstance().remove(container));
    }
}
