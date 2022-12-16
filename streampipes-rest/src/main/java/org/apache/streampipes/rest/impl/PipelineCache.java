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

import org.apache.streampipes.manager.pipeline.PipelineCacheManager;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/pipeline-cache")
public class PipelineCache extends AbstractAuthGuardedRestResource {

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateCachedPipeline(String rawPipelineModel) {
    PipelineCacheManager.updateCachedPipeline(getAuthenticatedUsername(), rawPipelineModel);
    return ok();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCachedPipeline() {
    String result = PipelineCacheManager.getCachedPipeline(getAuthenticatedUsername());
    if (result != null) {
      return ok(result);
    } else {
      return ok();
    }
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  public Response removePipelineFromCache() {
    PipelineCacheManager.removeCachedPipeline(getAuthenticatedUsername());
    return ok();
  }
}
