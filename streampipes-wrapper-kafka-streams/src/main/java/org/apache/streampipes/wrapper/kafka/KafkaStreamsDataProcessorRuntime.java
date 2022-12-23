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
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.kafka.converter.JsonToMapFormat;
import org.apache.streampipes.wrapper.kafka.converter.MapToJsonFormat;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.Map;
import java.util.regex.Pattern;

public abstract class KafkaStreamsDataProcessorRuntime<T extends
    EventProcessorBindingParams>
    extends KafkaStreamsRuntime<EventProcessorRuntimeParams<T>, T,
    DataProcessorInvocation, EventProcessorRuntimeContext> {


  public KafkaStreamsDataProcessorRuntime(EventProcessorRuntimeParams<T> runtimeParams) {
    super(runtimeParams);
  }


  @Override
  public void bindRuntime() throws SpRuntimeException {
    try {
      prepareRuntime();
      StreamsBuilder builder = new StreamsBuilder();
      SpDataStream inputStream = runtimeParams.getBindingParams().getGraph().getInputStreams().get(0);
      TransportProtocol protocol = protocol(inputStream);
      KStream<String, String> stream;

      if (protocol.getTopicDefinition() instanceof SimpleTopicDefinition) {
        stream = builder.stream(getTopic(inputStream));
      } else {
        stream = builder.stream(Pattern.compile(replaceWildcardWithPatternFormat(getTopic(inputStream))));
      }

      KStream<String, Map<String, Object>> mapFormat = stream.flatMapValues((ValueMapper<String, Iterable<Map<String,
          Object>>>) s -> new JsonToMapFormat(getGraph()).apply(s));

      KStream<String, String> outStream = getApplicationLogic(mapFormat).flatMapValues(new
          MapToJsonFormat());
      outStream.to(getTopic(runtimeParams.getBindingParams().getGraph()
          .getOutputStream()));
      streams = new KafkaStreams(builder.build(), config);

      streams.start();
    } catch (Exception e) {
      throw new SpRuntimeException(e.getMessage());
    }
  }

}
