package org.streampipes.sdk.helpers;

import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportFormat;

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
