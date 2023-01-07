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
package org.apache.streampipes.ps;

import org.apache.streampipes.manager.template.AdapterTemplateHandler;
import org.apache.streampipes.manager.template.DataProcessorTemplateHandler;
import org.apache.streampipes.manager.template.DataSinkTemplateHandler;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v2/pipeline-element-templates")
public class PipelineElementTemplateResource extends AbstractRestResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Operation(summary = "Get a list of all pipeline element templates",
      tags = {"Pipeline Element Templates"},
      responses = {
          @ApiResponse(content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = PipelineElementTemplate.class)))
          })
      })
  public Response getAll(@Parameter(description = "Filter all templates by this appId")
                         @QueryParam("appId") String appId) {
    if (appId == null) {
      return ok(getPipelineElementTemplateStorage().getAll());
    } else {
      return ok(getPipelineElementTemplateStorage().getPipelineElementTemplatesforAppId(appId));
    }
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Operation(summary = "Get a single pipeline element template by a given id",
      tags = {"Pipeline Element Templates"},
      responses = {
          @ApiResponse(content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PipelineElementTemplate.class))
          }),
          @ApiResponse(responseCode = "400", description = "Template with given id not found")
      })
  public Response getById(@Parameter(description = "The id of the pipeline element template", required = true)
                          @PathParam("id") String s) {
    try {
      return ok(getPipelineElementTemplateStorage().getElementById(s));
    } catch (RuntimeException e) {
      return badRequest();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Operation(summary = "Store a new pipeline element template",
      tags = {"Pipeline Element Templates"},
      responses = {
          @ApiResponse(responseCode = "200", description = "Template successfully stored")
      })
  public Response create(@RequestBody(description = "The pipeline element template to be stored",
      content = @Content(schema = @Schema(implementation = PipelineElementTemplate.class)))
                         PipelineElementTemplate entity) {
    getPipelineElementTemplateStorage().createElement(entity);
    return ok();
  }

  @PUT
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Operation(summary = "Update a pipeline element template",
      tags = {"Pipeline Element Templates"},
      responses = {
          @ApiResponse(content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PipelineElementTemplate.class))
          }, responseCode = "200", description = "Template successfully updated"),
          @ApiResponse(responseCode = "400", description = "Template with given id not found")
      })
  public Response update(@Parameter(description = "The id of the pipeline element template", required = true)
                         @PathParam("id") String id, PipelineElementTemplate entity) {
    try {
      if (id.equals(entity.getCouchDbId())) {
        return ok(getPipelineElementTemplateStorage().updateElement(entity));
      } else {
        return badRequest();
      }
    } catch (RuntimeException e) {
      return badRequest();
    }
  }

  @DELETE
  @Path("{id}")
  @Operation(summary = "Delete a pipeline element template by a given id",
      tags = {"Pipeline Element Templates"},
      responses = {
          @ApiResponse(responseCode = "200", description = "Pipeline element template successfully deleted"),
          @ApiResponse(responseCode = "400", description = "Template with given id not found")
      })
  public Response delete(@Parameter(description = "The id of the pipeline element template", required = true)
                         @PathParam("id") String s) {
    PipelineElementTemplate template = getPipelineElementTemplateStorage().getElementById(s);
    getPipelineElementTemplateStorage().deleteElement(template);
    return ok();
  }

  @POST
  @Path("{id}/sink")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Operation(summary = "Configure a data sink with a pipeline element template.",
      tags = {"Pipeline Element Templates"},
      responses = {
          @ApiResponse(content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = DataSinkInvocation.class))
          }, responseCode = "200", description = "The configured data sink invocation model"),
      })
  public Response getPipelineElementForTemplate(
      @Parameter(description = "The id of the pipeline element template", required = true)
      @PathParam("id") String id,

      @Parameter(
          description = "Overwrite the name and description of the pipeline element"
              + "with the labels given in the pipeline element template")
      @QueryParam("overwriteNames") String overwriteNameAndDescription,

      @RequestBody(description = "The data sink invocation that should be configured with the template contents",
          content = @Content(schema = @Schema(implementation = DataSinkInvocation.class)))
      DataSinkInvocation invocation) {
    PipelineElementTemplate template = getPipelineElementTemplateStorage().getElementById(id);
    return ok(new DataSinkTemplateHandler(template, invocation, Boolean.parseBoolean(overwriteNameAndDescription))
        .applyTemplateOnPipelineElement());
  }

  @POST
  @Path("{id}/processor")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Operation(summary = "Configure a data processor with a pipeline element template.",
      tags = {"Pipeline Element Templates"},
      responses = {
          @ApiResponse(content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = DataProcessorInvocation.class))
          }, responseCode = "200", description = "The configured data processor invocation model"),
      })
  public Response getPipelineElementForTemplate(
      @Parameter(description = "The id of the pipeline element template", required = true)
      @PathParam("id") String id,

      @Parameter(description = "Overwrite the name and description of the pipeline element with"
          + "the labels given in the pipeline element template")
      @QueryParam("overwriteNames") String overwriteNameAndDescription,

      @RequestBody(description = "The data processor invocation that should be configured with the template contents",
          content = @Content(schema = @Schema(implementation = DataProcessorInvocation.class)))
      DataProcessorInvocation invocation) {
    PipelineElementTemplate template = getPipelineElementTemplateStorage().getElementById(id);
    return ok(new DataProcessorTemplateHandler(template, invocation, Boolean.parseBoolean(overwriteNameAndDescription))
        .applyTemplateOnPipelineElement());
  }

  @POST
  @Path("{id}/adapter")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Operation(summary = "Configure an adapter with a pipeline element template.",
      tags = {"Pipeline Element Templates"},
      responses = {
          @ApiResponse(content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AdapterDescription.class))
          }, responseCode = "200", description = "The configured adapter model"),
      })
  public Response getPipelineElementForTemplate(
      @Parameter(description = "The id of the pipeline element template", required = true)
      @PathParam("id") String id,

      @Parameter(description = "Overwrite the name and description of the pipeline element"
          + "with the labels given in the pipeline element template")
      @QueryParam("overwriteNames") String overwriteNameAndDescription,

      @RequestBody(description = "The adapter that should be configured with the template contents",
          content = @Content(schema = @Schema(implementation = AdapterDescription.class)))
      AdapterDescription adapterDescription) {
    PipelineElementTemplate template = getPipelineElementTemplateStorage().getElementById(id);
    var desc =
        new AdapterTemplateHandler(template, adapterDescription, Boolean.parseBoolean(overwriteNameAndDescription))
            .applyTemplateOnPipelineElement();
    return ok(desc);
  }
}
