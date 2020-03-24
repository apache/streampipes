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

import org.apache.streampipes.connect.adapter.util.TransportFormatGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.Collections;
import java.util.UUID;

public class GroundingService {

    public static String extractBroker(AdapterDescription adapterDescription) {
        EventGrounding eventGrounding = getEventGrounding(adapterDescription);

        String host = eventGrounding.getTransportProtocol().getBrokerHostname();
        int port = 0;
        if ("true".equals(System.getenv("SP_NODE_BROKER"))) {
            port = ((JmsTransportProtocol) eventGrounding.getTransportProtocol()).getPort();
        }
        else {
            port = ((KafkaTransportProtocol) eventGrounding.getTransportProtocol()).getKafkaPort();
        }

        return host + ":" + port;
    }

    public static String extractTopic(AdapterDescription adapterDescription) {
        EventGrounding eventGrounding = getEventGrounding(adapterDescription);
        return eventGrounding.getTransportProtocol().getTopicDefinition().getActualTopicName();
    }

    private static EventGrounding getEventGrounding(AdapterDescription adapterDescription) {
        EventGrounding eventGrounding = null;

        if (adapterDescription instanceof SpecificAdapterSetDescription) {
            eventGrounding = ((SpecificAdapterSetDescription) adapterDescription).getDataSet().getEventGrounding();
        } else if (adapterDescription instanceof GenericAdapterSetDescription) {
            eventGrounding = ((GenericAdapterSetDescription) adapterDescription).getDataSet().getEventGrounding();
        } else {
            eventGrounding = adapterDescription.getEventGrounding();
        }

        return eventGrounding;
    }

    public static EventGrounding createEventGrounding(String host, int port, EventSchema eventSchema) {
        EventGrounding eventGrounding = new EventGrounding();

        if ("true".equals(System.getenv("SP_NODE_BROKER"))) {
            JmsTransportProtocol transportProtocol = new JmsTransportProtocol();
            transportProtocol.setBrokerHostname(host);
            transportProtocol.setPort(port);

            String topic = "org.apache.streampipes.connect." + UUID.randomUUID().toString();
            TopicDefinition topicDefinition = new SimpleTopicDefinition(topic);
            transportProtocol.setTopicDefinition(topicDefinition);

            eventGrounding.setTransportProtocol(transportProtocol);
            eventGrounding.setTransportFormats(Collections
                    .singletonList(TransportFormatGenerator.getTransportFormat()));
        }
        else {
            KafkaTransportProtocol transportProtocol = new KafkaTransportProtocol();
            transportProtocol.setBrokerHostname(host);
            transportProtocol.setKafkaPort(port);

            String topic = "org.apache.streampipes.connect." + UUID.randomUUID().toString();
            TopicDefinition topicDefinition = new SimpleTopicDefinition(topic);
            transportProtocol.setTopicDefinition(topicDefinition);

            eventGrounding.setTransportProtocol(transportProtocol);
            eventGrounding.setTransportFormats(Collections
                    .singletonList(TransportFormatGenerator.getTransportFormat()));
        }

        return eventGrounding;
    }
}
