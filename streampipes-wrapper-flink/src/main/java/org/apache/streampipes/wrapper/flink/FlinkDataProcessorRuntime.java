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

import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.runtime.IDataProcessorRuntime;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.wrapper.context.generator.DataProcessorContextGenerator;
import org.apache.streampipes.wrapper.flink.converter.EventToMapConverter;
import org.apache.streampipes.wrapper.flink.serializer.ByteArraySerializer;
import org.apache.streampipes.wrapper.flink.sink.JmsFlinkProducer;
import org.apache.streampipes.wrapper.flink.sink.MqttFlinkProducer;
import org.apache.streampipes.wrapper.params.generator.DataProcessorParameterGenerator;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class FlinkDataProcessorRuntime extends FlinkRuntime<
    IStreamPipesDataProcessor,
    DataProcessorInvocation,
    EventProcessorRuntimeContext,
    IDataProcessorParameterExtractor,
    IDataProcessorParameters,
    IDataProcessorProgram> implements IDataProcessorRuntime {

  public FlinkDataProcessorRuntime() {
    super(new DataProcessorContextGenerator(), new DataProcessorParameterGenerator());
  }

  private SpDataStream getOutputStream() {
    //return getGraph().getOutputStream();
    return null;
  }

  protected Properties getProperties(KafkaTransportProtocol protocol) {
    Properties props = new Properties();

    String kafkaHost = protocol.getBrokerHostname();
    int kafkaPort = protocol.getKafkaPort();

    props.put("client.id", UUID.randomUUID().toString());
    props.put("bootstrap.servers", kafkaHost + ":" + kafkaPort);
    return props;
  }

  @Override
  protected void appendExecutionConfig(IDataProcessorProgram program,
                                       DataStream<Event>... convertedStream) {
    DataStream<Map<String, Object>> applicationLogic = program.getApplicationLogic(convertedStream)
        .flatMap(new EventToMapConverter());

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

  @Override
  protected IDataProcessorProgram getFlinkProgram(IStreamPipesDataProcessor pipelineElement) {
    IDataProcessorProgram program;

    if (pipelineElement instanceof FlinkDataProcessorDeclarer<?>) {
      program = ((FlinkDataProcessorDeclarer<?>) pipelineElement)
          .getProgram(
              runtimeParameters.getModel(),
              ProcessingElementParameterExtractor.from(runtimeParameters.getModel())
          );
    } else {
      program = new FlinkDataProcessorCompatProgram(pipelineElement);
    }
    return program;
  }
}
