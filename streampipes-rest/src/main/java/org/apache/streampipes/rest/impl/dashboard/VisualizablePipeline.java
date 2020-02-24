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

package org.apache.streampipes.rest.impl.dashboard;

import org.apache.streampipes.rest.api.dashboard.IVisualizablePipeline;
import org.apache.streampipes.rest.impl.AbstractRestInterface;
import org.apache.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.apache.streampipes.rest.shared.util.SpMediaType;
import org.apache.streampipes.storage.api.IVisualizablePipelineStorage;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/ld/pipelines")
public class VisualizablePipeline extends AbstractRestInterface implements IVisualizablePipeline {

  @GET
  @JsonLdSerialized
  @Produces(SpMediaType.JSONLD)
  @Override
  public Response getVisualizablePipelines() {
    return ok(asContainer(getVisualizablePipelineStorage().getAllVisualizablePipelines()));
  }

  @GET
  @JsonLdSerialized
  @Produces(SpMediaType.JSONLD)
  @Path("/{id}")
  @Override
  public Response getVisualizablePipeline(@PathParam("id") String id) {
    org.apache.streampipes.model.dashboard.VisualizablePipeline pipeline = getVisualizablePipelineStorage().getVisualizablePipeline(id);
   return pipeline != null ? ok(pipeline) : fail();
  }

  @GET
  @JsonLdSerialized
  @Produces(SpMediaType.JSONLD)
  @Path("topic/{topic}")
  @Override
  public Response getVisualizablePipelineByTopic(@PathParam("topic") String topic) {
    List<org.apache.streampipes.model.dashboard.VisualizablePipeline> pipelines =
            getVisualizablePipelineStorage().getAllVisualizablePipelines();

    Optional<org.apache.streampipes.model.dashboard.VisualizablePipeline> matchedPipeline =
            pipelines.stream().filter(pipeline -> pipeline.getTopic().equals(topic)).findFirst();

    return matchedPipeline.isPresent() ? ok(matchedPipeline.get()) : fail();
  }

  private IVisualizablePipelineStorage getVisualizablePipelineStorage() {
    return getNoSqlStorage().getVisualizablePipelineStorage();
  }
}
