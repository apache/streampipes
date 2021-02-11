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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;

@Path("/v2/users/{username}/pipeline-cache")
public class PipelineCache extends AbstractRestResource {

  private static ConcurrentHashMap<String, String> cachedPipelines = new ConcurrentHashMap<>();

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateCachedPipeline(@PathParam("username") String user,
                                       String rawPipelineModel) {
    cachedPipelines.put(user, rawPipelineModel);
    return ok();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCachedPipeline(@PathParam("username") String user) {
    if (cachedPipelines.containsKey(user)) {
      return ok(cachedPipelines.get(user));
    } else {
      return ok();
    }
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  public Response removePipelineFromCache(@PathParam("username") String user) {
    cachedPipelines.remove(user);
    return ok();
  }
}
