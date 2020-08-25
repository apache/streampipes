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
import org.apache.streampipes.manager.matching.v2.ElementVerification;
import org.apache.streampipes.manager.matching.v2.mapping.MappingPropertyCalculator;
import org.apache.streampipes.manager.selector.PropertyRequirementSelector;
import org.apache.streampipes.manager.selector.PropertySelectorGenerator;
import org.apache.streampipes.manager.util.PipelineVerificationUtils;
import org.apache.streampipes.manager.util.TreeUtils;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.connection.Connection;
import org.apache.streampipes.model.client.exception.InvalidConnectionException;
import org.apache.streampipes.model.constants.PropertySelectorConstants;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineModification;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingProperty;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.ArrayList;
import java.util.List;

public class PipelineVerificationHandler {

  private Pipeline pipeline;
  private PipelineModificationMessage pipelineModificationMessage;
  private List<InvocableStreamPipesEntity> invocationGraphs;
  private InvocableStreamPipesEntity rootPipelineElement;

  public PipelineVerificationHandler(Pipeline pipeline) throws NoSepaInPipelineException {
    this.pipeline = pipeline;
    this.rootPipelineElement = PipelineVerificationUtils.getRootNode(pipeline);
    this.invocationGraphs = new ArrayList<>();
    this.pipelineModificationMessage = new PipelineModificationMessage();
  }

  public PipelineVerificationHandler validateConnection() throws InvalidConnectionException {

    ElementVerification verifier = new ElementVerification();
    boolean verified = true;
    InvocableStreamPipesEntity rightElement = rootPipelineElement;
    List<String> connectedTo = rootPipelineElement.getConnectedTo();

    for (String domId : connectedTo) {
      NamedStreamPipesEntity element = TreeUtils.findSEPAElement(domId, pipeline.getSepas(), pipeline.getStreams());
      if (element instanceof SpDataStream) {
        SpDataStream leftSpDataStream = (SpDataStream) element;

        if (!(verifier.verify(leftSpDataStream, rightElement))) {
          verified = false;
        }
      } else {
        invocationGraphs.addAll(makeInvocationGraphs());
        DataProcessorInvocation ancestor = findInvocationGraph(invocationGraphs, element.getDOM());
        if (!(verifier.verify(ancestor, rightElement))) {
          verified = false;
        }
      }
    }

    if (!verified) {
      throw new InvalidConnectionException(verifier.getErrorLog());
    }
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
          updateStaticProperties(tempStreams);
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

  private void updateStaticProperties(List<SpDataStream> inputStreams) {

    rootPipelineElement
            .getStaticProperties()
            .stream()
            .filter(property -> (property instanceof MappingProperty
                    || ((property instanceof CollectionStaticProperty) && ((CollectionStaticProperty) property).getStaticPropertyTemplate() instanceof MappingProperty)))
            .forEach(property -> {
              MappingProperty mappingProperty;

              if (property instanceof MappingProperty) {
                mappingProperty = (MappingProperty) property;
              } else {
                mappingProperty = (MappingProperty) ((CollectionStaticProperty) property).getStaticPropertyTemplate();
              }

              if (!mappingProperty.getRequirementSelector().equals("")) {
                mappingProperty.setMapsFromOptions(generateSelectorsFromRequirement
                        (inputStreams, mappingProperty.getRequirementSelector()));
              } else {
                mappingProperty.setMapsFromOptions(generateSelectorsWithoutRequirement
                        (inputStreams));
              }
            });
  }

  private List<String> generateSelectorsFromRequirement(List<SpDataStream> inputStreams, String
          requirementSelector) {
    PropertyRequirementSelector selector = new PropertyRequirementSelector
            (requirementSelector);

    EventProperty propertyRequirement = selector.findPropertyRequirement
            (rootPipelineElement.getStreamRequirements());
    SpDataStream inputStream = selector.getAffectedStream(inputStreams);

    List<String> availablePropertySelectors = new PropertySelectorGenerator(inputStream
            .getEventSchema(), true).generateSelectors(selector.getAffectedStreamPrefix());

    return new MappingPropertyCalculator(inputStream.getEventSchema(),
            availablePropertySelectors, propertyRequirement).matchedPropertySelectors();
  }

  private List<String> generateSelectorsWithoutRequirement(List<SpDataStream> inputStreams) {

    List<String> selectors = new ArrayList<>(new PropertySelectorGenerator(inputStreams
            .get(0).getEventSchema().getEventProperties(), true)
            .generateSelectors(PropertySelectorConstants.FIRST_STREAM_ID_PREFIX));

    if (inputStreams.size() > 1) {
      selectors.addAll(new PropertySelectorGenerator(inputStreams
              .get(1).getEventSchema().getEventProperties(), true)
              .generateSelectors(PropertySelectorConstants.SECOND_STREAM_ID_PREFIX));
    }

    return selectors;
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
    String fromId = rootPipelineElement.getConnectedTo().get(rootPipelineElement.getConnectedTo().size() - 1);
    NamedStreamPipesEntity sepaElement = TreeUtils.findSEPAElement(fromId, pipeline.getSepas(), pipeline.getStreams());
    String sourceId;
    if (sepaElement instanceof SpDataStream) {
      sourceId = sepaElement.getElementId();
    } else {
      sourceId = ((InvocableStreamPipesEntity) sepaElement).getBelongsTo();
    }
    Connection connection = new Connection(sourceId, rootPipelineElement.getBelongsTo());
    StorageDispatcher.INSTANCE.getNoSqlStore().getConnectionStorageApi().addConnection(connection);
    return this;
  }

  public PipelineModificationMessage getPipelineModificationMessage() {
    return pipelineModificationMessage;
  }

  public List<InvocableStreamPipesEntity> makeInvocationGraphs() {
    PipelineGraph pipelineGraph = new PipelineGraphBuilder(pipeline).buildGraph();
    return new InvocationGraphBuilder(pipelineGraph, null).buildGraphs();
  }

  private DataProcessorInvocation findInvocationGraph(List<InvocableStreamPipesEntity> graphs, String domId) {
    return (DataProcessorInvocation) TreeUtils.findByDomId(domId, graphs);
  }
}