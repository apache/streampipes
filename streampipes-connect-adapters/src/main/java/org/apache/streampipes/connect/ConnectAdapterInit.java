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

package org.apache.streampipes.connect;

import org.apache.streampipes.connect.adapters.coindesk.CoindeskBitcoinAdapter;
import org.apache.streampipes.connect.adapters.flic.FlicMQTTAdapter;
import org.apache.streampipes.connect.adapters.gdelt.GdeltAdapter;
import org.apache.streampipes.connect.adapters.iex.IexCloudNewsAdapter;
import org.apache.streampipes.connect.adapters.iex.IexCloudStockAdapter;
import org.apache.streampipes.connect.adapters.image.set.ImageSetAdapter;
import org.apache.streampipes.connect.adapters.image.stream.ImageStreamAdapter;
import org.apache.streampipes.connect.adapters.influxdb.InfluxDbSetAdapter;
import org.apache.streampipes.connect.adapters.influxdb.InfluxDbStreamAdapter;
import org.apache.streampipes.connect.adapters.iss.IssAdapter;
import org.apache.streampipes.connect.adapters.mysql.MySqlSetAdapter;
import org.apache.streampipes.connect.adapters.mysql.MySqlStreamAdapter;
import org.apache.streampipes.connect.adapters.netio.NetioMQTTAdapter;
import org.apache.streampipes.connect.adapters.netio.NetioRestAdapter;
import org.apache.streampipes.connect.adapters.opcua.OpcUaAdapter;
import org.apache.streampipes.connect.adapters.plc4x.modbus.Plc4xModbusAdapter;
import org.apache.streampipes.connect.adapters.plc4x.s7.Plc4xS7Adapter;
import org.apache.streampipes.connect.adapters.ros.RosBridgeAdapter;
import org.apache.streampipes.connect.adapters.simulator.machine.MachineDataStreamAdapter;
import org.apache.streampipes.connect.adapters.simulator.random.RandomDataSetAdapter;
import org.apache.streampipes.connect.adapters.simulator.random.RandomDataStreamAdapter;
import org.apache.streampipes.connect.adapters.slack.SlackAdapter;
import org.apache.streampipes.connect.adapters.ti.TISensorTag;
import org.apache.streampipes.connect.adapters.wikipedia.WikipediaEditedArticlesAdapter;
import org.apache.streampipes.connect.adapters.wikipedia.WikipediaNewArticlesAdapter;
import org.apache.streampipes.connect.container.worker.init.AdapterWorkerContainer;
import org.apache.streampipes.connect.protocol.set.FileProtocol;
import org.apache.streampipes.connect.protocol.set.HttpProtocol;
import org.apache.streampipes.connect.protocol.stream.*;
import org.apache.streampipes.connect.protocol.stream.pulsar.PulsarProtocol;
import org.apache.streampipes.container.model.SpServiceDefinition;
import org.apache.streampipes.container.model.SpServiceDefinitionBuilder;

public class ConnectAdapterInit extends AdapterWorkerContainer {

  public static void main(String[] args) {
    new ConnectAdapterInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("connect-worker-main",
            "StreamPipes Connect Worker Main",
            "",8098)
            .registerAdapter(new GdeltAdapter())
            .registerAdapter(new CoindeskBitcoinAdapter())
            .registerAdapter(new IexCloudNewsAdapter())
            .registerAdapter(new IexCloudStockAdapter())
            .registerAdapter(new MySqlStreamAdapter())
            .registerAdapter(new MySqlSetAdapter())
            .registerAdapter(new RandomDataSetAdapter())
            .registerAdapter(new RandomDataStreamAdapter())
            .registerAdapter(new MachineDataStreamAdapter())
            .registerAdapter(new SlackAdapter())
            .registerAdapter(new WikipediaEditedArticlesAdapter())
            .registerAdapter(new WikipediaNewArticlesAdapter())
            .registerAdapter(new RosBridgeAdapter())
            .registerAdapter(new OpcUaAdapter())
            .registerAdapter(new InfluxDbStreamAdapter())
            .registerAdapter(new InfluxDbSetAdapter())
            .registerAdapter(new TISensorTag())
            .registerAdapter(new NetioRestAdapter())
            .registerAdapter(new NetioMQTTAdapter())
            .registerAdapter(new Plc4xS7Adapter())
            .registerAdapter(new Plc4xModbusAdapter())
            .registerAdapter(new ImageStreamAdapter())
            .registerAdapter(new ImageSetAdapter())
            .registerAdapter(new IssAdapter())
            .registerAdapter(new FlicMQTTAdapter())

            .registerAdapter(new FileProtocol())
            .registerAdapter(new HttpProtocol())
            .registerAdapter(new FileStreamProtocol())
            .registerAdapter(new HDFSProtocol())
            .registerAdapter(new KafkaProtocol())
            .registerAdapter(new MqttProtocol())
            .registerAdapter(new HttpStreamProtocol())
            .registerAdapter(new PulsarProtocol())
            .registerAdapter(new HttpServerProtocol())

            .build();
  }

}
