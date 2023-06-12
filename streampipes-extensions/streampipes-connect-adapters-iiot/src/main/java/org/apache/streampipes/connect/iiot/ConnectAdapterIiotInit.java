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

package org.apache.streampipes.connect.iiot;

import org.apache.streampipes.connect.iiot.adapters.iolink.IfmAlMqttAdapter;
import org.apache.streampipes.connect.iiot.adapters.plc4x.modbus.Plc4xModbusAdapter;
import org.apache.streampipes.connect.iiot.adapters.plc4x.s7.Plc4xS7Adapter;
import org.apache.streampipes.connect.iiot.adapters.ros.RosBridgeAdapter;
import org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorAdapter;
import org.apache.streampipes.connect.iiot.protocol.stream.FileReplayAdapter;
import org.apache.streampipes.connect.iiot.protocol.stream.HttpServerProtocol;
import org.apache.streampipes.connect.iiot.protocol.stream.HttpStreamProtocol;
import org.apache.streampipes.connect.iiot.protocol.stream.KafkaProtocol;
import org.apache.streampipes.connect.iiot.protocol.stream.MqttProtocol;
import org.apache.streampipes.connect.iiot.protocol.stream.NatsProtocol;
import org.apache.streampipes.connect.iiot.protocol.stream.TubeMQProtocol;
import org.apache.streampipes.connect.iiot.protocol.stream.pulsar.PulsarProtocol;
import org.apache.streampipes.connect.iiot.protocol.stream.rocketmq.RocketMQProtocol;
import org.apache.streampipes.extensions.connectors.opcua.adapter.OpcUaAdapter;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;

public class ConnectAdapterIiotInit extends ExtensionsModelSubmitter {
  public static void main(String[] args) {
    new ConnectAdapterIiotInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("connect-adapter-iiot",
            "StreamPipes connect worker containing adapters relevant for the IIoT",
            "",
            8001)
        .registerAdapter(new MachineDataSimulatorAdapter())
        .registerAdapter(new FileReplayAdapter())
        .registerAdapter(new IfmAlMqttAdapter())
        .registerAdapter(new RosBridgeAdapter())
        .registerAdapter(new OpcUaAdapter())
        .registerAdapter(new Plc4xS7Adapter())
        .registerAdapter(new Plc4xModbusAdapter())
        .registerAdapter(new KafkaProtocol())
        .registerAdapter(new MqttProtocol())
        .registerAdapter(new NatsProtocol())
        .registerAdapter(new HttpStreamProtocol())
        .registerAdapter(new PulsarProtocol())
        .registerAdapter(new RocketMQProtocol())
        .registerAdapter(new HttpServerProtocol())
        .registerAdapter(new TubeMQProtocol())
        .build();
  }
}
