/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.impl;

import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.NoMatchingFormatException;
import org.streampipes.commons.exceptions.NoMatchingJsonSchemaException;
import org.streampipes.commons.exceptions.NoMatchingProtocolException;
import org.streampipes.commons.exceptions.NoMatchingSchemaException;
import org.streampipes.commons.exceptions.NoSuitableSepasAvailableException;
import org.streampipes.commons.exceptions.RemoteServerNotAccessibleException;
import org.streampipes.manager.execution.status.PipelineStatusManager;
import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.exception.InvalidConnectionException;
import org.streampipes.model.client.messages.Notification;
import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.client.messages.SuccessMessage;
import org.streampipes.model.client.pipeline.PipelineOperationStatus;
import org.streampipes.rest.api.IPipeline;
import org.streampipes.rest.management.PipelineManagement;
import org.streampipes.rest.shared.annotation.GsonWithIds;

import java.util.Date;
import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    @GsonWithIds
    @Override
    public Response getOwn(@PathParam("username") String username) {
        return ok(getUserService()
                .getOwnPipelines(username));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/system")
    @GsonWithIds
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
    @GsonWithIds
    @Override
    public Response getElement(@PathParam("username") String username, @PathParam("pipelineId") String pipelineId) {
        return ok(getPipelineStorage().getPipeline(pipelineId));
    }

    @Path("/{pipelineId}/start")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    public Response start(@PathParam("username") String username, @PathParam("pipelineId") String pipelineId) {
        try {
            org.streampipes.model.client.pipeline.Pipeline pipeline = getPipelineStorage()
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
    @GsonWithIds
    public Response stop(@PathParam("username") String username, @PathParam("pipelineId") String pipelineId) {
        logger.info("User: " + username + " stopped preprocessing: " + pipelineId);
        PipelineManagement pm = new PipelineManagement();
        return pm.stopPipeline(pipelineId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    public Response addPipeline(@PathParam("username") String username, org.streampipes.model.client.pipeline.Pipeline pipeline) {
        String pipelineId = UUID.randomUUID().toString();
        pipeline.setPipelineId(pipelineId);
        pipeline.setRunning(false);
        pipeline.setCreatedByUser(username);
        pipeline.setCreatedAt(new Date().getTime());
        //userService.addOwnPipeline(username, preprocessing);
        Operations.storePipeline(pipeline);
        SuccessMessage message = Notifications.success(NotificationType.PIPELINE_STORAGE_SUCCESS);
        message.addNotification(new Notification("id", pipelineId));
        return ok(message);
    }

    @Path("/recommend")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    public Response recommend(@PathParam("username") String email, org.streampipes.model.client.pipeline.Pipeline pipeline) {
        try {
            return ok(Operations.findRecommendedElements(email, pipeline));
        } catch (JsonSyntaxException e) {
            return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
        } catch (NoSuitableSepasAvailableException e) {
            return constructErrorMessage(new Notification(NotificationType.NO_SEPA_FOUND.title(), NotificationType.NO_SEPA_FOUND.description(), e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
        }
    }

    @Path("/update/dataset")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    public Response updateDataSet(org.streampipes.model.SpDataSet spDataSet, @PathParam("username")
            String username) {
        return ok(Operations.updateDataSet(spDataSet));
    }

    @Path("/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    public Response update(org.streampipes.model.client.pipeline.Pipeline pipeline, @PathParam("username") String username) {
        try {
            return ok(Operations.validatePipeline(pipeline, true, username));
        } catch (JsonSyntaxException e) {
            return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
        } catch (NoMatchingSchemaException e) {
            return constructErrorMessage(new Notification(NotificationType.NO_VALID_CONNECTION.title(), NotificationType.NO_VALID_CONNECTION.description(), e.getMessage()));
        } catch (NoMatchingFormatException e) {
            return constructErrorMessage(new Notification(NotificationType.NO_MATCHING_FORMAT_CONNECTION.title(), NotificationType.NO_MATCHING_FORMAT_CONNECTION.description(), e.getMessage()));
        } catch (NoMatchingProtocolException e) {
            return constructErrorMessage(new Notification(NotificationType.NO_MATCHING_PROTOCOL_CONNECTION.title(), NotificationType.NO_MATCHING_PROTOCOL_CONNECTION.description(), e.getMessage()));
        } catch (RemoteServerNotAccessibleException e) {
            return constructErrorMessage(new Notification(NotificationType.REMOTE_SERVER_NOT_ACCESSIBLE.title(), NotificationType.REMOTE_SERVER_NOT_ACCESSIBLE.description(), e.getMessage()));
        } catch (NoMatchingJsonSchemaException e) {
            return constructErrorMessage(new Notification(NotificationType.REMOTE_SERVER_NOT_ACCESSIBLE.title(), NotificationType.REMOTE_SERVER_NOT_ACCESSIBLE.description(), e.getMessage()));
        }
        catch (InvalidConnectionException e) {
            return ok(e.getErrorLog());
        } catch (Exception e) {
            e.printStackTrace();
            return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
        }
    }


    @PUT
    @Path("/{pipelineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response overwritePipeline(@PathParam("username") String username, org.streampipes.model.client.pipeline.Pipeline pipeline) {
        getPipelineStorage().updatePipeline(pipeline);
        return statusMessage(Notifications.success("Pipeline modified"));
    }

}
