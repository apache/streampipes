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
package org.apache.streampipes.extensions.all.jvm;

import org.apache.streampipes.connect.GeneralAdaptersExtensionModuleExport;
import org.apache.streampipes.connect.iiot.IIoTAdaptersExtensionModuleExport;
import org.apache.streampipes.connectors.ros.RosConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.influx.InfluxConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.kafka.KafkaConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.mqtt.MqttConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.nats.NatsConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.opcua.OpcUaConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.plc.PlcConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.pulsar.PulsarConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.rocketmq.RocketMqConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.tubemq.TubeMQConnectorsModuleExport;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.messaging.pulsar.SpPulsarProtocolFactory;
import org.apache.streampipes.processors.changedetection.jvm.ChangeDetectionExtensionModuleExport;
import org.apache.streampipes.processors.enricher.jvm.EnricherExtensionModuleExport;
import org.apache.streampipes.processors.filters.jvm.FilterExtensionModuleExport;
import org.apache.streampipes.processors.geo.jvm.GeoExtensionModuleExport;
import org.apache.streampipes.processors.imageprocessing.jvm.ImageProcessingExtensionModuleExport;
import org.apache.streampipes.processors.siddhi.SiddhiFilterExtensionModuleExport;
import org.apache.streampipes.processors.textmining.jvm.TextMiningExtensionModuleExport;
import org.apache.streampipes.processors.transformation.jvm.TransformationExtensionModuleExport;
import org.apache.streampipes.service.extensions.StreamPipesExtensionsServiceBase;
import org.apache.streampipes.sinks.brokers.jvm.BrokerSinksExtensionModuleExport;
import org.apache.streampipes.sinks.databases.jvm.DatabaseSinksExtensionModuleExport;
import org.apache.streampipes.sinks.internal.jvm.InternalSinksExtensionModuleExports;
import org.apache.streampipes.sinks.notifications.jvm.NotificationsExtensionModuleExport;
import org.apache.streampipes.wrapper.standalone.runtime.StandaloneStreamPipesRuntimeProvider;


public class AllExtensionsInit extends StreamPipesExtensionsServiceBase {

  public static void main(String[] args) {
    new AllExtensionsInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org.apache.streampipes.extensions.all.jvm",
            "StreamPipes Extensions (JVM)",
            "", 8090)
        .registerModules(
            new GeneralAdaptersExtensionModuleExport(),
            new IIoTAdaptersExtensionModuleExport(),

            new InfluxConnectorsModuleExport(),
            new KafkaConnectorsModuleExport(),
            new MqttConnectorsModuleExport(),
            new NatsConnectorsModuleExport(),
            new OpcUaConnectorsModuleExport(),
            new PlcConnectorsModuleExport(),
            new PulsarConnectorsModuleExport(),
            new RocketMqConnectorsModuleExport(),
            new RosConnectorsModuleExport(),
            new TubeMQConnectorsModuleExport(),

            new ChangeDetectionExtensionModuleExport(),
            new EnricherExtensionModuleExport(),
            new FilterExtensionModuleExport(),
            new SiddhiFilterExtensionModuleExport(),
            new FilterExtensionModuleExport(),
            new GeoExtensionModuleExport(),
            new ImageProcessingExtensionModuleExport(),
            new TextMiningExtensionModuleExport(),
            new TransformationExtensionModuleExport(),
            new BrokerSinksExtensionModuleExport(),
            new DatabaseSinksExtensionModuleExport(),
            new InternalSinksExtensionModuleExports(),
            new NotificationsExtensionModuleExport()
        )
        .registerRuntimeProvider(new StandaloneStreamPipesRuntimeProvider())
        .registerMessagingProtocols(
            new SpKafkaProtocolFactory(),
            new SpJmsProtocolFactory(),
            new SpMqttProtocolFactory(),
            new SpNatsProtocolFactory(),
            new SpPulsarProtocolFactory())
        .build();
  }
}
