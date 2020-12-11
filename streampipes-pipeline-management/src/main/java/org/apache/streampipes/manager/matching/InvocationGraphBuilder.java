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

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.SpProtocol;
import org.apache.streampipes.container.util.ConsulUtil;
import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphHelpers;
import org.apache.streampipes.manager.matching.output.OutputSchemaFactory;
import org.apache.streampipes.manager.matching.output.OutputSchemaGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.SpDataStreamRelay;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.monitoring.ElementStatusInfoSettings;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

public class InvocationGraphBuilder {

  private final PipelineGraph pipelineGraph;
  private final String pipelineId;
  private Integer uniquePeIndex = 0;
  private final List<InvocableStreamPipesEntity> graphs;

  public InvocationGraphBuilder(PipelineGraph pipelineGraph, String pipelineId) {
    this.graphs = new ArrayList<>();
    this.pipelineGraph = pipelineGraph;
    this.pipelineId = pipelineId;
  }

  public List<InvocableStreamPipesEntity> buildGraphs() {

    PipelineGraphHelpers
            .findStreams(pipelineGraph)
            .forEach(stream -> configure(stream, getConnections(stream)));

    return graphs;
  }

  private void configure(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {

    EventGrounding inputGrounding = new GroundingBuilder(source, targets).getEventGrounding();

    // set output stream event grounding for source data processors
    if (source instanceof InvocableStreamPipesEntity) {
      if (source instanceof DataProcessorInvocation && ((DataProcessorInvocation) source).isConfigured()) {

        DataProcessorInvocation sourceInvocation = (DataProcessorInvocation) source;

        Tuple2<EventSchema, ? extends OutputStrategy> outputSettings = getOutputSettings(sourceInvocation);
        sourceInvocation.setOutputStrategies(Collections.singletonList(outputSettings.b));
        sourceInvocation.setOutputStream(makeOutputStream(inputGrounding, outputSettings));
      }
      if (!graphExists(source.getDOM())) {
        graphs.add((InvocableStreamPipesEntity) source);
      }
    }

    // set input stream event grounding for target element data processors and sinks
    targets.forEach(t -> {
      // check if source and target share same node
      if (source instanceof InvocableStreamPipesEntity) {
        if (((InvocableStreamPipesEntity) source).getDeploymentTargetNodeId() != null ||
                t.getDeploymentTargetNodeId() != null) {

          if (matchingDeploymentTarget((InvocableStreamPipesEntity) source, t)) {
            // both PE on same node - share grounding
            t.getInputStreams()
                    .get(getIndex(source.getDOM(), t))
                    .setEventGrounding(inputGrounding);

          } else {
            // check if target runs on cloud or edge node
            if (t.getDeploymentTargetNodeId().equals("default")) {
              // target runs on cloud node: use central cloud broker, e.g. kafka
              // TODO: set event relay to true
              // TODO: add cloud broker to List<EventRelays>
              if (source instanceof DataProcessorInvocation) {

                String relayTopic = inputGrounding.getTransportProtocol().getTopicDefinition().getActualTopicName();

                if (relayNotExists(relayTopic, source)) {
                  // TODO: use prioritized cloud transport protocol instead of kafka
                  SpProtocol prioritizedProtocol =
                          BackendConfig.INSTANCE.getMessagingSettings().getPrioritizedProtocols().get(0);

                  EventGrounding relayEventGrounding = new EventGrounding();

                  if (isPrioritized(prioritizedProtocol, JmsTransportProtocol.class)) {
                    JmsTransportProtocol tp = new JmsTransportProtocol(
                            BackendConfig.INSTANCE.getJmsHost(),
                            BackendConfig.INSTANCE.getJmsPort(),
                            relayTopic);
                    relayEventGrounding.setTransportProtocol(tp);
                  }
                  else if (isPrioritized(prioritizedProtocol, KafkaTransportProtocol.class)) {
                    KafkaTransportProtocol tp = new KafkaTransportProtocol(
                            BackendConfig.INSTANCE.getKafkaHost(),
                            BackendConfig.INSTANCE.getKafkaPort(),
                            relayTopic,
                            BackendConfig.INSTANCE.getZookeeperHost(),
                            BackendConfig.INSTANCE.getZookeeperPort());
                    relayEventGrounding.setTransportProtocol(tp);
                  }
                  else if (isPrioritized(prioritizedProtocol, MqttTransportProtocol.class)){
                    MqttTransportProtocol tp = new MqttTransportProtocol(
                            BackendConfig.INSTANCE.getMqttHost(),
                            BackendConfig.INSTANCE.getMqttPort(),
                            relayTopic);
                    relayEventGrounding.setTransportProtocol(tp);
                  }

                  relayEventGrounding.setTransportFormats(inputGrounding.getTransportFormats());

                  // TODO: when modifying pipelines new relay are added to old ones. Should initialize new ArrayList
                  ((DataProcessorInvocation) source)
                          .addOutputStreamRelay(new SpDataStreamRelay(relayEventGrounding));
                }
              }

              t.getInputStreams()
                      .get(getIndex(source.getDOM(), t))
                      .getEventGrounding()
                      .getTransportProtocol()
                      .setTopicDefinition(inputGrounding.getTransportProtocol().getTopicDefinition());

            } else {
              // target runs on edge node: use target edge node broker
              // TODO: set event relay to true
              // TODO: add target edge node broker to List<EventRelays>

              String relayTopic = inputGrounding.getTransportProtocol().getTopicDefinition().getActualTopicName();

              if (relayNotExists(relayTopic, source)) {

                EventGrounding relayEventGrounding = new EventGrounding();

                relayEventGrounding.setTransportProtocol(
                        new MqttTransportProtocol(
                                getTargetNodeBrokerHost(t),
                                getTargetNodeBrokerPort(t),
                                relayTopic
                        ));

                relayEventGrounding.setTransportFormats(inputGrounding.getTransportFormats());

                // TODO: when modifying pipelines new relay are added to old ones. Should initialize new ArrayList
                ((DataProcessorInvocation) source)
                        .addOutputStreamRelay(new SpDataStreamRelay(relayEventGrounding));

                t.getInputStreams()
                        .get(getIndex(source.getDOM(), t))
                        .setEventGrounding(relayEventGrounding);

              } else {
                t.getInputStreams()
                        .get(getIndex(source.getDOM(), t))
                        .setEventGrounding(((DataProcessorInvocation) source)
                                .getOutputStreamRelays()
                                .get(getIndex(source.getDOM(), t))
                                .getEventGrounding());
              }
            }
          }
        } else {
            t.getInputStreams()
                    .get(getIndex(source.getDOM(), t))
                    .setEventGrounding(inputGrounding);
        }
      } else {

        // TODO: Handle following edge situation:
        //  data stream -> invocable (processor, sink) in edge deployments that do not reside on same node
        // idea: trigger corresponding node controller to relay topic to adjecent broker (either node broker or
        // global cloud broker)
        t.getInputStreams()
                .get(getIndex(source.getDOM(), t))
                .setEventGrounding(inputGrounding);
      }

      // old
//      t.getInputStreams()
//              .get(getIndex(source.getDOM(), t))
//              .setEventGrounding(inputGrounding);

      t.getInputStreams()
              .get(getIndex(source.getDOM(), t))
              .setEventSchema(getInputSchema(source));

      String elementIdentifier = makeElementIdentifier(pipelineId, inputGrounding
              .getTransportProtocol().getTopicDefinition().getActualTopicName(), t.getName());

      t.setElementId(t.getBelongsTo() + "/" + elementIdentifier);
      t.setDeploymentRunningInstanceId(elementIdentifier);
      t.setCorrespondingPipeline(pipelineId);
      t.setStatusInfoSettings(makeStatusInfoSettings(elementIdentifier));

      uniquePeIndex++;

      configure(t, getConnections(t));

    });
  }

  private boolean relayNotExists(String relayTopic, NamedStreamPipesEntity source) {
    return ((DataProcessorInvocation) source)
            .getOutputStreamRelays()
            .stream()
            .noneMatch(r -> r.getEventGrounding()
                    .getTransportProtocol()
                    .getTopicDefinition()
                    .getActualTopicName().equals(relayTopic));
  }

  private Tuple2<EventSchema,? extends OutputStrategy> getOutputSettings(DataProcessorInvocation dataProcessorInvocation) {
    Tuple2<EventSchema,? extends OutputStrategy> outputSettings;
    OutputSchemaGenerator<?> schemaGenerator = new OutputSchemaFactory(dataProcessorInvocation)
            .getOuputSchemaGenerator();

    if (dataProcessorInvocation.getInputStreams().size() == 1) {
      outputSettings = schemaGenerator
              .buildFromOneStream(dataProcessorInvocation
                      .getInputStreams()
                      .get(0));
    } else if (graphExists(dataProcessorInvocation.getDOM())) {
      DataProcessorInvocation existingInvocation = (DataProcessorInvocation) find(dataProcessorInvocation.getDOM());
      outputSettings = schemaGenerator
              .buildFromTwoStreams(
                      existingInvocation.getInputStreams().get(0),
                      dataProcessorInvocation.getInputStreams().get(1));
      graphs.remove(existingInvocation);
    } else {
      outputSettings = new Tuple2<>(new EventSchema(), dataProcessorInvocation.getOutputStrategies().get(0));
    }
    return outputSettings;
  }

  private SpDataStream makeOutputStream(EventGrounding inputGrounding,
                                        Tuple2<EventSchema,? extends OutputStrategy> outputSettings) {
    SpDataStream outputStream = new SpDataStream();
    outputStream.setEventGrounding(inputGrounding);
    outputStream.setEventSchema(outputSettings.a);
    return outputStream;
  }

  private String getTargetNodeBrokerHost(InvocableStreamPipesEntity t) {
    // TODO: no hardcoded route - only for testing
    return ConsulUtil.getValueForRoute(
            "sp/v1/node/org.apache.streampipes.node.controller/"
                    + t.getDeploymentTargetNodeHostname()
                    + "/config/SP_NODE_BROKER_HOST", String.class);
  }

  private int getTargetNodeBrokerPort(InvocableStreamPipesEntity t) {
    // TODO: no hardcoded route - only for testing
    return ConsulUtil.getValueForRoute(
            "sp/v1/node/org.apache.streampipes.node.controller/"
                    + t.getDeploymentTargetNodeHostname()
                    + "/config/SP_NODE_BROKER_PORT", Integer.class);
  }


  private boolean matchingDeploymentTarget(InvocableStreamPipesEntity source, InvocableStreamPipesEntity target) {
    if (source instanceof DataProcessorInvocation && target instanceof DataProcessorInvocation) {
      if (source.getDeploymentTargetNodeId().equals(target.getDeploymentTargetNodeId())) {
        System.out.println("same node - no relay");
        return true;
      }
      return false;
    }
    return false;
  }

  private ElementStatusInfoSettings makeStatusInfoSettings(String elementIdentifier) {
    ElementStatusInfoSettings statusSettings = new ElementStatusInfoSettings();
    statusSettings.setKafkaHost(BackendConfig.INSTANCE.getKafkaHost());
    statusSettings.setKafkaPort(BackendConfig.INSTANCE.getKafkaPort());
    statusSettings.setErrorTopic(elementIdentifier + ".error");
    statusSettings.setStatsTopic(elementIdentifier + ".stats");
    statusSettings.setElementIdentifier(elementIdentifier);

    return statusSettings;
  }

  private String makeElementIdentifier(String pipelineId, String topic, String elementName) {
    return pipelineId
            + "-"
            + topic
            + "-"
            + elementName.replaceAll(" ", "").toLowerCase()
            + "-"
            + uniquePeIndex;
  }

  private EventSchema getInputSchema(NamedStreamPipesEntity source) {
    if (source instanceof SpDataStream) {
      return ((SpDataStream) source).getEventSchema();
    } else if (source instanceof DataProcessorInvocation) {
      return ((DataProcessorInvocation) source)
              .getOutputStream()
              .getEventSchema();
    } else {
      throw new IllegalArgumentException();
    }
  }

  private Set<InvocableStreamPipesEntity> getConnections(NamedStreamPipesEntity source) {
    return pipelineGraph.outgoingEdgesOf(source)
            .stream()
            .map(o -> pipelineGraph.getEdgeTarget(o))
            .map(g -> (InvocableStreamPipesEntity) g)
            .collect(Collectors.toSet());
  }

  private Integer getIndex(String sourceDomId, InvocableStreamPipesEntity targetElement) {
    return targetElement.getConnectedTo().indexOf(sourceDomId);
  }

  private boolean graphExists(String domId) {
    return graphs
            .stream()
            .anyMatch(g -> g.getDOM().equals(domId));
  }

  private InvocableStreamPipesEntity find(String domId) {
    return graphs
            .stream()
            .filter(g -> g.getDOM().equals(domId))
            .findFirst()
            .get();
  }

  public static Boolean isPrioritized(SpProtocol prioritizedProtocol,
                                      Class<?> protocolClass) {
    return prioritizedProtocol.getProtocolClass().equals(protocolClass.getCanonicalName());
  }
}