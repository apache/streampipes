package org.streampipes.messaging.kafka;

import org.streampipes.messaging.EventConsumer;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.SpProtocolDefinition;
import org.streampipes.model.impl.KafkaTransportProtocol;

public class SpKafkaProtocol implements SpProtocolDefinition<KafkaTransportProtocol> {

  @Override
  public String getTransportFormatRdfUri() {
    return KafkaTransportProtocol.class.getCanonicalName();
  }

  @Override
  public EventConsumer<KafkaTransportProtocol> getConsumer() {
    return new SpKafkaConsumer();
  }

  @Override
  public EventProducer<KafkaTransportProtocol> getProducer() {
    return new SpKafkaProducer();
  }
}
