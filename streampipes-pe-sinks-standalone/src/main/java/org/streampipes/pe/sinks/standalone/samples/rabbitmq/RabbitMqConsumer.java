package org.streampipes.pe.sinks.standalone.samples.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.pe.sinks.standalone.samples.util.PlaceholderExtractor;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class RabbitMqConsumer implements EventSink<RabbitMqParameters> {

  // For testing: rabbitMQ default port is 15700 for the Axoom use case

  private RabbitMqPublisher publisher;
  private JsonDataFormatDefinition dataFormatDefinition;
  private String topic;

  private static final Logger LOG = LoggerFactory.getLogger(RabbitMqConsumer.class);

  public RabbitMqConsumer() {
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
