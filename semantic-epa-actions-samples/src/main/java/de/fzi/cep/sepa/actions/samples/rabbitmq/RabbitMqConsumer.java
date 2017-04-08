package de.fzi.cep.sepa.actions.samples.rabbitmq;

import de.fzi.cep.sepa.actions.samples.util.PlaceholderExtractor;
import de.fzi.cep.sepa.messaging.EventListener;

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
