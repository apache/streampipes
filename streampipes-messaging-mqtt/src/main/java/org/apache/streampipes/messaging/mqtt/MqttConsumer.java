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
package org.apache.streampipes.messaging.mqtt;


import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.Serializable;

public class MqttConsumer extends AbstractMqttConnector implements
        EventConsumer,
        AutoCloseable,
        Serializable {

    protected final MqttTransportProtocol protocol;
       private static final Logger LOG = LoggerFactory.getLogger(MqttConsumer.class);

    public MqttConsumer(MqttTransportProtocol protocol) {
        super(protocol);
        this.protocol = protocol;
    }

    @Override
    public void connect(InternalEventProcessor<byte[]> eventProcessor) throws SpRuntimeException {
        try {

          LOG.info("Call to create Broker Connection from Messaging");
            this.createBrokerConnection(protocol);

            client.subscribeWith()
                    .topicFilter(protocol.getTopicDefinition().getActualTopicName())
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .callback(this::handleMessage)
                    .send()
                    .join();

            this.eventProcessor = eventProcessor;

        } catch (Exception e) {
            throw new SpRuntimeException("Error connecting to MQTT broker", e);
        }
    }

    private InternalEventProcessor<byte[]> eventProcessor;

    private void handleMessage(Mqtt3Publish publish) {
        try {
            byte[] payload = publish.getPayloadAsBytes();
            if (eventProcessor != null) {
                eventProcessor.onEvent(payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() throws SpRuntimeException {
        try {
            if (client != null && connected) {
                client.disconnect().join();
            }
        } catch (Exception e) {
            throw new SpRuntimeException("Error disconnecting from MQTT broker", e);
        } finally {
            connected = false;
        }
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }
}