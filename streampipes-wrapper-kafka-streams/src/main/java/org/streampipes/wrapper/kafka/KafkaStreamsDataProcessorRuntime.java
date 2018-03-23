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
package org.streampipes.wrapper.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.grounding.SimpleTopicDefinition;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.wrapper.kafka.converter.JsonToMapFormat;
import org.streampipes.wrapper.kafka.converter.MapToJsonFormat;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.Map;
import java.util.regex.Pattern;

public abstract class KafkaStreamsDataProcessorRuntime<B extends EventProcessorBindingParams>
        extends KafkaStreamsRuntime<B, DataProcessorInvocation> {


  public KafkaStreamsDataProcessorRuntime(B bindingParams) {
    super(bindingParams);
  }


  @Override
  public void bindRuntime() throws SpRuntimeException {
    try {
      prepareRuntime();
      StreamsBuilder builder = new StreamsBuilder();
      SpDataStream inputStream = bindingParams.getGraph().getInputStreams().get(0);
      TransportProtocol protocol = protocol(inputStream);
      KStream<String, String> stream ;

      if (protocol.getTopicDefinition() instanceof SimpleTopicDefinition) {
        stream = builder.stream(getTopic(inputStream));
      } else {
        stream = builder.stream(Pattern.compile(replaceWildcardWithPatternFormat(getTopic(inputStream))));
      }

      KStream<String, Map<String, Object>> mapFormat = stream.flatMapValues((ValueMapper<String, Iterable<Map<String,
              Object>>>) s -> new JsonToMapFormat().apply(s));

      KStream<String, String> outStream = getApplicationLogic(mapFormat).flatMapValues(new
              MapToJsonFormat());
      outStream.to(Serdes.String(), Serdes.String(), getTopic(bindingParams.getGraph().getOutputStream()));
      streams = new KafkaStreams(builder.build(), config);

      streams.start();
    } catch (Exception e) {
      throw new SpRuntimeException(e.getMessage());
    }
  }

}
