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

package org.apache.streampipes.messaging.nats;

import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.nats.NatsConfig;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public abstract class AbstractNatsConnector {

  protected Connection natsConnection;
  protected String subject;

  protected void makeBrokerConnection(NatsConfig natsConfig) throws IOException, InterruptedException {
    Options options = NatsUtils.makeNatsOptions(natsConfig);
    this.natsConnection = Nats.connect(options);
    this.subject = natsConfig.getSubject();
  }

  protected void makeBrokerConnection(NatsTransportProtocol protocol) throws IOException, InterruptedException {
    this.natsConnection = Nats.connect(makeBrokerUrl(protocol));
    this.subject = protocol.getTopicDefinition().getActualTopicName();
  }

  protected NatsConfig makeNatsConfig(NatsTransportProtocol protocol) {
    var natsConfig = new NatsConfig();
    natsConfig.setNatsUrls(makeBrokerUrl(protocol));
    natsConfig.setSubject(protocol.getTopicDefinition().getActualTopicName());

    return natsConfig;
  }

  protected void disconnect() throws InterruptedException, TimeoutException {
    natsConnection.flush(Duration.ofMillis(50));
    natsConnection.close();
  }

  private String makeBrokerUrl(NatsTransportProtocol protocolSettings) {
    return "nats://" + protocolSettings.getBrokerHostname() + ":" + protocolSettings.getPort();
  }

}
