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
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.wrapper.context.RuntimeContext;
import org.apache.streampipes.wrapper.distributed.runtime.DistributedRuntime;
import org.apache.streampipes.wrapper.params.binding.BindingParams;
import org.apache.streampipes.wrapper.params.runtime.RuntimeParams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Map;
import java.util.Properties;

@SuppressWarnings("checkstyle:ClassTypeParameterName")
public abstract class KafkaStreamsRuntime<RP extends RuntimeParams<B, I, RC>, B extends
    BindingParams<I>, I extends InvocableStreamPipesEntity, RC extends RuntimeContext> extends
    DistributedRuntime<RP, B, I, RC> {

  Properties config;
  KafkaStreams streams;

  KafkaStreamsRuntime(RP runtimeParams) {
    super(runtimeParams);
  }

  @Override
  public void prepareRuntime() throws SpRuntimeException {
    config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, gneerateApplicationId(runtimeParams.getBindingParams()
        .getGraph()
        .getElementId()));
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaUrl(runtimeParams.getBindingParams().getGraph()
        .getInputStreams().get(0)));
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
