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

package org.apache.streampipes.integration.adapters;

import org.apache.streampipes.integration.containers.MosquittoContainer;
import org.apache.streampipes.messaging.mqtt.MqttPublisher;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MQTTPublisherUtils {

    public static void publishEvents(MqttPublisher publisher, List<Map<String, Object>> events) {
        var objectMapper = new ObjectMapper();

        events.forEach(event -> {

            try {
                var serializedEvent = objectMapper.writeValueAsBytes(event);
                publisher.publish(serializedEvent);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        publisher.disconnect();
    }

    @NotNull
    public static MqttPublisher getMqttPublisher(MosquittoContainer mosquittoContainer, String topic) {
        MqttTransportProtocol mqttSettings = new MqttTransportProtocol(
                mosquittoContainer.getBrokerHost(),
                mosquittoContainer.getBrokerPort(),
                topic);
        MqttPublisher publisher = new MqttPublisher(mqttSettings);
        publisher.connect();
        return publisher;
    }

}
