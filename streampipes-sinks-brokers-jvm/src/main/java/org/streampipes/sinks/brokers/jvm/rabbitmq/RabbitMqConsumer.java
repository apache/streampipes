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

package org.streampipes.sinks.brokers.jvm.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.runtime.Event;
import org.streampipes.pe.shared.PlaceholderExtractor;
import org.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class RabbitMqConsumer implements EventSink<RabbitMqParameters> {

  private RabbitMqPublisher publisher;
  private JsonDataFormatDefinition dataFormatDefinition;
  private String topic;

  private static final Logger LOG = LoggerFactory.getLogger(RabbitMqConsumer.class);

  public RabbitMqConsumer() {
    this.dataFormatDefinition = new JsonDataFormatDefinition();
  }

  @Override
  public void onInvocation(RabbitMqParameters parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    this.publisher = new RabbitMqPublisher(parameters);
    this.topic = parameters.getRabbitMqTopic();
  }

  @Override
  public void onEvent(Event inputEvent) {
    try {
      Map<String, Object> event = inputEvent.getRaw();
      publisher.fire(dataFormatDefinition.fromMap(event),
              PlaceholderExtractor.replacePlaceholders(topic, event));
    } catch (SpRuntimeException e) {
      LOG.error("Could not serialiaze event");
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    publisher.cleanup();
  }
}
