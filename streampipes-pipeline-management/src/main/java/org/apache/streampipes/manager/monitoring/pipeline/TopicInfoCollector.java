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
package org.apache.streampipes.manager.monitoring.pipeline;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.monitoring.ConsumedMessagesInfo;
import org.apache.streampipes.model.monitoring.PipelineElementMonitoringInfo;
import org.apache.streampipes.model.monitoring.ProducedMessagesInfo;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TopicInfoCollector {


  private final Pipeline pipeline;
  private final AdminClient kafkaAdminClient;

  private final Map<String, Long> latestTopicOffsets;
  private final Map<String, Long> topicOffsetAtPipelineStart;
  private final Map<String, Long> currentConsumerGroupOffsets;

  private final List<PipelineElementMonitoringInfo> monitoringInfo;

  public TopicInfoCollector(Pipeline pipeline) {
    this.pipeline = pipeline;
    this.kafkaAdminClient = kafkaAdminClient();
    this.latestTopicOffsets = new HashMap<>();
    this.topicOffsetAtPipelineStart = new HashMap<>();
    this.currentConsumerGroupOffsets = new HashMap<>();
    this.monitoringInfo = new ArrayList<>();
  }

  private void makeTopicInfo() {
    long currentTime = Instant.now().minus (1, ChronoUnit.SECONDS).toEpochMilli();
    List<TransportProtocol> affectedProtocols = new ArrayList<>();

    pipeline.getSepas().forEach(processor -> affectedProtocols.addAll(extractProtocols(processor)));
    pipeline.getActions().forEach(sink -> affectedProtocols.addAll(extractProtocols(sink)));

    affectedProtocols.forEach(protocol -> {
      if (protocol instanceof KafkaTransportProtocol) {
        try {
          Tuple2<String, Long> latestTopicOffsets = makeTopicOffsetInfo((KafkaTransportProtocol) protocol, OffsetSpec.forTimestamp(currentTime));
          Tuple2<String, Long> topicOffsetAtPipelineStart = makeTopicOffsetInfo((KafkaTransportProtocol) protocol, OffsetSpec.forTimestamp(pipeline.getStartedAt()));
          Tuple2<String, Long> currentConsumerGroupOffsets = makeTopicInfo((KafkaTransportProtocol) protocol);

          this.latestTopicOffsets.put(latestTopicOffsets.a, latestTopicOffsets.b);
          this.topicOffsetAtPipelineStart.put(topicOffsetAtPipelineStart.a, topicOffsetAtPipelineStart.b);
          this.currentConsumerGroupOffsets.put(currentConsumerGroupOffsets.a, currentConsumerGroupOffsets.b);

        } catch (ExecutionException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

  public List<PipelineElementMonitoringInfo> makeMonitoringInfo() {
    this.makeTopicInfo();
    // TODO filter for KafkaGrounding
    this.pipeline.getStreams().forEach(stream -> this.monitoringInfo.add(makeStreamMonitoringInfo(stream)));
    this.pipeline.getSepas().forEach(processor -> this.monitoringInfo.add(makeProcessorMonitoringInfo(processor)));
    this.pipeline.getActions().forEach(sink -> this.monitoringInfo.add(makeSinkMonitoringInfo(sink)));

    this.kafkaAdminClient.close();
    return this.monitoringInfo;
  }

  private PipelineElementMonitoringInfo makeStreamMonitoringInfo(SpDataStream stream) {
    PipelineElementMonitoringInfo info = prepare(stream.getElementId(), stream.getName(), false, true);
    KafkaTransportProtocol protocol = (KafkaTransportProtocol) stream.getEventGrounding().getTransportProtocol();
    info.setProducedMessagesInfo(makeOutputTopicInfoForPipelineElement(protocol));

    return info;
  }

  private PipelineElementMonitoringInfo makeProcessorMonitoringInfo(DataProcessorInvocation processor) {
    PipelineElementMonitoringInfo info = prepare(processor.getElementId(), processor.getName(), true, true);
    KafkaTransportProtocol outputProtocol = (KafkaTransportProtocol) processor.getOutputStream().getEventGrounding().getTransportProtocol();
    ProducedMessagesInfo outputTopicInfo = makeOutputTopicInfoForPipelineElement(outputProtocol);
    List<ConsumedMessagesInfo> inputTopicInfo = makeInputTopicInfoForPipelineElement(processor.getInputStreams());

    info.setProducedMessagesInfo(outputTopicInfo);
    info.setConsumedMessagesInfos(inputTopicInfo);
    printStatistics(info);
    return info;
  }

  private void printStatistics(PipelineElementMonitoringInfo info) {
    System.out.println("Pipeline Element: " + info.getPipelineElementName());
    info.getConsumedMessagesInfos().forEach(input -> {
      System.out.println("Consumed messages since pipeline start: " + input.getConsumedMessagesSincePipelineStart());
      System.out.println("Total messages since pipeline start: " + (input.getTotalMessagesSincePipelineStart()));
      System.out.println("Lag: " + input.getLag());
    });
    //System.out.println("Produced messages: " + (info.getOutputTopicInfo().getCurrentOffset() - info.getOutputTopicInfo().getOffsetAtPipelineStart()));
  }

  private PipelineElementMonitoringInfo makeSinkMonitoringInfo(DataSinkInvocation sink) {
    PipelineElementMonitoringInfo info = prepare(sink.getElementId(), sink.getName(), true, false);
    info.setConsumedMessagesInfos(makeInputTopicInfoForPipelineElement(sink.getInputStreams()));
    return info;
  }

  private List<ConsumedMessagesInfo> makeInputTopicInfoForPipelineElement(List<SpDataStream> inputStreams) {
    List<ConsumedMessagesInfo> infos = new ArrayList<>();
    inputStreams.stream().map(is -> is.getEventGrounding().getTransportProtocol()).forEach(protocol -> {
      String topic = getTopic((KafkaTransportProtocol) protocol);
      String groupId = ((KafkaTransportProtocol) protocol).getGroupId();
      ConsumedMessagesInfo info = new ConsumedMessagesInfo(topic, groupId);
      long consumedMessagesSincePipelineStart = (getCurrentConsumerGroupOffset(groupId) - topicOffsetAtPipelineStart.get(topic));
      long totalMessagesSincePipelineStart = (latestTopicOffsets.get(topic) - topicOffsetAtPipelineStart.get(topic));
      long lag = totalMessagesSincePipelineStart - consumedMessagesSincePipelineStart;

      info.setTotalMessagesSincePipelineStart(totalMessagesSincePipelineStart);
      info.setConsumedMessagesSincePipelineStart(consumedMessagesSincePipelineStart);
      info.setLag(lag);

      infos.add(info);
    });

    return infos;
  }

  private long getCurrentConsumerGroupOffset(String groupId) {
    return currentConsumerGroupOffsets.get(groupId);
  }

  private ProducedMessagesInfo makeOutputTopicInfoForPipelineElement(KafkaTransportProtocol protocol) {
    String topic = getTopic(protocol);
    ProducedMessagesInfo info = new ProducedMessagesInfo(topic);

    info.setTotalProducedMessages(this.latestTopicOffsets.get(topic));
    info.setTotalProducedMessagesSincePipelineStart(info.getTotalProducedMessages() - this.topicOffsetAtPipelineStart.get(topic));

    return info;
  }

  private String getTopic(KafkaTransportProtocol protocol) {
    return protocol.getTopicDefinition().getActualTopicName();
  }

  private PipelineElementMonitoringInfo prepare(String elementId, String name, boolean inputTopics, boolean outputTopics) {
    PipelineElementMonitoringInfo info = new PipelineElementMonitoringInfo();
    info.setPipelineElementName(name);
    info.setPipelineElementId(elementId);
    info.setConsumedMessageInfoExists(inputTopics);
    info.setProducedMessageInfoExists(outputTopics);
    return info;
  }

  private AdminClient kafkaAdminClient() {
    return KafkaAdminClient.create(makeProperties());
  }

  private Properties makeProperties() {
    Properties props = new Properties();

    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, getBrokerUrl());
    props.put(AdminClientConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());

    return props;
  }

  private String getBrokerUrl() {
    String env = System.getenv("SP_DEBUG");
    if (env == null) {
      env = "true";
    }
    System.out.println(System.getenv("SP_DEBUG"));
    if ("true".equals(env.replaceAll(" ", ""))) {
      return "localhost:9094";
    } else {
      return BackendConfig.INSTANCE.getKafkaUrl();
    }
  }

  private Tuple2<String, Long> makeTopicOffsetInfo(KafkaTransportProtocol protocol, OffsetSpec offsetSpec) throws ExecutionException, InterruptedException {
    Map<TopicPartition, OffsetAndMetadata> partitions = kafkaAdminClient.listConsumerGroupOffsets(protocol.getGroupId()).partitionsToOffsetAndMetadata().get();
    Map<TopicPartition, OffsetSpec> desiredOffsets = new HashMap<>();
    partitions.forEach((key, value) -> desiredOffsets.put(key, offsetSpec));
    ListOffsetsResult offsetsResult = kafkaAdminClient.listOffsets(desiredOffsets);
    Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> resultInfo = offsetsResult.all().get();
    Long offset = resultInfo
            .values()
            .stream()
            .map(ListOffsetsResult.ListOffsetsResultInfo::offset)
            .reduce(0L, Long::sum);

    return new Tuple2<>(protocol.getTopicDefinition().getActualTopicName(), offset);
  }

  private Tuple2<String, Long> makeTopicInfo(KafkaTransportProtocol protocol) throws ExecutionException, InterruptedException {
    Long offset = kafkaAdminClient.listConsumerGroupOffsets(protocol.getGroupId())
            .partitionsToOffsetAndMetadata()
            .get()
            .values()
            .stream()
            .map(OffsetAndMetadata::offset)
            .reduce(0L, Long::sum);

    return new Tuple2<>(protocol.getGroupId(), offset);
  }

  public List<TransportProtocol> extractProtocols(InvocableStreamPipesEntity pipelineElement) {
    return pipelineElement
            .getInputStreams()
            .stream()
            .map(stream -> stream.getEventGrounding().getTransportProtocol())
            .collect(Collectors.toList());
  }

  public static void main(String[] args) {
    List<Pipeline> pipelines = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().getAllPipelines();
    Pipeline testPipeline = pipelines.get(0);

    for(int i = 0; i < 50; i++) {
      List<PipelineElementMonitoringInfo> monitoringInfo = new TopicInfoCollector(testPipeline).makeMonitoringInfo();
      System.out.println(monitoringInfo.size());
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
