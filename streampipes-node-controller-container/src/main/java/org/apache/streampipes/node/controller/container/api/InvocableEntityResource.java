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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.node.controller.container.management.pe.InvocableElementManager;
import org.apache.streampipes.node.controller.container.management.pe.RunningInvocableInstances;
import org.apache.streampipes.node.controller.container.management.relay.DataStreamRelayManager;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/v2/node/element")
public class InvocableEntityResource extends AbstractResource {
    private static final Logger LOG = LoggerFactory.getLogger(InvocableEntityResource.class.getCanonicalName());
    private static final String SLASH = "/";
    private static final String DATA_PROCESSOR_PREFIX = "sepa";
    private static final String DATA_SINK_PREFIX = "sec";

    @POST
    @Path("/register")
    @JacksonSerialized
    @Consumes(MediaType.APPLICATION_JSON)
    public void register(InvocableRegistration registration) {
        InvocableElementManager.getInstance().register(registration);
    }

    @POST
    @Path("{identifier}/{elementId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response invoke(@PathParam("identifier") String identifier,
                                            @PathParam("elementId") String elementId, InvocableStreamPipesEntity graph) {
        String endpoint;

        if (identifier.equals(DATA_PROCESSOR_PREFIX)) {
            endpoint = graph.getBelongsTo();
            DataStreamRelayManager.getInstance().startPipelineElementDataStreamRelay((DataProcessorInvocation) graph);
            Response resp = InvocableElementManager.getInstance().invoke(endpoint, toJson(graph));
            if (resp.isSuccess()) {
                RunningInvocableInstances.INSTANCE.add(graph.getDeploymentRunningInstanceId(), graph);
            }
            return ok(resp);
        }
        // Currently no data sinks are registered at node controller. If we, at some point, want to also run data
        // sinks on edge nodes we need to register there Declarer at the node controller one startup.
        else if (identifier.equals(DATA_SINK_PREFIX)) {
            endpoint = graph.getBelongsTo();
            Response resp = InvocableElementManager.getInstance().invoke(endpoint, toJson(graph));
            if (resp.isSuccess()) {
                RunningInvocableInstances.INSTANCE.add(graph.getDeploymentRunningInstanceId(), graph);
            }
            return ok(resp);
        }

        return ok();
    }

    @DELETE
    @Path("{identifier}/{elementId}/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response detach(@PathParam("identifier") String identifier,
                                            @PathParam("elementId") String elementId,
                                            @PathParam("runningInstanceId") String runningInstanceId) {
        String endpoint = RunningInvocableInstances.INSTANCE.get(runningInstanceId).getBelongsTo();
        Response resp = InvocableElementManager.getInstance().detach(endpoint + SLASH + runningInstanceId);
        RunningInvocableInstances.INSTANCE.remove(runningInstanceId);
        DataStreamRelayManager.getInstance().stopPipelineElementDataStreamRelay(runningInstanceId);

        return ok(resp);
    }

    private String toJson(InvocableStreamPipesEntity graph) {
        try {
            return JacksonSerializer.getObjectMapper().writeValueAsString(graph);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new SpRuntimeException("Could not serialize object: " + graph);
    }
}
