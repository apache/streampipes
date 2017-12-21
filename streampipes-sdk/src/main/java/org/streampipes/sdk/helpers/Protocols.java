package org.streampipes.sdk.helpers;

import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;

/**
 * Created by riemer on 28.01.2017.
 */
public class Protocols {

  /**
   * Defines the transport protocol Kafka used by a data stream at runtime.
   * @param kafkaHost The hostname of any Kafka broker
   * @param kafkaPort The port of any Kafka broker
   * @param topic The topic identifier
   * @return The {@link org.streampipes.model.grounding.KafkaTransportProtocol} containing URL and topic where data
   * arrives.
   */
  public static KafkaTransportProtocol kafka(String kafkaHost, Integer kafkaPort, String topic) {
    return new KafkaTransportProtocol(kafkaHost, kafkaPort, topic, kafkaHost, kafkaPort);
  }

  /**
   * Defines the transport protocol Kafka used by a data stream at runtime.
   * @param jmsHost The hostname of any JMS broker
   * @param jmsPort The port of any JMS broker
   * @param topic The topic identifier
   * @return The {@link org.streampipes.model.grounding.KafkaTransportProtocol} containing URL and topic where data
   * arrives.
   */
  public static JmsTransportProtocol jms(String jmsHost, Integer jmsPort, String topic) {
    return new JmsTransportProtocol(jmsHost, jmsPort, topic);
  }
}
