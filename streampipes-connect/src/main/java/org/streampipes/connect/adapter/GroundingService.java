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

package org.streampipes.connect.adapter;

import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.SimpleTopicDefinition;
import org.streampipes.model.grounding.TopicDefinition;
import org.streampipes.model.schema.EventSchema;

import java.util.UUID;

public class GroundingService {

    public static String extractBroker(AdapterDescription adapterDescription) {
        EventGrounding eventGrounding = getEventGrounding(adapterDescription);

        String host = eventGrounding.getTransportProtocol().getBrokerHostname();
        int port = ((KafkaTransportProtocol) eventGrounding.getTransportProtocol()).getKafkaPort();
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

    public static EventGrounding createEventGrounding(String kafkaHost, int kafkaPort, EventSchema eventSchema) {
        EventGrounding eventGrounding = new EventGrounding();
        KafkaTransportProtocol transportProtocol = new KafkaTransportProtocol();
        transportProtocol.setBrokerHostname(kafkaHost);
        transportProtocol.setKafkaPort(kafkaPort);

        String topic = "org.streampipes.connect." + UUID.randomUUID().toString();
        TopicDefinition topicDefinition = new SimpleTopicDefinition(topic);
        transportProtocol.setTopicDefinition(topicDefinition);

        eventGrounding.setTransportProtocol(transportProtocol);


        return eventGrounding;
    }
}
