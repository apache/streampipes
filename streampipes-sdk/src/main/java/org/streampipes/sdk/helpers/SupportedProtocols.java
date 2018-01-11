package org.streampipes.sdk.helpers;

import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;

public class SupportedProtocols {

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messages arriving from a
   * Kafka broker.
   * @return The {@link org.streampipes.model.grounding.KafkaTransportProtocol}.
   */
  public static KafkaTransportProtocol kafka() {
    return new KafkaTransportProtocol();
  }

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messages arriving from a
   * JMS broker.
   * @return The {@link org.streampipes.model.grounding.JmsTransportProtocol}.
   */
  public static JmsTransportProtocol jms() {
    return new JmsTransportProtocol();
  }

}
