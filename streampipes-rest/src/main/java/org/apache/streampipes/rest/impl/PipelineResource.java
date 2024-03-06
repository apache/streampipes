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
import org.apache.streampipes.model.client.exception.InvalidConnectionException;
import org.apache.streampipes.model.message.ErrorMessage;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.PipelineStatusMessage;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementRecommendationMessage;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.rest.shared.exception.SpNotificationException;

import com.google.gson.JsonSyntaxException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v2/pipelines")
@Component
public class PipelineResource extends AbstractAuthGuardedRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineResource.class);

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get all pipelines of the current user", tags = {"Pipeline"}, responses = {
      @ApiResponse(content = {
          @Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = Pipeline.class))
          )})})
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.pipelineId, 'READ')")
  public List<Pipeline> get() {
    return PipelineManager.getAllPipelines();
  }

  @GetMapping(
      path = "{pipelineId}/status",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get the pipeline status of a given pipeline", tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_PRIVILEGE)
  public List<PipelineStatusMessage> getPipelineStatus(@PathVariable("pipelineId") String pipelineId) {
    return PipelineStatusManager.getPipelineStatus(pipelineId, 5);
  }

  @DeleteMapping(
      path = "/{pipelineId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete a pipeline with a given id", tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_DELETE_PIPELINE_PRIVILEGE)
  public Message removeOwn(@PathVariable("pipelineId") String pipelineId) {
    PipelineManager.deletePipeline(pipelineId);
    return Notifications.success("Pipeline deleted");
  }

  @GetMapping(path = "/{pipelineId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get a specific pipeline with the given id", tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_PRIVILEGE)
  public ResponseEntity<Pipeline> getElement(@PathVariable("pipelineId") String pipelineId) {
    Pipeline foundPipeline = PipelineManager.getPipeline(pipelineId);

    if (foundPipeline == null) {
      throw new ResponseStatusException(NOT_FOUND, "Pipeline with " + pipelineId + " not found.");
    } else {
      return ok(PipelineManager.getPipeline(pipelineId));
    }
  }

  @GetMapping(path = "/{pipelineId}/start", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Start the pipeline with the given id", tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public ResponseEntity<?> start(@PathVariable("pipelineId") String pipelineId) {
    try {
      PipelineOperationStatus status = PipelineManager.startPipeline(pipelineId);

      return ok(status);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return statusMessage(Notifications.error(NotificationType.UNKNOWN_ERROR));
    }
  }

  @GetMapping(path = "/{pipelineId}/stop", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Stop the pipeline with the given id", tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public ResponseEntity<?> stop(@PathVariable("pipelineId") String pipelineId,
                                @RequestParam(value = "forceStop", defaultValue = "false") boolean forceStop) {
    try {
      PipelineOperationStatus status = PipelineManager.stopPipeline(pipelineId, forceStop);
      return ok(status);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(),
          NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
    }
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Store a new pipeline", tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public ResponseEntity<SuccessMessage> addPipeline(@RequestBody Pipeline pipeline) {

    String pipelineId = PipelineManager.addPipeline(getAuthenticatedUserSid(), pipeline);
    SuccessMessage message = Notifications.success("Pipeline stored");
    message.addNotification(new Notification("id", pipelineId));
    return ok(message);
  }

  @PostMapping(
      path = "/recommend/{recId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Hidden
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  @PostAuthorize("hasPermission(returnObject, 'READ')")
  public PipelineElementRecommendationMessage recommend(@RequestBody Pipeline pipeline,
                                                        @PathVariable("recId") String baseRecElement) {
    try {
      return Operations.findRecommendedElements(pipeline, baseRecElement);
    } catch (JsonSyntaxException e) {
      throw new SpNotificationException(
          HttpStatus.BAD_REQUEST,
          new Notification(NotificationType.UNKNOWN_ERROR,
              e.getMessage())
      );
    } catch (NoSuitableSepasAvailableException e) {
      throw new SpNotificationException(
          HttpStatus.BAD_REQUEST,
          new Notification(NotificationType.NO_SEPA_FOUND,
              e.getMessage())
      );
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new SpMessageException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          new ErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR, e.getMessage())));
    }
  }

  @PostMapping(
      path = "/validate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Hidden
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public ResponseEntity<?> validatePipeline(@RequestBody Pipeline pipeline) {
    try {
      return ok(Operations.validatePipeline(pipeline));
    } catch (JsonSyntaxException e) {
      return badRequest(new Notification(NotificationType.UNKNOWN_ERROR, e.getMessage()));
    } catch (NoMatchingSchemaException e) {
      return badRequest(new Notification(NotificationType.NO_VALID_CONNECTION, e.getMessage()));
    } catch (NoMatchingFormatException e) {
      return badRequest(new Notification(NotificationType.NO_MATCHING_FORMAT_CONNECTION, e.getMessage()));
    } catch (NoMatchingProtocolException e) {
      return badRequest(new Notification(NotificationType.NO_MATCHING_PROTOCOL_CONNECTION, e.getMessage()));
    } catch (RemoteServerNotAccessibleException | NoMatchingJsonSchemaException e) {
      return serverError(new Notification(NotificationType.REMOTE_SERVER_NOT_ACCESSIBLE, e.getMessage()));
    } catch (InvalidConnectionException e) {
      return badRequest(e.getErrorLog());
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return serverError(new Notification(NotificationType.UNKNOWN_ERROR, e.getMessage()));
    }
  }

  @PutMapping(
      path = "/{pipelineId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update an existing pipeline", tags = {"Pipeline"})
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_PRIVILEGE)
  public ResponseEntity<SuccessMessage> overwritePipeline(@PathVariable("pipelineId") String pipelineId,
                                                          @RequestBody Pipeline pipeline) {
    Pipeline storedPipeline = getPipelineStorage().getPipeline(pipelineId);
    if (!storedPipeline.isRunning()) {
      storedPipeline.setStreams(pipeline.getStreams());
      storedPipeline.setSepas(pipeline.getSepas());
      storedPipeline.setActions(pipeline.getActions());
    }
    storedPipeline.setCreatedAt(System.currentTimeMillis());
    storedPipeline.setPipelineCategories(pipeline.getPipelineCategories());
    storedPipeline.setHealthStatus(pipeline.getHealthStatus());
    storedPipeline.setPipelineNotifications(pipeline.getPipelineNotifications());
    storedPipeline.setValid(pipeline.isValid());
    Operations.updatePipeline(storedPipeline);
    SuccessMessage message = Notifications.success("Pipeline modified");
    message.addNotification(new Notification("id", pipelineId));
    return ok(message);
  }

  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      path = "/contains/{elementId}")
  @Operation(summary = "Returns all pipelines that contain the element with the elementId", tags = {
      "Pipeline"}, responses = {@ApiResponse(content = {
        @Content(mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = Pipeline.class)))})})
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.pipelineId, 'READ')")
  public List<Pipeline> getPipelinesContainingElement(@PathVariable("elementId") String elementId) {
    return PipelineManager.getPipelinesContainingElements(elementId);
  }

}
