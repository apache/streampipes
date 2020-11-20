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

package org.apache.streampipes.manager.matching;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.SpProtocol;
import org.apache.streampipes.manager.util.TopicGenerator;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.List;
import java.util.Set;

public class ProtocolSelector extends GroundingSelector {

    private final String outputTopic;
    private final List<SpProtocol> prioritizedProtocols;

    public ProtocolSelector(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {
        super(source, targets);
        this.outputTopic = TopicGenerator.generateRandomTopic();
        this.prioritizedProtocols = BackendConfig.INSTANCE
                .getMessagingSettings()
                .getPrioritizedProtocols();
    }

    public TransportProtocol getPreferredProtocol() {
        if (source instanceof SpDataStream) {
            return ((SpDataStream) source)
                    .getEventGrounding()
                    .getTransportProtocol();
        } else {
            for(SpProtocol p: prioritizedProtocols) {
                if (matches(p, KafkaTransportProtocol.class) && supportsProtocol(KafkaTransportProtocol.class)) {
                    return kafkaTransportProtocol();
                } else if (matches(p, JmsTransportProtocol.class) && supportsProtocol(JmsTransportProtocol.class)) {
                    return jmsTransportProtocol();
                } else if (matches(p, MqttTransportProtocol.class) && supportsProtocol(MqttTransportProtocol.class)) {
                    return mqttTransportProtocol();
                } else {
                    throw new IllegalArgumentException("Transport protocol not found: " + p.getProtocolClass());
                }
            }
        }
        throw new IllegalArgumentException("Could not get preferred transport protocol");
    }

    private TransportProtocol mqttTransportProtocol() {
        return new MqttTransportProtocol(BackendConfig.INSTANCE.getMqttHost(),
                BackendConfig.INSTANCE.getMqttPort(),
                outputTopic);
    }

    private TransportProtocol jmsTransportProtocol() {
        return new JmsTransportProtocol(BackendConfig.INSTANCE.getJmsHost(),
                BackendConfig.INSTANCE.getJmsPort(),
                outputTopic);
    }

    private TransportProtocol kafkaTransportProtocol() {
        return new KafkaTransportProtocol(BackendConfig.INSTANCE.getKafkaHost(),
                BackendConfig.INSTANCE.getKafkaPort(),
                outputTopic,
                BackendConfig.INSTANCE.getZookeeperHost(),
                BackendConfig.INSTANCE.getZookeeperPort());
    }

    private <T extends TransportProtocol> boolean matches(SpProtocol p, Class<T> clazz) {
        return p.getProtocolClass().equals(clazz.getCanonicalName());
    }

    private <T extends TransportProtocol> boolean supportsProtocol(Class<T> protocol) {
        List<InvocableStreamPipesEntity> elements = buildInvocables();
        return elements
                .stream()
                .allMatch(e -> e
                        .getSupportedGrounding()
                        .getTransportProtocols()
                        .stream()
                        .anyMatch(protocol::isInstance));
    }
}
