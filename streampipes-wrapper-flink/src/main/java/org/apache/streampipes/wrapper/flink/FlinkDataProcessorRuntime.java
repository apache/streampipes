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

package org.apache.streampipes.wrapper.flink;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.flink.converter.EventToMapConverter;
import org.apache.streampipes.wrapper.flink.serializer.ByteArraySerializer;
import org.apache.streampipes.wrapper.flink.sink.JmsFlinkProducer;
import org.apache.streampipes.wrapper.flink.sink.MqttFlinkProducer;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public abstract class FlinkDataProcessorRuntime<T extends EventProcessorBindingParams> extends
    FlinkRuntime<EventProcessorRuntimeParams<T>, T,
        DataProcessorInvocation, EventProcessorRuntimeContext> {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(FlinkDataProcessorRuntime.class);


  public FlinkDataProcessorRuntime(T params,
                                   ConfigExtractor configExtractor,
                                   StreamPipesClient streamPipesClient) {
    super(params, configExtractor, streamPipesClient);
  }

  @SuppressWarnings("deprecation")
  public void appendExecutionConfig(DataStream<Event>... convertedStream) {
    DataStream<Map<String, Object>> applicationLogic = getApplicationLogic(convertedStream).flatMap
        (new EventToMapConverter());

    EventGrounding outputGrounding = getOutputStream().getEventGrounding();
    SpDataFormatDefinition outputDataFormatDefinition =
        getDataFormatDefinition(outputGrounding.getTransportFormats().get(0));

    ByteArraySerializer serializer =
        new ByteArraySerializer(outputDataFormatDefinition);
    if (isKafkaProtocol(getOutputStream())) {
      applicationLogic
          .addSink(new FlinkKafkaProducer<>(getTopic(getOutputStream()),
              serializer,
              getProducerProperties((KafkaTransportProtocol) outputGrounding.getTransportProtocol())));
    } else if (isJmsProtocol(getOutputStream())) {
      applicationLogic
          .addSink(new JmsFlinkProducer(getJmsProtocol(getOutputStream()), serializer));
    } else if (isMqttProtocol(getOutputStream())) {
      applicationLogic
          .addSink(new MqttFlinkProducer(getMqttProtocol(getOutputStream()), serializer));
    }

  }

  private SpDataStream getOutputStream() {
    return getGraph().getOutputStream();
  }

  protected abstract DataStream<Event> getApplicationLogic(DataStream<Event>... messageStream);

  protected Properties getProperties(KafkaTransportProtocol protocol) {
    Properties props = new Properties();

    String kafkaHost = protocol.getBrokerHostname();
    Integer kafkaPort = protocol.getKafkaPort();

    props.put("client.id", UUID.randomUUID().toString());
    props.put("bootstrap.servers", kafkaHost + ":" + kafkaPort);
    return props;
  }

  @Override
  protected EventProcessorRuntimeParams<T> makeRuntimeParams(ConfigExtractor configExtractor,
                                                             StreamPipesClient streamPipesClient) {
    LOG.warn("The config extractor and StreamPipes Client can currently not be accessed by"
        + " a deployed Flink program due to non-serializable classes.");
    return new EventProcessorRuntimeParams<>(bindingParams, false, null, null);
  }
}
