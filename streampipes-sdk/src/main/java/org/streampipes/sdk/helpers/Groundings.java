package org.streampipes.sdk.helpers;

import org.streampipes.model.impl.JmsTransportProtocol;
import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.TransportFormat;

/**
 * Created by riemer on 06.12.2016.
 */
@Deprecated
public class Groundings {

    public static KafkaTransportProtocol kafkaGrounding(String kafkaHost, Integer kafkaPort, String topic) {
        return Protocols.kafka(kafkaHost, kafkaPort, topic);
    }

    public static JmsTransportProtocol jmsGrounding(String jmsHost, Integer jmsPort, String topic) {
        return Protocols.jms(jmsHost, jmsPort, topic);
    }

    public static TransportFormat jsonFormat() {
        return Formats.jsonFormat();
    }

    public static TransportFormat thriftFormat() {
        return Formats.thriftFormat();
    }
}
