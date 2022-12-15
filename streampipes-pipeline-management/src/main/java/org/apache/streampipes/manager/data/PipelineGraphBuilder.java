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

package org.apache.streampipes.manager.data;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineGraphBuilder {

  private final Pipeline pipeline;
  private final List<NamedStreamPipesEntity> allPipelineElements;
  private final List<InvocableStreamPipesEntity> invocableElements;

  public PipelineGraphBuilder(Pipeline pipeline) {
    this.pipeline = pipeline;
    this.allPipelineElements = addAll();
    this.invocableElements = addInvocable();
  }

  private List<NamedStreamPipesEntity> addAll() {
    List<NamedStreamPipesEntity> allElements = new ArrayList<>();
    allElements.addAll(pipeline.getStreams());
    allElements.addAll(addInvocable());
    return allElements;
  }

  private List<InvocableStreamPipesEntity> addInvocable() {
    List<InvocableStreamPipesEntity> allElements = new ArrayList<>();
    allElements.addAll(pipeline.getSepas());
    allElements.addAll(pipeline.getActions());
    return allElements;
  }


  public PipelineGraph buildGraph() {
    PipelineGraph pipelineGraph = new PipelineGraph();
    allPipelineElements.forEach(pipelineGraph::addVertex);

    for (NamedStreamPipesEntity source : allPipelineElements) {
      List<InvocableStreamPipesEntity> targets = findTargets(source.getDom());
      targets.forEach(t -> pipelineGraph.addEdge(source, t, createEdge(source, t)));
    }

    return pipelineGraph;
  }

  private List<InvocableStreamPipesEntity> findTargets(String domId) {
    return invocableElements
        .stream()
        .filter(i -> i.getConnectedTo().contains(domId))
        .collect(Collectors.toList());
  }

  private String createEdge(NamedStreamPipesEntity sourceVertex,
                            NamedStreamPipesEntity targetVertex) {
    return sourceVertex.getDom() + "-" + targetVertex.getDom();
  }
}
