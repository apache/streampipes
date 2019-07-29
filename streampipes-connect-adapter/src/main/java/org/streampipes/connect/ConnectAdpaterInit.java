/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

package org.streampipes.connect;

import org.streampipes.connect.adapter.generic.protocol.set.HttpProtocol;
import org.streampipes.connect.adapters.coindesk.CoindeskBitcoinAdapter;
import org.streampipes.connect.adapters.gdelt.GdeltAdapter;
import org.streampipes.connect.adapters.iex.IexCloudNewsAdapter;
import org.streampipes.connect.adapters.iex.IexCloudStockAdapter;
import org.streampipes.connect.adapters.mysql.MySqlAdapter;
import org.streampipes.connect.adapters.opcua.OpcUaAdapter;
import org.streampipes.connect.adapters.ros.RosBridgeAdapter;
import org.streampipes.connect.adapters.simulator.RandomDataSetAdapter;
import org.streampipes.connect.adapters.simulator.RandomDataStreamAdapter;
import org.streampipes.connect.adapters.slack.SlackAdapter;
import org.streampipes.connect.adapters.wikipedia.WikipediaEditedArticlesAdapter;
import org.streampipes.connect.adapters.wikipedia.WikipediaNewArticlesAdapter;
import org.streampipes.connect.config.ConnectWorkerConfig;
import org.streampipes.connect.init.AdapterDeclarerSingleton;
import org.streampipes.connect.init.AdapterWorkerContainer;
import org.streampipes.connect.protocol.stream.HDFSProtocol;
import org.streampipes.connect.protocol.stream.HttpStreamProtocol;
import org.streampipes.connect.protocol.stream.KafkaProtocol;
import org.streampipes.connect.protocol.stream.MqttProtocol;

public class ConnectAdpaterInit extends AdapterWorkerContainer {

  public static void main(String[] args) {
    AdapterDeclarerSingleton
            .getInstance()

            // Protocols
//                .add(new FileProtocol())
            .add(new HttpProtocol())
//                .add(new FileStreamProtocol())
            .add(new HDFSProtocol())
            .add(new KafkaProtocol())
            .add(new MqttProtocol())
            .add(new HttpStreamProtocol())

            // Specific Adapters
            .add(new GdeltAdapter())
            .add(new CoindeskBitcoinAdapter())
            .add(new IexCloudNewsAdapter())
            .add(new IexCloudStockAdapter())
            .add(new MySqlAdapter())
            .add(new RandomDataSetAdapter())
            .add(new RandomDataStreamAdapter())
            .add(new SlackAdapter())
            .add(new WikipediaEditedArticlesAdapter())
            .add(new WikipediaNewArticlesAdapter())
            .add(new RosBridgeAdapter())
            .add(new OpcUaAdapter());

    String workerUrl = ConnectWorkerConfig.INSTANCE.getConnectContainerWorkerUrl();
    String masterUrl = ConnectWorkerConfig.INSTANCE.getConnectContainerMasterUrl();

    new ConnectAdpaterInit().init(workerUrl, masterUrl);

  }
}
