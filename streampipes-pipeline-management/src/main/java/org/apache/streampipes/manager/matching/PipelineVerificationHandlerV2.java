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

import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphBuilder;
import org.apache.streampipes.manager.matching.v2.pipeline.PipelineValidationSteps;
import org.apache.streampipes.manager.recommender.AllElementsProvider;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.pipeline.ExtendedPipelineElementValidationInfo;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineModification;
import org.apache.streampipes.model.pipeline.PipelineModificationResult;
import org.apache.streampipes.model.pipeline.PipelineVerificationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PipelineVerificationHandlerV2 {

  private final Pipeline pipeline;
  private final ExtensionServiceRequestManager requestManager;

  public PipelineVerificationHandlerV2(Pipeline pipeline,
                                       ExtensionServiceRequestManager requestManager) {
    this.pipeline = pipeline;
    this.requestManager = requestManager;
  }

  public PipelineModificationMessage verifyPipeline() {
    PipelineGraph graph = new PipelineGraphBuilder(pipeline).buildGraph();
    var steps = new PipelineValidationSteps().collect(requestManager);
    return new PipelineModificationGenerator(graph, steps).buildPipelineModificationMessage();
  }

  public PipelineModificationResult makeModifiedPipeline() {
    return makeModifiedPipeline(verifyPipeline());
  }

  public PipelineModificationResult makeModifiedPipeline(PipelineModificationMessage modificationMessage) {
    var result = verifyAndBuildGraphs(modificationMessage, false);
    var allElements = result.modifiedPipelineElements();
    pipeline.setSepas(filterAndConvert(allElements, DataProcessorInvocation.class));
    pipeline.setActions(filterAndConvert(allElements, DataSinkInvocation.class));
    return new PipelineModificationResult(pipeline, result.validationInfos());
  }

  private <T extends InvocableStreamPipesEntity> List<T> filterAndConvert(List<NamedStreamPipesEntity> elements,
                                                                          Class<T> clazz) {
    return elements
        .stream()
        .filter(clazz::isInstance)
        .map(clazz::cast)
        .toList();
  }

  public PipelineVerificationResult verifyAndBuildGraphs(boolean ignoreUnconfigured) {
    return verifyAndBuildGraphs(verifyPipeline(), ignoreUnconfigured);
  }

  public PipelineVerificationResult verifyAndBuildGraphs(PipelineModificationMessage modificationMessage,
                                                        boolean ignoreUnconfigured) {
    var pipelineModifications = modificationMessage.getPipelineModifications();
    var allElements = new AllElementsProvider(pipeline).getAllElements();
    var validationInfos = new ArrayList<ExtendedPipelineElementValidationInfo>();
    var modifiedPipelineElements = new ArrayList<NamedStreamPipesEntity>();
    allElements.forEach(pipelineElement -> {
      var modificationOpt = getModification(pipelineElement.getDom(), pipelineModifications);
      if (modificationOpt.isPresent()) {
        PipelineModification modification = modificationOpt.get();
        if (pipelineElement instanceof InvocableStreamPipesEntity) {
          applyModificationsForInvocable((InvocableStreamPipesEntity) pipelineElement, modification);
          if (pipelineElement instanceof DataProcessorInvocation) {
            applyModificationsForDataProcessor((DataProcessorInvocation) pipelineElement, modification);
          }
        }
        validationInfos.addAll(
            modification.getValidationInfos()
                .stream()
                .map(v -> new ExtendedPipelineElementValidationInfo(
                        pipelineElement.getName(),
                        pipelineElement.getDom(),
                        v
                    )
                )
                .toList()
        );
        if (!ignoreUnconfigured || modification.isPipelineElementValid()) {
          modifiedPipelineElements.add(pipelineElement);
        }
      } else {
        modifiedPipelineElements.add(pipelineElement);
      }
    });

    return new PipelineVerificationResult(validationInfos, modifiedPipelineElements);
  }

  private void applyModificationsForDataProcessor(DataProcessorInvocation pipelineElement,
                                                  PipelineModification modification) {
    if (modification.getOutputStream() != null) {
      pipelineElement.setOutputStream(modification.getOutputStream());
      if (pipelineElement.getOutputStream().getEventGrounding() == null) {
        EventGrounding grounding =
            new GroundingBuilder(pipelineElement, Collections.emptySet()).getEventGrounding();
        pipelineElement.getOutputStream().setEventGrounding(grounding);
      }
    }
    if (modification.getOutputStrategies() != null) {
      pipelineElement.setOutputStrategies(modification.getOutputStrategies());
    }
  }

  private void applyModificationsForInvocable(InvocableStreamPipesEntity pipelineElement,
                                              PipelineModification modification) {
    pipelineElement.setInputStreams(modification.getInputStreams());
    pipelineElement.setStaticProperties(modification.getStaticProperties());
  }

  private Optional<PipelineModification> getModification(String id,
                                                         List<PipelineModification> modifications) {
    return modifications.stream().filter(m -> m.getDomId().equals(id)).findFirst();
  }
}
