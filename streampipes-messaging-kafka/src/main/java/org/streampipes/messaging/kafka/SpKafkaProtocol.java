package org.streampipes.messaging.kafka;

import org.streampipes.messaging.EventConsumer;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.SpProtocolDefinition;
import org.streampipes.model.impl.KafkaTransportProtocol;

public class SpKafkaProtocol implements SpProtocolDefinition<KafkaTransportProtocol> {

  private EventConsumer<KafkaTransportProtocol> kafkaConsumer;
  private EventProducer<KafkaTransportProtocol> kafkaProducer;

  public SpKafkaProtocol() {
    this.kafkaConsumer = new SpKafkaConsumer();
    this.kafkaProducer = new SpKafkaProducer();
  }

  @Override
  public EventConsumer<KafkaTransportProtocol> getConsumer() {
    return kafkaConsumer;
  }

  @Override
  public EventProducer<KafkaTransportProtocol> getProducer() {
    return kafkaProducer;
  }
}
