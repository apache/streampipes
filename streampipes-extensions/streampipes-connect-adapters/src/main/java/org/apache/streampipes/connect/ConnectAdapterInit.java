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
import org.apache.streampipes.connect.adapters.image.stream.ImageStreamAdapter;
import org.apache.streampipes.connect.adapters.iss.IssAdapter;
import org.apache.streampipes.connect.adapters.netio.NetioMQTTAdapter;
import org.apache.streampipes.connect.adapters.netio.NetioRestAdapter;
import org.apache.streampipes.connect.adapters.slack.SlackAdapter;
import org.apache.streampipes.connect.adapters.ti.TISensorTag;
import org.apache.streampipes.connect.adapters.wikipedia.WikipediaEditedArticlesAdapter;
import org.apache.streampipes.connect.adapters.wikipedia.WikipediaNewArticlesAdapter;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;

public class ConnectAdapterInit extends ExtensionsModelSubmitter {

  public static void main(String[] args) {
    new ConnectAdapterInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("connect-adapter",
            "StreamPipes Connect Worker Main",
            "", 8001)
//        .registerAdapter(new GdeltAdapter())
        .registerAdapter(new CoindeskBitcoinAdapter())
        .registerAdapter(new NetioRestAdapter())
        .registerAdapter(new NetioMQTTAdapter())
//        .registerAdapter(new IexCloudNewsAdapter())
//        .registerAdapter(new IexCloudStockAdapter())
        .registerAdapter(new SlackAdapter())
        .registerAdapter(new WikipediaEditedArticlesAdapter())
        .registerAdapter(new WikipediaNewArticlesAdapter())
        .registerAdapter(new ImageStreamAdapter())
        .registerAdapter(new IssAdapter())
        .registerAdapter(new FlicMQTTAdapter())
        .registerAdapter(new TISensorTag())
        .build();
  }

}
