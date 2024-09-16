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

package org.apache.streampipes.extensions.iiot.minimal;

import org.apache.streampipes.connect.iiot.IIoTAdaptersExtensionModuleExport;
import org.apache.streampipes.extensions.connectors.influx.InfluxConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.mqtt.MqttConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.nats.NatsConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.opcua.OpcUaConnectorsModuleExport;
import org.apache.streampipes.extensions.connectors.plc.PlcConnectorsModuleExport;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.processors.changedetection.jvm.ChangeDetectionExtensionModuleExport;
import org.apache.streampipes.processors.enricher.jvm.EnricherExtensionModuleExport;
import org.apache.streampipes.processors.filters.jvm.FilterExtensionModuleExport;
import org.apache.streampipes.processors.siddhi.SiddhiFilterExtensionModuleExport;
import org.apache.streampipes.processors.transformation.jvm.TransformationExtensionModuleExport;
import org.apache.streampipes.service.extensions.StreamPipesExtensionsServiceBase;
import org.apache.streampipes.sinks.brokers.jvm.BrokerSinksExtensionModuleExport;
import org.apache.streampipes.sinks.databases.jvm.DatabaseSinksExtensionModuleExport;
import org.apache.streampipes.sinks.internal.jvm.InternalSinksExtensionModuleExports;
import org.apache.streampipes.sinks.notifications.jvm.NotificationsExtensionModuleExport;
import org.apache.streampipes.wrapper.standalone.runtime.StandaloneStreamPipesRuntimeProvider;

public class ExtensionsIIoTMinimalInit extends StreamPipesExtensionsServiceBase {

  public static void main(String[] args) {
    new ExtensionsIIoTMinimalInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org.apache.streampipes.extensions.iiot.minimal",
            "StreamPipes Extensions (Minimal extensions for edge deployments)",
            "", 8090)
        .registerModules(
            new IIoTAdaptersExtensionModuleExport(),
            new InfluxConnectorsModuleExport(),
            new MqttConnectorsModuleExport(),
            new NatsConnectorsModuleExport(),
            new OpcUaConnectorsModuleExport(),
            new PlcConnectorsModuleExport(),

            new ChangeDetectionExtensionModuleExport(),
            new EnricherExtensionModuleExport(),
            new FilterExtensionModuleExport(),
            new SiddhiFilterExtensionModuleExport(),
            new FilterExtensionModuleExport(),
            new TransformationExtensionModuleExport(),
            new BrokerSinksExtensionModuleExport(),
            new DatabaseSinksExtensionModuleExport(),
            new InternalSinksExtensionModuleExports(),
            new NotificationsExtensionModuleExport())
        .registerRuntimeProvider(new StandaloneStreamPipesRuntimeProvider())
        .registerMessagingProtocols(
            new SpNatsProtocolFactory(),
            new SpMqttProtocolFactory()
        )
        .build();
  }
}
