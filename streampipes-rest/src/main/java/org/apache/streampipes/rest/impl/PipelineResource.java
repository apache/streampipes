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



import org.apache.streampipes.commons.exceptions.NoMatchingFormatException;
import org.apache.streampipes.commons.exceptions.NoMatchingJsonSchemaException;
import org.apache.streampipes.commons.exceptions.NoMatchingProtocolException;
import org.apache.streampipes.commons.exceptions.NoMatchingSchemaException;
import org.apache.streampipes.commons.exceptions.NoSuitableSepasAvailableException;
import org.apache.streampipes.commons.exceptions.RemoteServerNotAccessibleException;
import org.apache.streampipes.manager.execution.status.PipelineStatusManager;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.client.exception.InvalidConnectionException;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementRecommendationMessage;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import com.google.gson.JsonSyntaxException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

@Component
@Path("/v2/pipelines")
public class PipelineResource extends AbstractAuthGuardedRestResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/")
  @JacksonSerialized
  @Operation(summary = "Get all pipelines of the current user",
      tags = {"Pipeline"},
      responses = {
          @ApiResponse(content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Pipeline.class)))
          })})
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.pipelineId, 'READ')")
  public List<Pipeline> get() {
    return PipelineManager.getAllPipelines();
  }

  /**
   * @deprecated use {@link #get()} instead.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/own")
  @JacksonSerialized
  @Operation(summary = "Get all pipelines assigned to the current user",
      tags = {"Pipeline"},
      responses = {
          @ApiResponse(content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Pipeline.class)))
          })})
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.pipelineId, 'READ')")
  @Deprecated(since = "0.71.0", forRemoval = true)
  public List<Pipeline> getOwn() {
    return PipelineManager.getAllPipelines();
  }

  /**
   * @deprecated use {@link #get()} instead.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/system")
  @JacksonSerialized
  @Operation(summary = "Get all system pipelines assigned to the current user",
      tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_PRIVILEGE)
  @Deprecated(since = "0.71.0", forRemoval = true)
  public Response getSystemPipelines() {
    return ok(getPipelineStorage().getSystemPipelines());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{pipelineId}/status")
  @JacksonSerialized
  @Operation(summary = "Get the pipeline status of a given pipeline",
      tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_PRIVILEGE)
  public Response getPipelineStatus(@PathParam("pipelineId") String pipelineId) {
    return ok(PipelineStatusManager.getPipelineStatus(pipelineId, 5));
  }

  @DELETE
  @Path("/{pipelineId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Operation(summary = "Delete a pipeline with a given id",
      tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_DELETE_PIPELINE_PRIVILEGE)
  public Response removeOwn(@PathParam("pipelineId") String pipelineId) {
    PipelineManager.deletePipeline(pipelineId);
    return statusMessage(Notifications.success("Pipeline deleted"));
  }

  @GET
  @Path("/{pipelineId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Operation(summary = "Get a specific pipeline with the given id",
      tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_PRIVILEGE)
  public Response getElement(@PathParam("pipelineId") String pipelineId) {
    return ok(PipelineManager.getPipeline(pipelineId));
  }

  @Path("/{pipelineId}/start")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Operation(summary = "Start the pipeline with the given id",
      tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public Response start(@PathParam("pipelineId") String pipelineId) {
    try {
      PipelineOperationStatus status = PipelineManager.startPipeline(pipelineId);

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
  @Operation(summary = "Stop the pipeline with the given id",
      tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public Response stop(@PathParam("pipelineId") String pipelineId,
                       @QueryParam("forceStop") @DefaultValue("false") boolean forceStop) {
    try {
      PipelineOperationStatus status = PipelineManager.stopPipeline(pipelineId, forceStop);
      return ok(status);
    } catch
    (Exception e) {
      e.printStackTrace();
      return constructErrorMessage(
          new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(),
              e.getMessage()));
    }
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Operation(summary = "Store a new pipeline",
      tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public Response addPipeline(Pipeline pipeline) {

    String pipelineId = PipelineManager.addPipeline(getAuthenticatedUserSid(), pipeline);
    SuccessMessage message = Notifications.success("Pipeline stored");
    message.addNotification(new Notification("id", pipelineId));
    return ok(message);
  }

  @Path("/recommend/{recId}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Hidden
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  @PostAuthorize("hasPermission(returnObject, 'READ')")
  public PipelineElementRecommendationMessage recommend(Pipeline pipeline,
                                                        @PathParam("recId") String baseRecElement) {
    try {
      return Operations.findRecommendedElements(pipeline, baseRecElement);
    } catch (JsonSyntaxException e) {
      throw new WebApplicationException(badRequest(new Notification(NotificationType.UNKNOWN_ERROR,
          e.getMessage())));
    } catch (NoSuitableSepasAvailableException e) {
      throw new WebApplicationException(badRequest(new Notification(NotificationType.NO_SEPA_FOUND,
          e.getMessage())));
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(
          serverError(constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR,
              e.getMessage()))));
    }
  }

  @Path("/update/dataset")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Hidden
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public Response updateDataSet(SpDataSet spDataSet) {
    return ok(Operations.updateDataSet(spDataSet));
  }

  @Path("/update")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Hidden
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public Response update(Pipeline pipeline) {
    try {
      return ok(Operations.validatePipeline(pipeline));
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
  @JacksonSerialized
  @Operation(summary = "Update an existing pipeline",
      tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public Response overwritePipeline(@PathParam("pipelineId") String pipelineId,
                                    Pipeline pipeline) {
    Pipeline storedPipeline = getPipelineStorage().getPipeline(pipelineId);
    if (!storedPipeline.isRunning()) {
      storedPipeline.setStreams(pipeline.getStreams());
      storedPipeline.setSepas(pipeline.getSepas());
      storedPipeline.setActions(pipeline.getActions());
    }
    storedPipeline.setCreatedAt(System.currentTimeMillis());
    storedPipeline.setPipelineCategories(pipeline.getPipelineCategories());
    storedPipeline.setPipelineNotifications(pipeline.getPipelineNotifications());
    Operations.updatePipeline(storedPipeline);
    SuccessMessage message = Notifications.success("Pipeline modified");
    message.addNotification(new Notification("id", pipelineId));
    return ok(message);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/contains/{elementId}")
  @JacksonSerialized
  @Operation(summary = "Returns all pipelines that contain the element with the elementId",
      tags = {"Pipeline"},
      responses = {
          @ApiResponse(content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Pipeline.class)))
          })})
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.pipelineId, 'READ')")
  public List<Pipeline> getPipelinesContainingElement(@PathParam("elementId") String elementId) {
    return PipelineManager.getPipelinesContainingElements(elementId);
  }

}
