/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
