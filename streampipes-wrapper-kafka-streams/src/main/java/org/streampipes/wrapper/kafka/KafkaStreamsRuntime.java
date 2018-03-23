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
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.wrapper.distributed.runtime.DistributedRuntime;
import org.streampipes.wrapper.params.binding.BindingParams;

import java.util.Map;
import java.util.Properties;

public abstract class KafkaStreamsRuntime<B extends BindingParams<I>, I extends InvocableStreamPipesEntity> extends
        DistributedRuntime<B, I> {

  Properties config;
  KafkaStreams streams;

  KafkaStreamsRuntime(B bindingParams) {
    super(bindingParams);
  }

  @Override
  public void prepareRuntime() throws SpRuntimeException {
    config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, gneerateApplicationId(params.getGraph().getElementId()));
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaUrl(bindingParams.getGraph().getInputStreams().get(0)));
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
  }

  private String gneerateApplicationId(String elementId) {
    return elementId.replaceAll("/", "-").replaceAll(":", "-");
  }

  @Override
  public void postDiscard() throws SpRuntimeException {

  }

  @Override
  public void discardRuntime() throws SpRuntimeException {
    streams.close();
  }

  protected abstract KStream<String, Map<String, Object>> getApplicationLogic(KStream<String, Map<String, Object>>...
                                                                           inputStreams);

}
