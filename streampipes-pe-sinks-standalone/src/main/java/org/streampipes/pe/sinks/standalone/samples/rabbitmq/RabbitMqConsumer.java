package org.streampipes.pe.sinks.standalone.samples.rabbitmq;

import org.streampipes.pe.sinks.standalone.samples.util.PlaceholderExtractor;
import org.streampipes.messaging.EventListener;

/**
 * Created by riemer on 05.04.2017.
 */
public class RabbitMqConsumer implements EventListener<byte[]> {

  private String topic;

  public RabbitMqConsumer(String topic) {
    this.topic = topic;
  }

  @Override
  public void onEvent(byte[] event) {
    RabbitMqPublisher.INSTANCE.fire(new String(event),
            PlaceholderExtractor.replacePlaceholders(topic, new String(event)));
  }
}
