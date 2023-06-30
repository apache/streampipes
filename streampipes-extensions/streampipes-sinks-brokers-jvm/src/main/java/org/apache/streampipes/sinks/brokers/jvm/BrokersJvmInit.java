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

package org.apache.streampipes.sinks.brokers.jvm;

import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;
import org.apache.streampipes.sinks.brokers.jvm.bufferrest.BufferRestPublisherSink;
import org.apache.streampipes.sinks.brokers.jvm.jms.JmsPublisherSink;
import org.apache.streampipes.sinks.brokers.jvm.kafka.KafkaPublishSink;
import org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink;
import org.apache.streampipes.sinks.brokers.jvm.nats.NatsController;
import org.apache.streampipes.sinks.brokers.jvm.pulsar.PulsarPublisherSink;
import org.apache.streampipes.sinks.brokers.jvm.rabbitmq.RabbitMqPublisherSink;
import org.apache.streampipes.sinks.brokers.jvm.rest.RestSink;
import org.apache.streampipes.sinks.brokers.jvm.rocketmq.RocketMQPublisherSink;
import org.apache.streampipes.sinks.brokers.jvm.tubemq.TubeMQPublisherSink;
import org.apache.streampipes.sinks.brokers.jvm.websocket.WebsocketServerSink;

public class BrokersJvmInit extends ExtensionsModelSubmitter {

  public static void main(String[] args) {
    new BrokersJvmInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org.apache.streampipes.sinks.notifications.jvm",
            "Sinks Notifications JVM",
            "",
            8096)
        .registerPipelineElements(
            new KafkaPublishSink(),
            new JmsPublisherSink(),
            new RestSink(),
            new BufferRestPublisherSink(),
            new RabbitMqPublisherSink(),
            new MqttPublisherSink(),
            new WebsocketServerSink(),
            new PulsarPublisherSink(),
            new RocketMQPublisherSink(),
            new TubeMQPublisherSink(),
            new NatsController())
        .registerMessagingFormats(
            new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory())
        .registerMessagingProtocols(
            new SpKafkaProtocolFactory(),
            new SpJmsProtocolFactory(),
            new SpMqttProtocolFactory(),
            new SpNatsProtocolFactory())
        .build();
  }
}
