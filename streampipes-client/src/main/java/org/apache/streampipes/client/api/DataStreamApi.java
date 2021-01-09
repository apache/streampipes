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
package org.apache.streampipes.client.api;

import org.apache.streampipes.client.live.EventProcessor;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.dataformat.SpDataFormatFactory;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.messaging.kafka.SpKafkaConsumer;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;

import java.util.List;
import java.util.Optional;

public class DataStreamApi extends AbstractClientApi<SpDataStream> implements CRUDApi<String, SpDataStream> {

  public DataStreamApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig, SpDataStream.class);
  }

  @Override
  public SpDataStream get(String s) {
    return null;
  }

  @Override
  public List<SpDataStream> all() {
    return getAll(getBaseResourcePath());
  }

  @Override
  public void create(SpDataStream element) {

  }

  @Override
  public void delete(String s) {

  }

  @Override
  public void update(SpDataStream element) {

  }

  public void subscribe(SpDataStream stream, EventProcessor callback) {
    Optional<SpDataFormatFactory> formatConverterOpt = this
            .clientConfig
            .getRegisteredDataFormats()
            .stream()
            .filter(format -> stream
                    .getEventGrounding()
                    .getTransportFormats()
                    .get(0)
                    .getRdfType()
                    .stream()
                    .anyMatch(tf -> tf.toString().equals(format.getTransportFormatRdfUri())))
            .findFirst();

    if (formatConverterOpt.isPresent()) {
      final SpDataFormatDefinition converter = formatConverterOpt.get().createInstance();
      SpKafkaConsumer kafkaConsumer = new SpKafkaConsumer(getKafkaProtocol(stream), getOutputTopic(stream), new InternalEventProcessor<byte[]>() {
        @Override
        public void onEvent(byte[] event) {
          try {
            Event spEvent = EventFactory.fromMap(converter.toMap(event));
            callback.onEvent(spEvent);
          } catch (SpRuntimeException e) {
            e.printStackTrace();
          }
        }
      });
      Thread t = new Thread(kafkaConsumer);
      t.start();

    } else {
      throw new SpRuntimeException("No converter found for data format - did you add a format factory (client.registerDataFormat)?");
    }
  }

  private KafkaTransportProtocol getKafkaProtocol(SpDataStream stream) {
    return (KafkaTransportProtocol) stream.getEventGrounding().getTransportProtocol();
  }

  private String getOutputTopic(SpDataStream spDataStream) {
    return spDataStream
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicDefinition()
            .getActualTopicName();
  }

  @Override
  protected StreamPipesApiPath getBaseResourcePath() {
    return StreamPipesApiPath.fromUserApiPath(clientConfig.getCredentials())
            .addToPath("streams")
            .addToPath("own");
  }
}
