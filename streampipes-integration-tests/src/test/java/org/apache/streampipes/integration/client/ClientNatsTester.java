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

package org.apache.streampipes.integration.client;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.integration.containers.NatsContainer;
import org.apache.streampipes.integration.containers.NatsDevContainer;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;

import java.util.Objects;

public class ClientNatsTester extends ClientLiveDataTesterBase<NatsTransportProtocol> {

  private NatsContainer natsContainer;

  @Override
  public void startContainer() {
    if (Objects.equals(System.getenv("TEST_MODE"), "dev")) {
      natsContainer = new NatsDevContainer();
    } else {
      natsContainer = new NatsContainer();
    }

    natsContainer.start();
  }

  @Override
  public NatsTransportProtocol makeProtocol() {
    var protocol = new NatsTransportProtocol();
    protocol.setBrokerHostname(natsContainer.getBrokerHost());
    protocol.setPort(natsContainer.getBrokerPort());
    protocol.setTopicDefinition(new SimpleTopicDefinition("test-topic"));

    return protocol;
  }

  @Override
  public void prepareClient(IStreamPipesClient client) {
    client.registerProtocol(new SpNatsProtocolFactory());
  }

  @Override
  public void close() throws Exception {
    natsContainer.close();
  }
}
