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

import org.apache.streampipes.model.canvas.PipelineCanvasMetadata;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.storage.api.IPipelineCanvasMetadataStorage;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v2/pipeline-canvas-metadata")
public class PipelineCanvasMetadataResource extends AbstractRestResource {

  @GET
  @Path("/pipeline/{pipelineId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getPipelineCanvasMetadataForPipeline(@PathParam("pipelineId") String pipelineId) {
    try {
      return ok(getPipelineCanvasMetadataStorage()
          .getPipelineCanvasMetadataForPipeline(pipelineId));
    } catch (IllegalArgumentException e) {
      return badRequest();
    }
  }

  @GET
  @Path("{canvasId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getPipelineCanvasMetadata(@PathParam("canvasId") String pipelineCanvasId) {
    try {
      return ok(getPipelineCanvasMetadataStorage()
          .getElementById(pipelineCanvasId));
    } catch (IllegalArgumentException e) {
      return badRequest();
    }
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response storePipelineCanvasMetadata(PipelineCanvasMetadata pipelineCanvasMetadata) {
    getPipelineCanvasMetadataStorage().createElement(pipelineCanvasMetadata);
    return ok();
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{canvasId}")
  @JacksonSerialized
  public Response deletePipelineCanvasMetadata(@PathParam("canvasId") String pipelineCanvasId) {
    PipelineCanvasMetadata metadata = find(pipelineCanvasId);
    getPipelineCanvasMetadataStorage().deleteElement(metadata);
    return ok();
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/pipeline/{pipelineId}")
  @JacksonSerialized
  public Response deletePipelineCanvasMetadataForPipeline(@PathParam("pipelineId") String pipelineId) {
    PipelineCanvasMetadata metadata =
        getPipelineCanvasMetadataStorage().getPipelineCanvasMetadataForPipeline(pipelineId);
    getPipelineCanvasMetadataStorage().deleteElement(metadata);
    return ok();
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{canvasId}")
  @JacksonSerialized
  public Response updatePipelineCanvasMetadata(@PathParam("canvasId") String pipelineCanvasId,
                                               PipelineCanvasMetadata pipelineCanvasMetadata) {
    try {
      getPipelineCanvasMetadataStorage().updateElement(pipelineCanvasMetadata);
    } catch (IllegalArgumentException e) {
      getPipelineCanvasMetadataStorage().createElement(pipelineCanvasMetadata);
    }
    return ok();
  }

  private PipelineCanvasMetadata find(String canvasId) {
    return getPipelineCanvasMetadataStorage().getElementById(canvasId);
  }

  private IPipelineCanvasMetadataStorage getPipelineCanvasMetadataStorage() {
    return getNoSqlStorage().getPipelineCanvasMetadataStorage();
  }
}
