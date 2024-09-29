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

import org.apache.streampipes.model.dashboard.VisualizablePipeline;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.rest.security.AuthConstants;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/dashboard/pipelines")
public class VisualizablePipelineResource extends AbstractPipelineExtractionResource<VisualizablePipeline> {

  private static final String DashboardAppId = "org.apache.streampipes.sinks.internal.jvm.dashboard";
  private static final String VisualizationFieldInternalName = "visualization-name";

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_DASHBOARD_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.pipelineId, 'READ')")
  public List<VisualizablePipeline> getVisualizablePipelines() {
    return extract(new ArrayList<>(), DashboardAppId);
  }

  @GetMapping(path = "{pipelineId}/{visualizationName}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getVisPipelineByIdAndVisualizationName(@PathVariable("pipelineId") String pipelineId,
          @PathVariable("visualizationName") String visualizationName) {
    return getPipelineByIdAndFieldValue(DashboardAppId, pipelineId, visualizationName);
  }

  private String makeTopic(DataSinkInvocation sink) {
    return extractInputTopic(sink) + "-" + normalize(extractFieldValue(sink, VisualizationFieldInternalName));
  }

  private String normalize(String visualizationName) {
    return visualizationName.replaceAll(" ", "").toLowerCase();
  }

  @Override
  protected org.apache.streampipes.model.dashboard.VisualizablePipeline convert(Pipeline pipeline,
          DataSinkInvocation sink) {
    VisualizablePipeline visualizablePipeline = new org.apache.streampipes.model.dashboard.VisualizablePipeline();
    visualizablePipeline.setPipelineId(pipeline.getPipelineId());
    visualizablePipeline.setPipelineName(pipeline.getName());
    visualizablePipeline.setVisualizationName(extractFieldValue(sink, VisualizationFieldInternalName));
    visualizablePipeline.setSchema(sink.getInputStreams().get(0).getEventSchema());
    visualizablePipeline.setTopic(makeTopic(sink));
    return visualizablePipeline;
  }

  @Override
  protected boolean matches(VisualizablePipeline pipeline, String pipelineId, String visualizationName) {
    return pipeline.getPipelineId().equals(pipelineId) && pipeline.getVisualizationName().equals(visualizationName);
  }
}
