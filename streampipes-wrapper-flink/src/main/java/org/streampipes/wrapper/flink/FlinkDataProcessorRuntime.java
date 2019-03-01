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

package org.streampipes.wrapper.flink;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.util.serialization.SerializationSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.flink.converter.MapGenerator;
import org.streampipes.wrapper.flink.serializer.SimpleJmsSerializer;
import org.streampipes.wrapper.flink.serializer.SimpleKafkaSerializer;
import org.streampipes.wrapper.flink.sink.FlinkJmsProducer;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public abstract class FlinkDataProcessorRuntime<B extends EventProcessorBindingParams> extends
        FlinkRuntime<EventProcessorRuntimeParams<B>, B,
        DataProcessorInvocation, EventProcessorRuntimeContext> {

  private static final long serialVersionUID = 1L;

  /**
   * @deprecated Use {@link #FlinkDataProcessorRuntime(EventProcessorBindingParams, boolean)} instead
   */
  @Deprecated
  public FlinkDataProcessorRuntime(B params) {
    super(params);
  }

  public FlinkDataProcessorRuntime(B params, boolean debug) {
    super(params, debug);
  }

  /**
   * @deprecated Use {@link #FlinkDataProcessorRuntime(EventProcessorBindingParams, boolean)} instead
   */
  public FlinkDataProcessorRuntime(B params, FlinkDeploymentConfig config) {
    super(params, config);
  }

  @SuppressWarnings("deprecation")
  public void appendExecutionConfig(DataStream<Event>... convertedStream) {
    DataStream<Map<String, Object>> applicationLogic = getApplicationLogic(convertedStream).flatMap
            (new MapGenerator());

    SerializationSchema<Map<String, Object>> kafkaSerializer = new SimpleKafkaSerializer();
    SerializationSchema<Map<String, Object>> jmsSerializer = new SimpleJmsSerializer();
    if (isKafkaProtocol(getOutputStream())) {
      applicationLogic
              .addSink(new FlinkKafkaProducer010<>(getTopic(getOutputStream()),
                      kafkaSerializer, getProperties((KafkaTransportProtocol) getOutputStream().getEventGrounding().getTransportProtocol())));
    } else {
      applicationLogic
              .addSink(new FlinkJmsProducer<>(getJmsProtocol(getOutputStream()), jmsSerializer));
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

  protected EventProcessorRuntimeParams<B> makeRuntimeParams() {
    return new EventProcessorRuntimeParams<>(bindingParams, false);
  }
}
