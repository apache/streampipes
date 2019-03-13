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

package org.streampipes.connect.adapter.generic.pipeline.elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.streampipes.connect.adapter.GroundingService;
import org.streampipes.connect.adapter.generic.pipeline.AdapterPipelineElement;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportProtocol;

import java.util.Map;

public class SendToKafkaAdapterSink implements AdapterPipelineElement  {
    private SpKafkaProducer producer;
    private ObjectMapper objectMapper;


    // TODO Handle multiple Event Groundings and define what happens when none is provided
    public SendToKafkaAdapterSink(AdapterDescription adapterDescription) {
        String brokerUrl = GroundingService.extractBroker(adapterDescription);
        String topic = GroundingService.extractTopic(adapterDescription);


//        producer = new SpKafkaProducer(brokerUrl, topic);
        producer = new SpKafkaProducer();
        KafkaTransportProtocol kafkaTransportProtocol = (KafkaTransportProtocol) adapterDescription.getEventGrounding().getTransportProtocol();
        producer.connect(kafkaTransportProtocol);
        objectMapper = new ObjectMapper();
    }

    @Override
    public Map<String, Object> process(Map<String, Object> event) {
        try {
            if (event != null) {
                producer.publish(objectMapper.writeValueAsBytes(event));
                System.out.println("send to kafka: " + event);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void changeTransportProtocol(TransportProtocol transportProtocol) {
        producer.disconnect();
        producer.connect((KafkaTransportProtocol) transportProtocol);
    }
}
