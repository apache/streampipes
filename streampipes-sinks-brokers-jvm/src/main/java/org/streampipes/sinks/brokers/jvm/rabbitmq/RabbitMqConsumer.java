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
import org.streampipes.pe.shared.PlaceholderExtractor;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class RabbitMqConsumer extends EventSink<RabbitMqParameters> {

  // For testing: rabbitMQ default port is 15700 for the Axoom use case

  private RabbitMqPublisher publisher;
  private JsonDataFormatDefinition dataFormatDefinition;
  private String topic;

  private static final Logger LOG = LoggerFactory.getLogger(RabbitMqConsumer.class);

  public RabbitMqConsumer(RabbitMqParameters params) {
    super(params);
    this.dataFormatDefinition = new JsonDataFormatDefinition();
  }

  @Override
  public void bind(RabbitMqParameters parameters) throws SpRuntimeException {
    this.publisher = new RabbitMqPublisher(parameters);
    this.topic = parameters.getRabbitMqTopic();
  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {
    try {
      publisher.fire(dataFormatDefinition.fromMap(event),
              PlaceholderExtractor.replacePlaceholders(topic, event));
    } catch (SpRuntimeException e) {
      LOG.error("Could not serialiaze event");
    }
  }

  @Override
  public void discard() throws SpRuntimeException {
    publisher.cleanup();
  }
}
