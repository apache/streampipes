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
package org.apache.streampipes.node.controller.api;

import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.logging.evaluation.EvaluationLogger;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.PipelineElementReconfigurationEntity;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.node.controller.management.pe.PipelineElementManager;
import org.apache.streampipes.node.controller.management.pe.storage.RunningInvocableInstances;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/api/v2/node/element")
public class InvocableEntityResource extends AbstractResource {
    private static final Logger LOG = LoggerFactory.getLogger(InvocableEntityResource.class.getCanonicalName());
    private static final String SLASH = "/";
    private static final String DATA_PROCESSOR_PREFIX = "sepa";
    private static final String DATA_SINK_PREFIX = "sec";
    private static int nrRuns = 1;

    @POST
    @Path("/register")
    @JacksonSerialized
    @Consumes(MediaType.APPLICATION_JSON)
    public void register(InvocableRegistration registration) {
        PipelineElementManager.getInstance().register(registration);
    }

    @POST
    @Path("{identifier}/{elementId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response invoke(@PathParam("identifier") String identifier,
                                            @PathParam("elementId") String elementId, InvocableStreamPipesEntity graph) {

        if (identifier.equals(DATA_PROCESSOR_PREFIX)) {
            Response elementResponse = PipelineElementManager.getInstance().invoke(graph);
            if (elementResponse.isSuccess()) {
                RunningInvocableInstances.INSTANCE.add(graph.getDeploymentRunningInstanceId(), graph);
            }

            return ok(elementResponse);

        } else if (identifier.equals(DATA_SINK_PREFIX)) {
            Response elementResponse = PipelineElementManager.getInstance().invoke(graph);
            if (elementResponse.isSuccess()) {
                RunningInvocableInstances.INSTANCE.add(graph.getDeploymentRunningInstanceId(), graph);
            }

            return ok(elementResponse);
        }
        return ok();
    }

    @DELETE
    @Path("{identifier}/{elementId}/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response detach(@PathParam("identifier") String identifier,
                                            @PathParam("elementId") String elementId,
                                            @PathParam("runningInstanceId") String runningInstanceId) {
        //TODO: Remove Logger after debugging
        InvocableStreamPipesEntity graph = RunningInvocableInstances.INSTANCE.get(runningInstanceId);
        EvaluationLogger logger = EvaluationLogger.getInstance();
        logger.logMQTT("Offloading", "Element detached");
        Response resp = PipelineElementManager.getInstance().detach(graph, runningInstanceId);
        RunningInvocableInstances.INSTANCE.remove(runningInstanceId);

        return ok(resp);
    }

    @GET
    @Path("{identifier}/{elementId}/{runningInstanceId}/state")
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response getState(@PathParam("identifier") String identifier,
                                            @PathParam("elementId") String elementId,
                                            @PathParam("runningInstanceId") String runningInstanceId) {
        InvocableStreamPipesEntity graph = RunningInvocableInstances.INSTANCE.get(runningInstanceId);
        Response resp = PipelineElementManager.getInstance().getState(graph, runningInstanceId);

        return ok(resp);
    }

    @PUT
    @Path("{identifier}/{elementId}/{runningInstanceId}/state")
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response setState(@PathParam("identifier") String identifier,
                                              @PathParam("elementId") String elementId,
                                              @PathParam("runningInstanceId") String runningInstanceId,
                                              String state) {
        InvocableStreamPipesEntity graph = RunningInvocableInstances.INSTANCE.get(runningInstanceId);
        Response resp = PipelineElementManager.getInstance().setState(graph, runningInstanceId, state);

        return ok(resp);
    }

    // Live-Reconfiguration
    @POST
    @JacksonSerialized
    @Path("/reconfigure/{runningInstanceId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response adapt( @PathParam("runningInstanceId") String runningInstanceId,
                                            PipelineElementReconfigurationEntity reconfigurationEntity) {
        //TODO: Remove Logger after debugging
        EvaluationLogger logger = EvaluationLogger.getInstance();
        String value = "";
        if (reconfigurationEntity.getReconfiguredStaticProperties().get(0) instanceof FreeTextStaticProperty) {
            value = ((FreeTextStaticProperty) reconfigurationEntity
                    .getReconfiguredStaticProperties()
                    .get(0))
                    .getValue();
        } else if (reconfigurationEntity.getReconfiguredStaticProperties().get(0) instanceof CodeInputStaticProperty) {
            value = ((CodeInputStaticProperty) reconfigurationEntity
                    .getReconfiguredStaticProperties()
                    .get(0))
                    .getValue();
        }
        logger.logMQTT("Reconfiguration", "reconfiguration request received", nrRuns++, value);
        InvocableStreamPipesEntity graph = RunningInvocableInstances.INSTANCE.get(runningInstanceId);
        return ok(PipelineElementManager.getInstance().reconfigure(graph, reconfigurationEntity));
    }
}
