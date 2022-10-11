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

package org.apache.streampipes.connect.adapter;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.SpProtocol;
import org.apache.streampipes.connect.adapter.util.TransportFormatGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.Collections;
import java.util.UUID;

public class GroundingService {

    private static final String TOPIC_PREFIX = "org.apache.streampipes.connect.";

    public static String extractTopic(AdapterDescription adapterDescription) {
        EventGrounding eventGrounding = getEventGrounding(adapterDescription);
        return eventGrounding.getTransportProtocol().getTopicDefinition().getActualTopicName();
    }

    private static EventGrounding getEventGrounding(AdapterDescription adapterDescription) {
        EventGrounding eventGrounding;

        if (adapterDescription instanceof SpecificAdapterSetDescription) {
            eventGrounding = ((SpecificAdapterSetDescription) adapterDescription).getDataSet().getEventGrounding();
        } else if (adapterDescription instanceof GenericAdapterSetDescription) {
            eventGrounding = ((GenericAdapterSetDescription) adapterDescription).getDataSet().getEventGrounding();
        } else {
            eventGrounding = adapterDescription.getEventGrounding();
        }

        return eventGrounding;
    }

    public static EventGrounding createEventGrounding() {
        EventGrounding eventGrounding = new EventGrounding();

        String topic = TOPIC_PREFIX + UUID.randomUUID().toString();
        TopicDefinition topicDefinition = new SimpleTopicDefinition(topic);

        SpProtocol prioritizedProtocol =
                BackendConfig.INSTANCE.getMessagingSettings().getPrioritizedProtocols().get(0);

        if (isPrioritized(prioritizedProtocol, JmsTransportProtocol.class)) {
            eventGrounding.setTransportProtocol(
                    makeJmsTransportProtocol(
                            BackendConfig.INSTANCE.getJmsHost(),
                            BackendConfig.INSTANCE.getJmsPort(),
                            topicDefinition));
        } else if (isPrioritized(prioritizedProtocol, KafkaTransportProtocol.class)){
            eventGrounding.setTransportProtocol(
                    makeKafkaTransportProtocol(
                            BackendConfig.INSTANCE.getKafkaHost(),
                            BackendConfig.INSTANCE.getKafkaPort(),
                            topicDefinition));
        } else if (isPrioritized(prioritizedProtocol, MqttTransportProtocol.class)) {
            eventGrounding.setTransportProtocol(
                    makeMqttTransportProtocol(
                            BackendConfig.INSTANCE.getMqttHost(),
                            BackendConfig.INSTANCE.getMqttPort(),
                            topicDefinition));
        } else if (isPrioritized(prioritizedProtocol, NatsTransportProtocol.class)) {
            eventGrounding.setTransportProtocol(
                makeNatsTransportProtocol(
                BackendConfig.INSTANCE.getNatsHost(),
                BackendConfig.INSTANCE.getNatsPort(),
                topicDefinition));
        }

        eventGrounding.setTransportFormats(Collections
                .singletonList(TransportFormatGenerator.getTransportFormat()));

        return eventGrounding;
    }

    public static Boolean isPrioritized(SpProtocol prioritizedProtocol,
                                         Class<?> protocolClass) {
        return prioritizedProtocol.getProtocolClass().equals(protocolClass.getCanonicalName());
    }

    private static JmsTransportProtocol makeJmsTransportProtocol(String hostname, Integer port,
                                                          TopicDefinition topicDefinition) {
        JmsTransportProtocol transportProtocol = new JmsTransportProtocol();
        transportProtocol.setPort(port);
        fillTransportProtocol(transportProtocol, hostname, topicDefinition);

        return transportProtocol;
    }

    private static MqttTransportProtocol makeMqttTransportProtocol(String hostname, Integer port,
                                                                 TopicDefinition topicDefinition) {
        MqttTransportProtocol transportProtocol = new MqttTransportProtocol();
        transportProtocol.setPort(port);
        fillTransportProtocol(transportProtocol, hostname, topicDefinition);

        return transportProtocol;
    }

    private static NatsTransportProtocol makeNatsTransportProtocol(String hostname,
                                                                   int port,
                                                                   TopicDefinition topicDefinition) {
        var tp = new NatsTransportProtocol();
        tp.setPort(port);
        fillTransportProtocol(tp, hostname, topicDefinition);

        return tp;
    }

    private static KafkaTransportProtocol makeKafkaTransportProtocol(String hostname, Integer port,
                                                              TopicDefinition topicDefinition) {
        KafkaTransportProtocol transportProtocol = new KafkaTransportProtocol();
        transportProtocol.setKafkaPort(port);
        fillTransportProtocol(transportProtocol, hostname, topicDefinition);

        return transportProtocol;
    }

    private static void fillTransportProtocol(TransportProtocol protocol, String hostname,
                                       TopicDefinition topicDefinition) {
        protocol.setBrokerHostname(hostname);
        protocol.setTopicDefinition(topicDefinition);
    }
}
