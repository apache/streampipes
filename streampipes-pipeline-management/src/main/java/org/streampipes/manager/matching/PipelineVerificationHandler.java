/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.matching;

import org.streampipes.commons.exceptions.NoSepaInPipelineException;
import org.streampipes.manager.data.PipelineGraph;
import org.streampipes.manager.data.PipelineGraphBuilder;
import org.streampipes.manager.matching.v2.ElementVerification;
import org.streampipes.manager.matching.v2.mapping.MappingPropertyCalculator;
import org.streampipes.manager.selector.PropertySelectorGenerator;
import org.streampipes.manager.util.PipelineVerificationUtils;
import org.streampipes.manager.util.TreeUtils;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.client.connection.Connection;
import org.streampipes.model.client.exception.InvalidConnectionException;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.client.pipeline.PipelineModification;
import org.streampipes.model.client.pipeline.PipelineModificationMessage;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.CustomOutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.staticproperty.MappingProperty;
import org.streampipes.storage.management.StorageDispatcher;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PipelineVerificationHandler {

  private Pipeline pipeline;
  private PipelineModificationMessage pipelineModificationMessage;
  private List<InvocableStreamPipesEntity> invocationGraphs;
  private InvocableStreamPipesEntity rdfRootElement;

  public PipelineVerificationHandler(Pipeline pipeline) throws NoSepaInPipelineException {

    this.pipeline = pipeline;
    this.rdfRootElement = PipelineVerificationUtils.getRootNode(pipeline);
    this.invocationGraphs = new ArrayList<>();

    pipelineModificationMessage = new PipelineModificationMessage();

  }

  public PipelineVerificationHandler validateConnection() throws InvalidConnectionException {

    ElementVerification verifier = new ElementVerification();
    boolean verified = true;
    InvocableStreamPipesEntity rightElement = rdfRootElement;
    List<String> connectedTo = rdfRootElement.getConnectedTo();

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
   * computes mapping properties (based on EXACT input/output
   * matching)
   *
   * @return PipelineValidationHandler
   */

  public PipelineVerificationHandler computeMappingProperties() {
    List<String> connectedTo = rdfRootElement.getConnectedTo();
    String domId = rdfRootElement.getDOM();

    List<SpDataStream> tempStreams = new ArrayList<>();

    for (int i = 0; i < connectedTo.size(); i++) {
      NamedStreamPipesEntity element = TreeUtils.findSEPAElement(rdfRootElement
              .getConnectedTo().get(i), pipeline.getSepas(), pipeline
              .getStreams());

      SpDataStream incomingStream;

      if (element instanceof DataProcessorInvocation || element instanceof SpDataStream) {

        if (element instanceof DataProcessorInvocation) {

          DataProcessorInvocation ancestor = (DataProcessorInvocation) TreeUtils.findByDomId(
                  connectedTo.get(i), invocationGraphs);

          incomingStream = ancestor.getOutputStream();
          updateStaticProperties(ancestor.getOutputStream(), i);
          //updateOutputStrategy(ancestor.getOutputStream(), i);

        } else {

          SpDataStream stream = (SpDataStream) element;
          incomingStream = stream;
          updateStaticProperties(stream, i);
          //updateOutputStrategy(stream, i);

        }

        tempStreams.add(incomingStream);
        if (rdfRootElement.getStreamRequirements().size() - 1 == i) {
          PipelineModification modification = new PipelineModification(
                  domId,
                  rdfRootElement.getElementId(),
                  rdfRootElement.getStaticProperties());
          modification.setInputStreams(tempStreams);
          updateOutputStrategy(tempStreams);
          if (rdfRootElement instanceof DataProcessorInvocation) {
            modification.setOutputStrategies(((DataProcessorInvocation) rdfRootElement).getOutputStrategies());
          }
          pipelineModificationMessage
                  .addPipelineModification(modification);
        }
      }
    }
    return this;
  }

  public void updateStaticProperties(SpDataStream stream, Integer count) {

    rdfRootElement
            .getStaticProperties()
            .stream()
            .filter(property -> property instanceof MappingProperty)
            .forEach(property -> {
              try {

                MappingProperty mappingProperty = (MappingProperty) property;
                if (mappingProperty.getMapsFrom() != null) {
                  if (inStream(rdfRootElement.getStreamRequirements().get(count), mappingProperty.getMapsFrom())) {
                    mappingProperty
                            .setMapsFromOptions(findSupportedEventProperties(stream,
                                    rdfRootElement.getStreamRequirements(),
                                    mappingProperty.getMapsFrom()));
                  }
                } else {
                  mappingProperty.setMapsFromOptions(new ArrayList<>());
                  for (EventProperty streamProperty : stream
                          .getEventSchema().getEventProperties()) {

                    if ((streamProperty instanceof EventPropertyPrimitive) || streamProperty instanceof EventPropertyList) {
                      mappingProperty.getMapsFromOptions().add(streamProperty);
                    } else {
                      mappingProperty.getMapsFromOptions().addAll(addNestedProperties((EventPropertyNested) streamProperty));
                    }
                  }
                }

              } catch (Exception e) {
                e.printStackTrace();
              }
            });
  }


  private boolean inStream(SpDataStream stream, URI mapsFrom) {
    return stream
            .getEventSchema()
            .getEventProperties()
            .stream().anyMatch(ep -> ep.getElementId().equals(mapsFrom.toString()));
  }

  private List<EventProperty> findSupportedEventProperties(SpDataStream streamOffer, List<SpDataStream> streamRequirements, URI mapsFrom) {
    EventProperty mapsFromProperty = findPropertyRequirement(mapsFrom);

    return new MappingPropertyCalculator().matchesProperties(streamOffer.getEventSchema().getEventProperties(), mapsFromProperty);
  }

  private EventProperty findPropertyRequirement(URI mapsFrom) {
    return TreeUtils
            .findEventProperty(mapsFrom.toString(), rdfRootElement.getStreamRequirements());
  }

  private void updateOutputStrategy(List<SpDataStream> inputStreams) {

    if (rdfRootElement instanceof DataProcessorInvocation) {
      ((DataProcessorInvocation) rdfRootElement)
              .getOutputStrategies()
              .stream()
              .filter(strategy -> strategy instanceof CustomOutputStrategy)
              .forEach(strategy -> {
                CustomOutputStrategy outputStrategy = (CustomOutputStrategy) strategy;
                if (inputStreams.size() == 1 || (inputStreams.size() > 1 && !(outputStrategy
                        .isOutputRight()))) {
                  outputStrategy.setAvailablePropertyKeys(new PropertySelectorGenerator
                          (inputStreams.get(0).getEventSchema()).generateSelectors());
                } else {
                  outputStrategy.setAvailablePropertyKeys(new PropertySelectorGenerator
                          (inputStreams.get(0).getEventSchema(), inputStreams.get(1).getEventSchema())
                          .generateSelectors());
                }
              });
    }
  }

  private List<EventProperty> addNestedProperties(EventPropertyNested properties) {
    List<EventProperty> options = new ArrayList<>();
    for (EventProperty p : properties.getEventProperties()) {
      if (p instanceof EventPropertyPrimitive) {
        options.add(p);
      } else {
        options.addAll(addNestedProperties(properties));
      }
    }
    return options;
  }

  public PipelineVerificationHandler storeConnection() {
    String fromId = rdfRootElement.getConnectedTo().get(rdfRootElement.getConnectedTo().size() - 1);
    NamedStreamPipesEntity sepaElement = TreeUtils.findSEPAElement(fromId, pipeline.getSepas(), pipeline.getStreams());
    String sourceId;
    if (sepaElement instanceof SpDataStream) {
      sourceId = sepaElement.getElementId();
    } else {
      sourceId = ((InvocableStreamPipesEntity) sepaElement).getBelongsTo();
    }
    Connection connection = new Connection(sourceId, rdfRootElement.getBelongsTo());
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