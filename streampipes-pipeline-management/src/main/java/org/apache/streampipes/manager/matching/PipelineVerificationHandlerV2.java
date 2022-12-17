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
package org.apache.streampipes.manager.matching;

import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphBuilder;
import org.apache.streampipes.manager.recommender.AllElementsProvider;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineModification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PipelineVerificationHandlerV2 {

  private final Pipeline pipeline;

  public PipelineVerificationHandlerV2(Pipeline pipeline) {
    this.pipeline = pipeline;
  }

  public PipelineModificationMessage verifyPipeline() {
    PipelineGraph graph = new PipelineGraphBuilder(pipeline).buildGraph();
    return new PipelineModificationGenerator(graph).buildPipelineModificationMessage();
  }

  public List<NamedStreamPipesEntity> verifyAndBuildGraphs(boolean ignoreUnconfigured) {
    List<PipelineModification> pipelineModifications = verifyPipeline().getPipelineModifications();
    List<NamedStreamPipesEntity> allElements = new AllElementsProvider(pipeline).getAllElements();
    List<NamedStreamPipesEntity> result = new ArrayList<>();
    allElements.forEach(pipelineElement -> {
      Optional<PipelineModification> modificationOpt = getModification(pipelineElement.getDom(), pipelineModifications);
      if (modificationOpt.isPresent()) {
        PipelineModification modification = modificationOpt.get();
        if (pipelineElement instanceof InvocableStreamPipesEntity) {
          ((InvocableStreamPipesEntity) pipelineElement).setInputStreams(modification.getInputStreams());
          ((InvocableStreamPipesEntity) pipelineElement).setStaticProperties(modification.getStaticProperties());
          if (pipelineElement instanceof DataProcessorInvocation) {
            ((DataProcessorInvocation) pipelineElement).setOutputStream(modification.getOutputStream());
            if (((DataProcessorInvocation) pipelineElement).getOutputStream().getEventGrounding() == null) {
              EventGrounding grounding =
                  new GroundingBuilder(pipelineElement, Collections.emptySet()).getEventGrounding();
              ((DataProcessorInvocation) pipelineElement).getOutputStream().setEventGrounding(grounding);
            }
            if (modification.getOutputStrategies() != null) {
              ((DataProcessorInvocation) pipelineElement).setOutputStrategies(modification.getOutputStrategies());
            }
          }
        }
        if (!ignoreUnconfigured || modification.isPipelineElementValid()) {
          result.add(pipelineElement);
        }
      } else {
        result.add(pipelineElement);
      }
    });

    return result;
  }

  private Optional<PipelineModification> getModification(String id,
                                                         List<PipelineModification> modifications) {
    return modifications.stream().filter(m -> m.getDomId().equals(id)).findFirst();
  }
}
