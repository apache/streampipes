/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.wrapper.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.wrapper.kafka.converter.JsonToMapFormat;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.runtime.PipelineElementRuntime;

import java.util.Map;
import java.util.Properties;

public abstract class KafkaStreamsDataProcessorRuntime<B extends EventProcessorBindingParams> extends
        PipelineElementRuntime {

  protected B bindingParams;
  private Properties config;
  private KafkaStreams streams;

  public KafkaStreamsDataProcessorRuntime(B bindingParams) {
    super();
    this.bindingParams = bindingParams;
  }

  @Override
  public void prepareRuntime() throws SpRuntimeException {
    config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaUrl(0) +":" +getKafkaPort(0));
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
  }

  @Override
  public void postDiscard() throws SpRuntimeException {

  }

  @Override
  public void bindRuntime() throws SpRuntimeException {
    try {
      prepareRuntime();
      KStreamBuilder builder = new KStreamBuilder();
      KStream<String, String> stream = builder.stream(getInputTopic(0));

      KStream<String, Map<String, Object>> mapFormat = stream.flatMapValues((ValueMapper<String, Iterable<Map<String,
              Object>>>) s -> new JsonToMapFormat().apply(s));

      getApplicationLogic(mapFormat);
      streams = new KafkaStreams(builder, config);
      streams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
        throwable.printStackTrace();
      });
      streams.start();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private String getInputTopic(Integer streamId) {
    return bindingParams.getGraph().getInputStreams().get(streamId).getEventGrounding().getTransportProtocol()
            .getTopicName();
  }

  private String getKafkaUrl(Integer streamId) {
    SpDataStream stream = bindingParams.getGraph().getInputStreams().get(streamId);
    KafkaTransportProtocol protocol = (KafkaTransportProtocol) stream.getEventGrounding().getTransportProtocol();

    return protocol.getBrokerHostname();

  }

  private Integer getKafkaPort(Integer streamId) {
    SpDataStream stream = bindingParams.getGraph().getInputStreams().get(streamId);
    KafkaTransportProtocol protocol = (KafkaTransportProtocol) stream.getEventGrounding().getTransportProtocol();

    return protocol.getKafkaPort();

  }


  @Override
  public void discardRuntime() throws SpRuntimeException {
    streams.close();
  }

  protected abstract void getApplicationLogic(KStream<String, Map<String, Object>>... inputStreams);
}
