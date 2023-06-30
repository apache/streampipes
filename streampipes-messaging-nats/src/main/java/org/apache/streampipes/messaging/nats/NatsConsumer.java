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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.nats.NatsConfig;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Subscription;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class NatsConsumer extends AbstractNatsConnector implements EventConsumer {

  private Dispatcher dispatcher;
  private Subscription subscription;
  private NatsConfig natsConfig;

  public NatsConsumer(NatsTransportProtocol protocol) {
    this.natsConfig = makeNatsConfig(protocol);
  }

  public NatsConsumer(NatsConfig natsConfig) {
    this.natsConfig = natsConfig;
  }

  public void connect(NatsConfig natsConfig,
                      InternalEventProcessor<byte[]> eventProcessor) throws IOException, InterruptedException {
    this.natsConfig = natsConfig;
    connect(eventProcessor);
  }

  @Override
  public void connect(InternalEventProcessor<byte[]> eventProcessor) throws SpRuntimeException {
    try {
      makeBrokerConnection(natsConfig);
      createSubscription(eventProcessor);
    } catch (IOException | InterruptedException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    try {
      dispatcher.unsubscribe(this.subscription);
      super.disconnect();
    } catch (InterruptedException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean isConnected() {
    return natsConnection != null && natsConnection.getStatus() == Connection.Status.CONNECTED;
  }

  private void createSubscription(InternalEventProcessor<byte[]> eventProcessor) {
    dispatcher = natsConnection.createDispatcher((message) -> {});

    this.subscription = dispatcher.subscribe(subject, (message) ->
        eventProcessor.onEvent(message.getData()));
  }
}
