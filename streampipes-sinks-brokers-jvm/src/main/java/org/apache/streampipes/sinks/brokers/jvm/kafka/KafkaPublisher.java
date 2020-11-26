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

package org.apache.streampipes.sinks.brokers.jvm.kafka;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.messaging.kafka.SpKafkaProducer;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class KafkaPublisher implements EventSink<KafkaParameters> {

  private SpKafkaProducer producer;
  private JsonDataFormatDefinition dataFormatDefinition;

  public KafkaPublisher() {
    this.dataFormatDefinition = new JsonDataFormatDefinition();
  }

  @Override
  public void onInvocation(KafkaParameters parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    boolean useAuthentication = parameters.getAuthentication().equals(KafkaController.getSaslAccessKey());
    if (useAuthentication) {
      this.producer = new SpKafkaProducer(parameters.getKafkaHost() + ":" + parameters.getKafkaPort(),
              parameters.getTopic(),
              parameters.getUsername(),
              parameters.getPassword());
    }
    else {
      this.producer = new SpKafkaProducer(parameters.getKafkaHost() + ":" + parameters.getKafkaPort(),
              parameters.getTopic());
    }
  }

  @Override
  public void onEvent(Event inputEvent) {
    try {
      Map<String, Object> event = inputEvent.getRaw();
      producer.publish(dataFormatDefinition.fromMap(event));
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.producer.disconnect();
  }
}
