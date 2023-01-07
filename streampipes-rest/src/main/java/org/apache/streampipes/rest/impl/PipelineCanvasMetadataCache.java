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

import org.apache.streampipes.manager.pipeline.PipelineCanvasMetadataCacheManager;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v2/pipeline-canvas-cache")
public class PipelineCanvasMetadataCache extends AbstractAuthGuardedRestResource {

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateCachedCanvasMetadata(String canvasMetadata) {
    PipelineCanvasMetadataCacheManager.updateCachedCanvasMetadata(getAuthenticatedUsername(), canvasMetadata);
    return ok();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCachedCanvasMetadata() {
    String result = PipelineCanvasMetadataCacheManager.getCachedCanvasMetadata(getAuthenticatedUsername());
    if (result != null) {
      return ok(result);
    } else {
      return ok();
    }
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeCanvasMetadataFromCache() {
    PipelineCanvasMetadataCacheManager.removeCanvasMetadataFromCache(getAuthenticatedUsername());
    return ok();
  }
}
