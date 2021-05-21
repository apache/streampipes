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

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.rest.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/v2/users/{username}/dashboard/pipelines")
public class VisualizablePipeline extends AbstractRestResource {

  private static final String DashboardAppId = "org.apache.streampipes.sinks.internal.jvm.dashboard";
  private static final String VisualizationFieldInternalName = "visualization-name";
  private static final String Slash = "/";

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  public Response getVisualizablePipelines(@PathParam("username") String email) {
    return ok(extractVisualizablePipelines(email));
  }

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{pipelineId}/{visualizationName}")
  public Response getVisualizablePipelineByPipelineIdAndVisualizationName(@PathParam("username") String email,
                                                                          @PathParam("pipelineId") String pipelineId,
                                                                          @PathParam("visualizationName") String visualizationName) {
    List<org.apache.streampipes.model.dashboard.VisualizablePipeline> pipelines =
            extractVisualizablePipelines(email);

    Optional<org.apache.streampipes.model.dashboard.VisualizablePipeline> matchedPipeline =
            pipelines
                    .stream()
                    .filter(pipeline -> pipeline.getPipelineId().equals(pipelineId)
                            && pipeline.getVisualizationName().equals(visualizationName)).findFirst();

    return matchedPipeline.isPresent() ? ok(matchedPipeline.get()) : fail();
  }

  private List<org.apache.streampipes.model.dashboard.VisualizablePipeline> extractVisualizablePipelines(String email) {
    List<org.apache.streampipes.model.dashboard.VisualizablePipeline> visualizablePipelines = new ArrayList<>();
     getUserService()
            .getOwnPipelines(email)
            .forEach(pipeline -> {
              List<DataSinkInvocation> dashboardSinks = extractDashboardSinks(pipeline);
              dashboardSinks.forEach(sink -> {
                org.apache.streampipes.model.dashboard.VisualizablePipeline visualizablePipeline = new org.apache.streampipes.model.dashboard.VisualizablePipeline();
                visualizablePipeline.setPipelineId(pipeline.getPipelineId());
                visualizablePipeline.setPipelineName(pipeline.getName());
                visualizablePipeline.setVisualizationName(extractVisualizationName(sink));
                visualizablePipeline.setSchema(sink.getInputStreams().get(0).getEventSchema());
                visualizablePipeline.setTopic(sink.getElementId().substring(sink.getElementId().lastIndexOf(Slash) + 1));

                visualizablePipelines.add(visualizablePipeline);
              });
            });

     return visualizablePipelines;
  }

  private String extractVisualizationName(DataSinkInvocation sink) {
    return sink.getStaticProperties()
            .stream()
            .filter(sp -> sp.getInternalName().equals(VisualizationFieldInternalName))
            .map(sp -> (FreeTextStaticProperty) sp)
            .findFirst().get().getValue();
  }

  private List<DataSinkInvocation> extractDashboardSinks(Pipeline pipeline) {
    return pipeline
            .getActions()
            .stream()
            .filter(sink -> sink.getAppId().equals(DashboardAppId))
            .collect(Collectors.toList());
  }
}
