package org.streampipes.messaging.kafka;

import org.streampipes.messaging.SpProtocolDefinition;
import org.streampipes.messaging.SpProtocolDefinitionFactory;
import org.streampipes.model.impl.KafkaTransportProtocol;

public class SpKafkaProtocolFactory extends SpProtocolDefinitionFactory<KafkaTransportProtocol> {

  @Override
  public String getTransportProtocolClass() {
    return KafkaTransportProtocol.class.getCanonicalName();
  }

  @Override
  public SpProtocolDefinition<KafkaTransportProtocol> createInstance() {
    return new SpKafkaProtocol();
  }
}
