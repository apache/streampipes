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
package org.apache.streampipes.wrapper.kafka;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.dataformat.SpDataFormatManager;
import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.runtime.IDataProcessorRuntime;
import org.apache.streampipes.messaging.SpProtocolDefinition;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.wrapper.context.generator.DataProcessorContextGenerator;
import org.apache.streampipes.wrapper.kafka.converter.JsonToMapFormat;
import org.apache.streampipes.wrapper.params.generator.DataProcessorParameterGenerator;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.Map;
import java.util.regex.Pattern;

public class KafkaStreamsDataProcessorRuntime extends KafkaStreamsRuntime<
    IStreamPipesDataProcessor,
    DataProcessorInvocation,
    EventProcessorRuntimeContext,
    IDataProcessorParameterExtractor,
    IDataProcessorParameters> implements IDataProcessorRuntime {

  private KafkaStreamsOutputCollector outputCollector;

  public KafkaStreamsDataProcessorRuntime() {
    super(new DataProcessorContextGenerator(), new DataProcessorParameterGenerator());
  }

  @Override
  public void bindRuntime() throws SpRuntimeException {
    try {
      pipelineElement.onPipelineStarted(runtimeParameters, null, runtimeContext);
      StreamsBuilder builder = new StreamsBuilder();
      SpDataStream inputStream = pipelineElementInvocation.getInputStreams().get(0);
      TransportProtocol protocol = protocol(inputStream);
      KStream<String, String> stream;

      if (protocol.getTopicDefinition() instanceof SimpleTopicDefinition) {
        stream = builder.stream(getTopic(inputStream));
      } else {
        stream = builder.stream(Pattern.compile(replaceWildcardWithPatternFormat(getTopic(inputStream))));
      }

      var outputProtocol = pipelineElementInvocation.getOutputStream().getEventGrounding().getTransportProtocol();
      if (outputProtocol instanceof KafkaTransportProtocol) {
        var outputFormatConverter = getDataFormatConverter();
        var outputProducer = getOutputProtocol().getProducer((KafkaTransportProtocol) outputProtocol);
        this.outputCollector = new KafkaStreamsOutputCollector(outputFormatConverter, outputProducer);
        this.outputCollector.connect();
      }

      stream
          .flatMapValues((ValueMapper<String, Iterable<Map<String, Object>>>) s ->
              new JsonToMapFormat(pipelineElementInvocation).apply(s))
          .foreach((key, rawEvent) -> {
            var event = internalRuntimeParameters.makeEvent(runtimeParameters, rawEvent, getTopic(inputStream));
            pipelineElement.onEvent(event, outputCollector);
          });

      streams = new KafkaStreams(builder.build(), config);

      streams.start();
    } catch (Exception e) {
      throw new SpRuntimeException(e.getMessage());
    }
  }

  @Override
  protected void afterStop() {
    this.pipelineElement.onPipelineStopped();
    this.outputCollector.disconnect();
  }

  private SpDataFormatDefinition getDataFormatConverter() {
    return SpDataFormatManager
        .INSTANCE
        .findDefinition(pipelineElementInvocation.getOutputStream().getEventGrounding().getTransportFormats().get(0))
        .orElseThrow();
  }

  private SpProtocolDefinition<KafkaTransportProtocol> getOutputProtocol() {
    return SpProtocolManager
        .INSTANCE
        .findDefinition((KafkaTransportProtocol)
            pipelineElementInvocation.getOutputStream().getEventGrounding().getTransportProtocol())
        .orElseThrow();
  }


}
