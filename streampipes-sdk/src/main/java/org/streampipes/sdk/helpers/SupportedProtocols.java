package org.streampipes.sdk.helpers;

import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;

/**
 * Created by riemer on 29.01.2017.
 */
public class SupportedProtocols {

  public static KafkaTransportProtocol kafka() {
    return new KafkaTransportProtocol();
  }

  public static JmsTransportProtocol jms() {
    return new JmsTransportProtocol();
  }

}
