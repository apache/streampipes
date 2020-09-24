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

import org.apache.streampipes.commons.exceptions.NoSepaInPipelineException;
import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphBuilder;
import org.apache.streampipes.manager.matching.mapping.AbstractRequirementsSelectorGenerator;
import org.apache.streampipes.manager.matching.mapping.RequirementsSelectorGeneratorFactory;
import org.apache.streampipes.manager.selector.PropertySelectorGenerator;
import org.apache.streampipes.manager.util.PipelineVerificationUtils;
import org.apache.streampipes.manager.util.TreeUtils;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.exception.InvalidConnectionException;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineModification;
import org.apache.streampipes.model.staticproperty.*;

import java.util.ArrayList;
import java.util.List;

public class PipelineVerificationHandler {

  private final Pipeline pipeline;
  private final PipelineModificationMessage pipelineModificationMessage;
  private final List<InvocableStreamPipesEntity> invocationGraphs;
  private final InvocableStreamPipesEntity rootPipelineElement;

  public PipelineVerificationHandler(Pipeline pipeline) throws NoSepaInPipelineException {
    this.pipeline = pipeline;
    this.rootPipelineElement = PipelineVerificationUtils.getRootNode(pipeline);
    this.invocationGraphs = makeInvocationGraphs();
    this.pipelineModificationMessage = new PipelineModificationMessage();
  }

  /**
   * Determines whether the current pipeline is valid
   *
   * @return PipelineValidationHandler
   * @throws InvalidConnectionException if the connection is not considered valid
   */
  public PipelineVerificationHandler validateConnection() throws InvalidConnectionException {
    new ConnectionValidator(pipeline, invocationGraphs, rootPipelineElement).validateConnection();
    return this;
  }

  /**
   * computes mapping properties (based on input/output matching)
   *
   * @return PipelineValidationHandler
   */
  public PipelineVerificationHandler computeMappingProperties() {
    List<String> connectedTo = rootPipelineElement.getConnectedTo();
    String domId = rootPipelineElement.getDOM();

    List<SpDataStream> tempStreams = new ArrayList<>();

    for (int i = 0; i < connectedTo.size(); i++) {
      NamedStreamPipesEntity element = TreeUtils.findSEPAElement(rootPipelineElement
              .getConnectedTo().get(i), pipeline.getSepas(), pipeline
              .getStreams());

      SpDataStream incomingStream;

      if (element instanceof DataProcessorInvocation || element instanceof SpDataStream) {
        if (element instanceof DataProcessorInvocation) {
          DataProcessorInvocation ancestor = (DataProcessorInvocation) TreeUtils.findByDomId(
                  connectedTo.get(i), invocationGraphs);

          incomingStream = ancestor.getOutputStream();
        } else {
          incomingStream = (SpDataStream) element;
        }

        tempStreams.add(incomingStream);
        if (rootPipelineElement.getStreamRequirements().size() - 1 == i) {
          updateStaticProperties(tempStreams, rootPipelineElement.getStaticProperties());
          PipelineModification modification = new PipelineModification(
                  domId,
                  rootPipelineElement.getElementId(),
                  rootPipelineElement.getStaticProperties());
          modification.setInputStreams(tempStreams);
          updateOutputStrategy(tempStreams);
          if (rootPipelineElement instanceof DataProcessorInvocation) {
            modification.setOutputStrategies(((DataProcessorInvocation) rootPipelineElement).getOutputStrategies());
          }
          pipelineModificationMessage.addPipelineModification(modification);
        }
      }
    }
    return this;
  }

  private void updateStaticProperties(List<SpDataStream> inputStreams,
                                      List<StaticProperty> staticProperties) {
    staticProperties
      .stream()
      .filter(sp -> (sp instanceof CollectionStaticProperty
              || sp instanceof MappingProperty
              || sp instanceof StaticPropertyGroup
              || sp instanceof StaticPropertyAlternatives))
      .forEach(property -> updateStaticProperty(inputStreams, property));
  }

  private void updateStaticProperty(List<SpDataStream> inputStreams, StaticProperty property) {
    if (property instanceof MappingProperty) {
      MappingProperty mappingProperty = property.as(MappingProperty.class);
      AbstractRequirementsSelectorGenerator generator = RequirementsSelectorGeneratorFactory.getRequirementsSelector(mappingProperty, inputStreams, rootPipelineElement);
      mappingProperty.setMapsFromOptions(generator.generateSelectors());
    } else if (property instanceof StaticPropertyGroup) {
      updateStaticProperties(inputStreams, property.as(StaticPropertyGroup.class).getStaticProperties());
    } else if (property instanceof StaticPropertyAlternatives) {
      // TODO
    } else if (property instanceof CollectionStaticProperty) {
      CollectionStaticProperty collection = property.as(CollectionStaticProperty.class);
      if (hasMappingEntry(collection)) {
        updateStaticProperty(inputStreams, collection.getStaticPropertyTemplate());
      } else if (hasGroupEntry(collection)) {
        updateStaticProperties(inputStreams, collection.getStaticPropertyTemplate().as(StaticPropertyGroup.class).getStaticProperties());
      }
    }
  }

  private Boolean hasMappingEntry(CollectionStaticProperty property) {
    return property.getStaticPropertyTemplate() instanceof MappingProperty;
  }

  private Boolean hasGroupEntry(CollectionStaticProperty property) {
    return property.getStaticPropertyTemplate() instanceof StaticPropertyGroup;
  }

  private void updateOutputStrategy(List<SpDataStream> inputStreams) {

    if (rootPipelineElement instanceof DataProcessorInvocation) {
      ((DataProcessorInvocation) rootPipelineElement)
              .getOutputStrategies()
              .stream()
              .filter(strategy -> strategy instanceof CustomOutputStrategy)
              .forEach(strategy -> {
                CustomOutputStrategy outputStrategy = (CustomOutputStrategy) strategy;
                if (inputStreams.size() == 1 || (inputStreams.size() > 1 && !(outputStrategy
                        .isOutputRight()))) {
                  outputStrategy.setAvailablePropertyKeys(new PropertySelectorGenerator
                          (inputStreams.get(0).getEventSchema(), false).generateSelectors());
                } else {
                  outputStrategy.setAvailablePropertyKeys(new PropertySelectorGenerator
                          (inputStreams.get(0).getEventSchema(), inputStreams.get(1)
                                  .getEventSchema(), false)
                          .generateSelectors());
                }
              });
    }
  }

  public PipelineVerificationHandler storeConnection() {
    new ConnectionStorageHandler(pipeline, rootPipelineElement).storeConnection();
    return this;
  }

  public PipelineModificationMessage getPipelineModificationMessage() {
    return pipelineModificationMessage;
  }

  public List<InvocableStreamPipesEntity> makeInvocationGraphs() {
    if (onlyStreamAncestorsPresentInPipeline()) {
      return new ArrayList<>();
    } else {
      PipelineGraph pipelineGraph = new PipelineGraphBuilder(pipeline).buildGraph();
      return new InvocationGraphBuilder(pipelineGraph, null).buildGraphs();
    }
  }

  private boolean onlyStreamAncestorsPresentInPipeline() {
    return rootPipelineElement
            .getConnectedTo()
            .stream()
            .map(connectedTo -> TreeUtils.findSEPAElement(connectedTo, pipeline.getSepas(), pipeline.getStreams()))
            .allMatch(pe -> pe instanceof SpDataStream);
  }
}