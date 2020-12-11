/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.node.controller.container.management.relay;

import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.node.controller.container.management.relay.bridges.*;

public abstract class BaseEventRelay {

    protected final MultiBrokerBridge<?,?> multiBrokerBridge;

    public BaseEventRelay(TransportProtocol source, TransportProtocol target, String relayStrategy) {
        this.multiBrokerBridge = getBrokerToBrokerRelaySetup(source, target, relayStrategy);
    }

    private MultiBrokerBridge<?,?> getBrokerToBrokerRelaySetup(TransportProtocol source, TransportProtocol target,
                                                               String relayStrategy) {
        if (source instanceof MqttTransportProtocol && target instanceof MqttTransportProtocol) {
            return new MqttToMqttBridge((MqttTransportProtocol) source, (MqttTransportProtocol) target, relayStrategy);
        } else if (source instanceof MqttTransportProtocol && target instanceof JmsTransportProtocol) {
            return new MqttToJmsBridge((MqttTransportProtocol) source, (JmsTransportProtocol) target, relayStrategy);
        } else if (source instanceof MqttTransportProtocol && target instanceof KafkaTransportProtocol) {
            return new MqttToKafkaBridge((MqttTransportProtocol) source, (KafkaTransportProtocol) target, relayStrategy);
        } else if (source instanceof JmsTransportProtocol && target instanceof JmsTransportProtocol) {
            return new JmsToJmsBridge((JmsTransportProtocol) source, (JmsTransportProtocol) target, relayStrategy);
        } else if (source instanceof JmsTransportProtocol && target instanceof MqttTransportProtocol) {
            return new JmsToMqttBridge((JmsTransportProtocol) source, (MqttTransportProtocol) target, relayStrategy);
        } else if (source instanceof JmsTransportProtocol && target instanceof KafkaTransportProtocol) {
            return new JmsToKafkaBridge((JmsTransportProtocol) source, (KafkaTransportProtocol) target, relayStrategy);
        } else if (source instanceof KafkaTransportProtocol && target instanceof KafkaTransportProtocol) {
            return new KafkaToKafkaBridge((KafkaTransportProtocol) source, (KafkaTransportProtocol) target, relayStrategy);
        } else if (source instanceof KafkaTransportProtocol && target instanceof MqttTransportProtocol) {
            return new KafkaToMqttBridge((KafkaTransportProtocol) source, (MqttTransportProtocol) target, relayStrategy);
        } else if (source instanceof KafkaTransportProtocol && target instanceof JmsTransportProtocol) {
            return new KafkaToJmsBridge((KafkaTransportProtocol) source, (JmsTransportProtocol) target, relayStrategy);
        }
        throw new IllegalArgumentException("TransportProtocol not valid. source=" + source + ", target=" + target);
    }
}
