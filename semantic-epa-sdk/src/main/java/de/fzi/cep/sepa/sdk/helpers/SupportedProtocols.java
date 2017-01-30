package de.fzi.cep.sepa.sdk.helpers;

import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;

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
