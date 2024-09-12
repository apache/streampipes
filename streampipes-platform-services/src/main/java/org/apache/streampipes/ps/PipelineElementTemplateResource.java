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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/pipeline-element-templates")
public class PipelineElementTemplateResource extends AbstractRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get a list of all pipeline element templates",
             tags = {"Pipeline Element Templates"},
             responses = {
                 @ApiResponse(content = {
                     @Content(
                         mediaType = "application/json",
                         array = @ArraySchema(schema = @Schema(implementation = PipelineElementTemplate.class)))
                 })
             })
  public ResponseEntity<List<PipelineElementTemplate>> getAll(
      @Parameter(description = "Filter all templates by this appId")
      @RequestParam("appId") String appId
  ) {
    if (appId == null) {
      return ok(getPipelineElementTemplateStorage().findAll());
    } else {
      return ok(getPipelineElementTemplateStorage().getPipelineElementTemplatesforAppId(appId));
    }
  }

  @GetMapping(path = "{id}", produces = { MediaType.APPLICATION_JSON_VALUE, "application/yaml" })
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
  public ResponseEntity<?> getById(
      @Parameter(description = "The id of the pipeline element template", required = true)
      @PathVariable("id") String s
  ) {
    try {
      return ok(getPipelineElementTemplateStorage().getElementById(s));
    } catch (RuntimeException e) {
      return badRequest();
    }
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Store a new pipeline element template",
             tags = {"Pipeline Element Templates"},
             responses = {
                 @ApiResponse(responseCode = "200", description = "Template successfully stored")
             })
  public ResponseEntity<Void> create(
      @RequestBody(description = "The pipeline element template to be stored",
                   content = @Content(schema = @Schema(implementation = PipelineElementTemplate.class)))
      @org.springframework.web.bind.annotation.RequestBody PipelineElementTemplate entity
  ) {
    getPipelineElementTemplateStorage().persist(entity);
    return ok();
  }

  @PutMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
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
  public ResponseEntity<?> update(
      @Parameter(description = "The id of the pipeline element template", required = true)
      @PathVariable("id") String id,
      @RequestBody PipelineElementTemplate entity
  ) {
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

  @DeleteMapping(path = "{id}")
  @Operation(summary = "Delete a pipeline element template by a given id",
             tags = {"Pipeline Element Templates"},
             responses = {
                 @ApiResponse(responseCode = "200", description = "Pipeline element template successfully deleted"),
                 @ApiResponse(responseCode = "400", description = "Template with given id not found")
             })
  public ResponseEntity<Void> delete(
      @Parameter(description = "The id of the pipeline element template", required = true)
      @PathVariable("id") String s
  ) {
    PipelineElementTemplate template = getPipelineElementTemplateStorage().getElementById(s);
    getPipelineElementTemplateStorage().deleteElement(template);
    return ok();
  }

  @PostMapping(
      path = "{id}/sink",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Configure a data sink with a pipeline element template.",
             tags = {"Pipeline Element Templates"},
             responses = {
                 @ApiResponse(content = {
                     @Content(
                         mediaType = "application/json",
                         schema = @Schema(implementation = DataSinkInvocation.class))
                 }, responseCode = "200", description = "The configured data sink invocation model"),
             })
  public ResponseEntity<DataSinkInvocation> getPipelineElementForTemplate(
      @Parameter(description = "The id of the pipeline element template", required = true)
      @PathVariable("id") String id,

      @Parameter(
          description = "Overwrite the name and description of the pipeline element"
              + "with the labels given in the pipeline element template")
      @RequestParam(value = "overwriteNames", defaultValue = "false", required = false)
      String overwriteNameAndDescription,
      @RequestBody(description = "The data sink invocation that should be configured with the template contents",
                   content = @Content(schema = @Schema(implementation = DataSinkInvocation.class)))
      @org.springframework.web.bind.annotation.RequestBody DataSinkInvocation invocation
  ) {
    PipelineElementTemplate template = getPipelineElementTemplateStorage().getElementById(id);
    return ok(new DataSinkTemplateHandler(template, invocation, Boolean.parseBoolean(overwriteNameAndDescription))
                  .applyTemplateOnPipelineElement());
  }

  @PostMapping(
      path = "{id}/processor",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Configure a data processor with a pipeline element template.",
             tags = {"Pipeline Element Templates"},
             responses = {
                 @ApiResponse(content = {
                     @Content(
                         mediaType = "application/json",
                         schema = @Schema(implementation = DataProcessorInvocation.class))
                 }, responseCode = "200", description = "The configured data processor invocation model"),
             })
  public ResponseEntity<DataProcessorInvocation> getPipelineElementForTemplate(
      @Parameter(description = "The id of the pipeline element template", required = true)
      @PathVariable("id") String id,

      @Parameter(description = "Overwrite the name and description of the pipeline element with"
          + "the labels given in the pipeline element template")
      @RequestParam(value = "overwriteNames", defaultValue = "false", required = false)
      String overwriteNameAndDescription,

      @RequestBody(description = "The data processor invocation that should be configured with the template contents",
                   content = @Content(schema = @Schema(implementation = DataProcessorInvocation.class)))
      @org.springframework.web.bind.annotation.RequestBody DataProcessorInvocation invocation
  ) {
    PipelineElementTemplate template = getPipelineElementTemplateStorage().getElementById(id);
    return ok(new DataProcessorTemplateHandler(template, invocation, Boolean.parseBoolean(overwriteNameAndDescription))
                  .applyTemplateOnPipelineElement());
  }

  @PostMapping(
      path = "{id}/adapter",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Configure an adapter with a pipeline element template.",
             tags = {"Pipeline Element Templates"},
             responses = {
                 @ApiResponse(content = {
                     @Content(
                         mediaType = "application/json",
                         schema = @Schema(implementation = AdapterDescription.class))
                 }, responseCode = "200", description = "The configured adapter model"),
             })
  public ResponseEntity<AdapterDescription> getPipelineElementForTemplate(
      @Parameter(description = "The id of the pipeline element template", required = true)
      @PathVariable("id") String id,

      @Parameter(description = "Overwrite the name and description of the pipeline element"
          + "with the labels given in the pipeline element template")
      @RequestParam(value = "overwriteNames", defaultValue = "false", required = false)
      String overwriteNameAndDescription,

      @RequestBody(description = "The adapter that should be configured with the template contents",
                   content = @Content(schema = @Schema(implementation = AdapterDescription.class)))
      @org.springframework.web.bind.annotation.RequestBody AdapterDescription adapterDescription
  ) {
    PipelineElementTemplate template = getPipelineElementTemplateStorage().getElementById(id);
    var desc =
        new AdapterTemplateHandler(template, adapterDescription, Boolean.parseBoolean(overwriteNameAndDescription))
            .applyTemplateOnPipelineElement();
    return ok(desc);
  }
}
