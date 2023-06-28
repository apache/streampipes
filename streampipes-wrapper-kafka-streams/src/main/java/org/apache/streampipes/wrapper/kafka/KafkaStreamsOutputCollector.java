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
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.runtime.Event;

import java.util.Map;

public class KafkaStreamsOutputCollector implements SpOutputCollector {

  private String topic;

  private final EventProducer outputProducer;
  private final SpDataFormatDefinition outputFormatConverter;


  public KafkaStreamsOutputCollector(SpDataFormatDefinition outputFormatConverter,
                                     EventProducer outputProducer) {
    this.outputFormatConverter = outputFormatConverter;
    this.outputProducer = outputProducer;
  }

  @Override
  public void collect(Event event) {
    this.outputProducer.publish(outputFormatConverter.fromMap(event.getRaw()));
  }

  @Override
  public void registerConsumer(String routeId,
                               InternalEventProcessor<Map<String, Object>> consumer) {

  }

  @Override
  public void unregisterConsumer(String routeId) {

  }

  @Override
  public void connect() throws SpRuntimeException {
    this.outputProducer.connect();
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    this.outputProducer.disconnect();
  }
}
