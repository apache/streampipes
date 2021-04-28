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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.SpEdgeNodeProtocol;
import org.apache.streampipes.config.backend.SpProtocol;
import org.apache.streampipes.container.util.ConsulUtil;
import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphHelpers;
import org.apache.streampipes.manager.matching.output.OutputSchemaFactory;
import org.apache.streampipes.manager.matching.output.OutputSchemaGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelay;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.monitoring.ElementStatusInfoSettings;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.storage.api.INodeInfoStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class InvocationGraphBuilder {

  private static final String CONSUL_NODE_CONTROLLER_ROUTE = "sp/v1/node/org.apache.streampipes.node.controller";
  private static final String NODE_BROKER_CONTAINER_HOST = "SP_NODE_BROKER_CONTAINER_HOST";
  private static final String NODE_BROKER_CONTAINER_PORT = "SP_NODE_BROKER_CONTAINER_PORT";
  private static final String SLASH ="/";
  private static final String DEFAULT_TAG = "default";
  private final PipelineGraph pipelineGraph;
  private final String pipelineId;
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
      if (source instanceof InvocableStreamPipesEntity && deploymentTargetNotNull(source, t)) {

          if (matchingDeploymentTargets(source, t)) {
            // both processor on same node - share grounding
            t.getInputStreams()
                    .get(getIndex(source.getDOM(), t))
                    .setEventGrounding(inputGrounding);

          }
          else if (defaultDeploymentTarget(t) && source instanceof DataProcessorInvocation) {
            // target runs on cloud node: use central cloud broker, e.g. kafka
            connectSourceToTarget((DataProcessorInvocation) source, t, inputGrounding, false);
          }
          else {
            // target runs on other edge node: use target edge node broker
            connectSourceToTarget((DataProcessorInvocation) source, t, inputGrounding, true);
          }

        } else {

          if (matchingDeploymentTargets(source, t)) {
            t.getInputStreams()
                    .get(getIndex(source.getDOM(), t))
                    .setEventGrounding(inputGrounding);

          } else if (defaultDeploymentTarget(t)) {
            // target runs on cloud node: use central cloud broker, e.g. kafka
            EventGrounding eg = generateRelayGrounding(inputGrounding,false);
            // use unique topic for target in case we have multiple source stream relays to the target
            String oldTopic = extractTopic(eg);
            eg.getTransportProtocol().getTopicDefinition().setActualTopicName(oldTopic + "."
                    + this.pipelineId);
            t.getInputStreams()
                    .get(getIndex(source.getDOM(),t))
                    .setEventGrounding(eg);
          } else if (targetInvocableOnEdgeNode(t)) {
            // case 2: target on other edge node -> relay + target node broker
            // use unique topic for target in case we have multiple source stream relays to the target
            EventGrounding eg = generateRelayGrounding(inputGrounding,t,true);
            String oldTopic = extractTopic(eg);
            eg.getTransportProtocol().getTopicDefinition().setActualTopicName(oldTopic + "."
                    + this.pipelineId);
            t.getInputStreams()
                    .get(getIndex(source.getDOM(), t))
                    .setEventGrounding(eg);
          } else {
            // default case while modelling. no deployment target known
            t.getInputStreams()
                    .get(getIndex(source.getDOM(), t))
                    .setEventGrounding(inputGrounding);
          }
      }

      t.getInputStreams()
              .get(getIndex(source.getDOM(), t))
              .setEventSchema(getInputSchema(source));

      String elementIdentifier = makeElementIdentifier(pipelineId,
              inputGrounding.getTransportProtocol().getTopicDefinition().getActualTopicName(), t.getName(), t.getDOM());

      t.setElementId(t.getBelongsTo() + "/" + elementIdentifier);
      t.setDeploymentRunningInstanceId(elementIdentifier);
      t.setCorrespondingPipeline(pipelineId);
      t.setStatusInfoSettings(makeStatusInfoSettings(elementIdentifier));
      configure(t, getConnections(t));

    });
  }

  private void removeExistingStreamRelay(NamedStreamPipesEntity source, InvocableStreamPipesEntity t) {
    ((DataProcessorInvocation) source).removeOutputStreamRelay(
            ((DataProcessorInvocation) source)
                    .getOutputStreamRelays()
                    .get(getRelayIndex((DataProcessorInvocation) source, t)));
  }

  private boolean targetInvocableOnEdgeNode(InvocableStreamPipesEntity t) {
    return t.getDeploymentTargetNodeId() != null && !t.getDeploymentTargetNodeId().equals(DEFAULT_TAG);
  }

  private boolean defaultDeploymentTarget(InvocableStreamPipesEntity t) {
    return t.getDeploymentTargetNodeId() != null && t.getDeploymentTargetNodeId().equals(DEFAULT_TAG);
  }

  private void connectSourceToTarget(DataProcessorInvocation source, InvocableStreamPipesEntity target,
                                     EventGrounding inputGrounding, boolean edgeToEdgeRelay){
    //connect a source entity with a target entity through an appropriate relay
    EventGrounding relayGrounding = edgeToEdgeRelay ?
            generateRelayGrounding(inputGrounding, target, true) : generateRelayGrounding(inputGrounding, false);
    if (eventRelayExists(source, target)) {
      removeExistingStreamRelay(source, target);
    }
    modifyTargetInputStream(source, target, relayGrounding, edgeToEdgeRelay);
    addOutputStreamRelayIfNoDuplicate(source, relayGrounding);
  }

  private void addOutputStreamRelayIfNoDuplicate(DataProcessorInvocation source, EventGrounding relayGrounding) {
    boolean isDuplicate = source.getOutputStreamRelays().stream()
            .anyMatch(r -> r.getEventGrounding().getElementId().equals(relayGrounding.getElementId()));

    if (!isDuplicate) {
      source.addOutputStreamRelay(new SpDataStreamRelay(relayGrounding));
    }
  }

  private void modifyTargetInputStream(NamedStreamPipesEntity s, InvocableStreamPipesEntity t,
                                               EventGrounding grounding, boolean edgeToEdgeRelay) {
    //modify the grounding of the InputStream of the target entity so that it matches the relay grounding
    SpDataStream inputStream = t.getInputStreams()
            .get(getIndex(s.getDOM(),t));
    if(edgeToEdgeRelay){
      inputStream.setEventGrounding(grounding);
    }else{
      inputStream.getEventGrounding()
              .getTransportProtocol()
              .getTopicDefinition()
              .setActualTopicName(extractTopic(grounding));
      inputStream.getEventGrounding().setElementId(grounding.getElementId());
    }
  }

  private boolean deploymentTargetNotNull(NamedStreamPipesEntity s, InvocableStreamPipesEntity t) {
    if (s instanceof SpDataStream) {
      return ((SpDataStream) s).getDeploymentTargetNodeId() != null && t.getDeploymentTargetNodeId() != null;
    } else {
      return ((InvocableStreamPipesEntity) s).getDeploymentTargetNodeId() != null &&
              t.getDeploymentTargetNodeId() != null;
    }
  }

  private String extractTopic(EventGrounding eg) {
    return eg.getTransportProtocol().getTopicDefinition().getActualTopicName();
  }

  private boolean eventRelayExists(NamedStreamPipesEntity s, InvocableStreamPipesEntity t) {
    SpDataStream targetInput = t.getInputStreams().get(getIndex(s.getDOM(), t));
    return ((DataProcessorInvocation)s).getOutputStreamRelays()
            .stream()
            .anyMatch(i -> i.getEventGrounding().getElementId().equals(
                    targetInput.getEventGrounding().getElementId()));
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
    Optional<NodeInfoDescription> targetNode = getNodeStorageApi().getNode(t.getDeploymentTargetNodeId());
    if (targetNode.isPresent()) {
      return targetNode.get().getHostname();
    }
    throw new SpRuntimeException("Deployment target node not found");
  }

  private int getTargetNodeBrokerPort(InvocableStreamPipesEntity t) {
    Optional<NodeInfoDescription> targetNode = getNodeStorageApi().getNode(t.getDeploymentTargetNodeId());
    if (targetNode.isPresent()) {
      return targetNode.get().getPort();
    }
    throw new SpRuntimeException("Deployment target node not found");
  }

  private boolean matchingDeploymentTargets(NamedStreamPipesEntity s, InvocableStreamPipesEntity t) {
    if (s instanceof DataProcessorInvocation &&
            (t instanceof DataProcessorInvocation || t instanceof DataSinkInvocation) && deploymentTargetNotNull(s,t)) {
        return ((DataProcessorInvocation) s)
                .getDeploymentTargetNodeId()
                .equals(t.getDeploymentTargetNodeId());
    } else if (s instanceof SpDataStream &&
            (t instanceof DataProcessorInvocation || t instanceof DataSinkInvocation) && deploymentTargetNotNull(s,t)) {
        return ((SpDataStream) s)
                .getDeploymentTargetNodeId()
                .equals(t.getDeploymentTargetNodeId());
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

  private String makeElementIdentifier(String pipelineId, String topic, String elementName, String dom) {
    return pipelineId
            + "-"
            + topic
            + "-"
            + elementName.replaceAll(" ", "").toLowerCase()
            + "-"
            + dom.split("_")[dom.split("_").length-1];
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

  private Integer getRelayIndex(DataProcessorInvocation source, InvocableStreamPipesEntity target) throws SpRuntimeException {
    Optional<SpDataStreamRelay> relay = source.getOutputStreamRelays().stream().filter(r ->
            target.getInputStreams().stream().map(s ->
                    s.getEventGrounding()
                            .getElementId())
                    .collect(Collectors.toList())
                    .contains(r.getEventGrounding()
                            .getElementId())).findFirst();
    if(relay.isPresent()){
      return source.getOutputStreamRelays().indexOf(relay.get());
    }else {
      throw new SpRuntimeException("Index for relay not available");
    }
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

  private EventGrounding generateRelayGrounding(EventGrounding sourceInvocableOutputGrounding,
                                                boolean edgeToEdgeRelay) {
    return generateRelayGrounding(sourceInvocableOutputGrounding, null, edgeToEdgeRelay);
  }

  private EventGrounding generateRelayGrounding(EventGrounding sourceInvocableOutputGrounding,
                                                InvocableStreamPipesEntity target, boolean edgeToEdgeRelay) {
    EventGrounding eg = new EventGrounding();
    String topic = extractTopic(sourceInvocableOutputGrounding);
    if (edgeToEdgeRelay) {
      eg.setTransportProtocol(getPrioritizedEdgeProtocol(
              BackendConfig.INSTANCE.getMessagingSettings().getPrioritizedEdgeProtocols().get(0), topic, target));
    } else {
      eg.setTransportProtocol(getPrioritizedGlobalProtocol(
              BackendConfig.INSTANCE.getMessagingSettings().getPrioritizedProtocols().get(0), topic));
    }
    eg.setTransportFormats(sourceInvocableOutputGrounding.getTransportFormats());
    eg.setElementId(sourceInvocableOutputGrounding.getElementId());
    return eg;
  }

  private TransportProtocol getPrioritizedGlobalProtocol(SpProtocol p, String topic) {
    if (matches(p, JmsTransportProtocol.class)) {
      return jmsTransportProtocol(topic);
    } else if (matches(p, KafkaTransportProtocol.class)) {
      return kafkaTransportProtocol(topic);
    } else if (matches(p, MqttTransportProtocol.class)){
      return mqttTransportProtocol(topic);
    }
    throw new SpRuntimeException("Could not retrieve prioritized transport protocol");
  }

  private TransportProtocol getPrioritizedEdgeProtocol(SpEdgeNodeProtocol p, String topic,
                                                       InvocableStreamPipesEntity target) {
    if (matches(p, MqttTransportProtocol.class)){
      return mqttTransportProtocol(topic, target);
    } else if (matches(p, KafkaTransportProtocol.class)){
      return kafkaTransportProtocol(topic, target);
    }
    throw new SpRuntimeException("Could not retrieve prioritized transport protocol");
  }

  private JmsTransportProtocol jmsTransportProtocol(String topic) {
    return new JmsTransportProtocol(
            BackendConfig.INSTANCE.getJmsHost(),
            BackendConfig.INSTANCE.getJmsPort(),
            topic);
  }

  private KafkaTransportProtocol kafkaTransportProtocol(String topic) {
    return kafkaTransportProtocol(topic, null);
  }

  private KafkaTransportProtocol kafkaTransportProtocol(String topic, InvocableStreamPipesEntity target) {
    if (target != null) {
      return new KafkaTransportProtocol(
              getTargetNodeBrokerHost(target),
              getTargetNodeBrokerPort(target),
              topic);
//              getTargetNodeZookeeperHost(target),
//              getTargetNodeZookeeperPort(target));
    }
    return new KafkaTransportProtocol(
            BackendConfig.INSTANCE.getKafkaHost(),
            BackendConfig.INSTANCE.getKafkaPort(),
            topic,
            BackendConfig.INSTANCE.getZookeeperHost(),
            BackendConfig.INSTANCE.getZookeeperPort());
  }

  private MqttTransportProtocol mqttTransportProtocol(String topic) {
    return mqttTransportProtocol(topic, null);
  }

  private MqttTransportProtocol mqttTransportProtocol(String topic, InvocableStreamPipesEntity target) {
    if (target != null) {
      return new MqttTransportProtocol(
              getTargetNodeBrokerHost(target),
              getTargetNodeBrokerPort(target),
              topic);
    }
    return new MqttTransportProtocol(
            BackendConfig.INSTANCE.getMqttHost(),
            BackendConfig.INSTANCE.getMqttPort(),
            topic);
  }

  private <T extends TransportProtocol> boolean matches(SpProtocol p, Class<T> clazz) {
    return p.getProtocolClass().equals(clazz.getCanonicalName());
  }

  private <T extends TransportProtocol> boolean matches(SpEdgeNodeProtocol p, Class<T> clazz) {
    return p.getProtocolClass().equals(clazz.getCanonicalName());
  }

  private static INodeInfoStorage getNodeStorageApi() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage();
  }
}