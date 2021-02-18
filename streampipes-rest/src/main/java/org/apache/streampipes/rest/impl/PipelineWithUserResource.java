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

package org.apache.streampipes.rest.impl;

import com.google.gson.JsonSyntaxException;
import org.apache.streampipes.commons.exceptions.*;
import org.apache.streampipes.manager.execution.status.PipelineStatusManager;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.client.exception.InvalidConnectionException;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.rest.api.IPipeline;
import org.apache.streampipes.rest.management.PipelineManagement;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.UUID;

@Path("/v2/users/{username}/pipelines")
public class PipelineWithUserResource extends AbstractRestInterface implements IPipeline {

    private static final Logger logger = LoggerFactory.getLogger(PipelineWithUserResource.class);

    @Override
    public Response getAvailable(String username) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Response getFavorites(String username) {
        // TODO Auto-generated method stub
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/own")
    @JacksonSerialized
    @Override
    public Response getOwn(@PathParam("username") String username) {
        return ok(getUserService()
                .getOwnPipelines(username));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/system")
    @JacksonSerialized
    @Override
    public Response getSystemPipelines() {
        return ok(getPipelineStorage().getSystemPipelines());
    }

    @Override
    public Response addFavorite(String username, String elementUri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response removeFavorite(String username, String elementUri) {
        // TODO Auto-generated method stub
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{pipelineId}/status")
    @GsonWithIds
    @Override
    public Response getPipelineStatus(@PathParam("username") String username, @PathParam("pipelineId") String pipelineId) {
        return ok(PipelineStatusManager.getPipelineStatus(pipelineId, 5));
    }

    @DELETE
    @Path("/{pipelineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    public Response removeOwn(@PathParam("username") String username, @PathParam("pipelineId") String elementUri) {
        getPipelineStorage().deletePipeline(elementUri);
        return statusMessage(Notifications.success("Pipeline deleted"));
    }

    @Override
    public String getAsJsonLd(String elementUri) {
        return null;
    }

    @GET
    @Path("/{pipelineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    @Override
    public Response getElement(@PathParam("username") String username, @PathParam("pipelineId") String pipelineId) {
        return ok(getPipelineStorage().getPipeline(pipelineId));
    }

    @Path("/{pipelineId}/start")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    public Response start(@PathParam("username") String username, @PathParam("pipelineId") String pipelineId) {
        try {
            Pipeline pipeline = getPipelineStorage()
                    .getPipeline(pipelineId);
            PipelineOperationStatus status = Operations.startPipeline(pipeline);
            return ok(status);
        } catch (Exception e) {
            e.printStackTrace();
            return statusMessage(Notifications.error(NotificationType.UNKNOWN_ERROR));
        }
    }

    @Path("/{pipelineId}/stop")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    public Response stop(@PathParam("username") String username, @PathParam("pipelineId") String pipelineId) {
        logger.info("User: " + username + " stopped pipeline: " + pipelineId);
        PipelineManagement pm = new PipelineManagement();
        return pm.stopPipeline(pipelineId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    public Response addPipeline(@PathParam("username") String username, Pipeline pipeline) {
        String pipelineId = UUID.randomUUID().toString();
        pipeline.setPipelineId(pipelineId);
        pipeline.setRunning(false);
        pipeline.setCreatedByUser(username);
        pipeline.setCreatedAt(new Date().getTime());
        pipeline.getSepas().forEach(processor -> processor.setCorrespondingUser(username));
        pipeline.getActions().forEach(action -> action.setCorrespondingUser(username));
        //userService.addOwnPipeline(username, pipeline);
        Operations.storePipeline(pipeline);
        SuccessMessage message = Notifications.success("Pipeline stored");
        message.addNotification(new Notification("id", pipelineId));
        return ok(message);
    }

    @Path("/recommend")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    public Response recommend(@PathParam("username") String email, Pipeline pipeline) {
        try {
            return ok(Operations.findRecommendedElements(email, pipeline));
        } catch (JsonSyntaxException e) {
            return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR,
                    e.getMessage()));
        } catch (NoSuitableSepasAvailableException e) {
            return constructErrorMessage(new Notification(NotificationType.NO_SEPA_FOUND,
                    e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR,
                    e.getMessage()));
        }
    }

    @Path("/update/dataset")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    public Response updateDataSet(SpDataSet spDataSet, @PathParam("username")
            String username) {
        return ok(Operations.updateDataSet(spDataSet));
    }

    @Path("/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    public Response update(Pipeline pipeline, @PathParam("username") String username) {
        try {
            return ok(Operations.validatePipeline(pipeline, true, username));
        } catch (JsonSyntaxException e) {
            return badRequest(new Notification(NotificationType.UNKNOWN_ERROR,
                    e.getMessage()));
        } catch (NoMatchingSchemaException e) {
            return badRequest(new Notification(NotificationType.NO_VALID_CONNECTION,
                    e.getMessage()));
        } catch (NoMatchingFormatException e) {
            return badRequest(new Notification(NotificationType.NO_MATCHING_FORMAT_CONNECTION,
                    e.getMessage()));
        } catch (NoMatchingProtocolException e) {
            return badRequest(new Notification(NotificationType.NO_MATCHING_PROTOCOL_CONNECTION,
                    e.getMessage()));
        } catch (RemoteServerNotAccessibleException | NoMatchingJsonSchemaException e) {
            return serverError(new Notification(NotificationType.REMOTE_SERVER_NOT_ACCESSIBLE
                    , e.getMessage()));
        } catch (InvalidConnectionException e) {
            return badRequest(e.getErrorLog());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError(new Notification(NotificationType.UNKNOWN_ERROR,
                    e.getMessage()));
        }
    }

    @PUT
    @Path("/{pipelineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response overwritePipeline(@PathParam("username") String username,
                                      @PathParam("pipelineId") String pipelineId,
                                      Pipeline pipeline) {
        Pipeline storedPipeline = getPipelineStorage().getPipeline(pipelineId);
        storedPipeline.setActions(pipeline.getActions());
        storedPipeline.setSepas(pipeline.getSepas());
        storedPipeline.setActions(pipeline.getActions());
        storedPipeline.setCreatedAt(System.currentTimeMillis());
        storedPipeline.setPipelineCategories(pipeline.getPipelineCategories());
        storedPipeline.setEventRelayStrategy(pipeline.getEventRelayStrategy());
        Operations.updatePipeline(storedPipeline);
        return statusMessage(Notifications.success("Pipeline modified"));
    }

    @POST
    @Path("/migrate/{pipelineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response migratePipelineProcessors(@PathParam("username") String username,
                                              @PathParam("pipelineId") String pipelineId,
                                              Pipeline pipelineNew) {
        try {
            PipelineOperationStatus status = Operations.migratePipelineProcessors(pipelineNew, true, true, true);
            return ok(status);
        } catch (Exception e) {
            e.printStackTrace();
            return statusMessage(Notifications.error(NotificationType.UNKNOWN_ERROR));
        }
    }
}
