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

package org.apache.streampipes.sinks.brokers.jvm.mqtt;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;
import org.fusesource.mqtt.client.*;

import java.net.URISyntaxException;
import java.util.Map;

public class MqttPublisher implements EventSink<MqttParameters> {

    private MQTT client;
    private String topic;
    private BlockingConnection connection;
    private JsonDataFormatDefinition dataFormatDefinition;
    private MqttParameters params;

    public MqttPublisher() {
        this.dataFormatDefinition = new JsonDataFormatDefinition();
    }

    @Override
    public void onInvocation(MqttParameters params, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
        this.client = new MQTT();
        this.topic = params.getTopic();
        this.params = params;

        try {
            client.setHost(params.getMqttHost() + ":" + params.getMqttPort());
            this.connection = client.blockingConnection();
            this.connection.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!this.connection.isConnected()) {
            throw new SpRuntimeException("Could not connect to MQTT server: " + params.getMqttHost() + " on Port: " + params.getMqttPort() + " to topic: " + params.getTopic());
        }
    }

    @Override
    public void onEvent(Event inputEvent) {

        Map<String, Object> event = inputEvent.getRaw();

        try {
            this.connection.publish(this.topic, new String(dataFormatDefinition.fromMap(event)).getBytes(), QoS.AT_LEAST_ONCE, false);
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() throws SpRuntimeException {
        try {
            this.connection.disconnect();
        } catch (Exception e) {
            throw new SpRuntimeException("Could not disconnect from MQTT server: " + params.getMqttHost() + " on Port: " + params.getMqttPort() + " to topic: " + params.getTopic(), e);
        }
    }
}