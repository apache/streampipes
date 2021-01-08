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
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.node.controller.container.management.pe.InvocableElementManager;
import org.apache.streampipes.node.controller.container.management.pe.RunningInvocableInstances;
import org.apache.streampipes.node.controller.container.management.relay.DataStreamRelayManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public abstract class InvocableEntityResource<I extends InvocableStreamPipesEntity> extends AbstractResource {
    private static final Logger LOG = LoggerFactory.getLogger(InvocableEntityResource.class.getCanonicalName());

    private static final String SLASH = "/";

    protected Class<I> clazz;

    public InvocableEntityResource(Class<I> clazz) {
        this.clazz = clazz;
    }

    @POST
    @Path("/register")
    public void register(InvocableRegistration registration) {
        InvocableElementManager.getInstance().register(registration);
    }

    @POST
    @Path("{elementId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response invoke(@PathParam("elementId") String elementId, I graph) {
        String endpoint;

        if (graph instanceof DataProcessorInvocation) {
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
        else if (graph instanceof DataSinkInvocation) {
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
    @Path("{elementId}/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response detach(@PathParam("elementId") String elementId,
                         @PathParam("runningInstanceId") String runningInstanceId) {
        LOG.info("receive stop request elementId={}, runningInstanceId={}", elementId, runningInstanceId);

        String endpoint = RunningInvocableInstances.INSTANCE.get(runningInstanceId).getBelongsTo();
        Response resp = InvocableElementManager.getInstance().detach(endpoint + SLASH + runningInstanceId);
        RunningInvocableInstances.INSTANCE.remove(runningInstanceId);
        DataStreamRelayManager.getInstance().stopPipelineElementDataStreamRelay(runningInstanceId);

        return ok(resp);
    }

    private String toJson(I graph) {
        try {
            return JacksonSerializer.getObjectMapper().writeValueAsString(graph);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new SpRuntimeException("Could not serialize object: " + graph);
    }
}
