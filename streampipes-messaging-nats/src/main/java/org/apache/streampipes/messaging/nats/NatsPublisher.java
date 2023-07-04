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
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;

import io.nats.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class NatsPublisher extends AbstractNatsConnector implements EventProducer {

  private final NatsTransportProtocol protocol;

  public NatsPublisher(NatsTransportProtocol protocol) {
    this.protocol = protocol;
  }

  @Override
  public void connect() throws SpRuntimeException {
    try {
      makeBrokerConnection(protocol);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void publish(byte[] event) {
    natsConnection.publish(subject, event);
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    try {
      super.disconnect();
    } catch (InterruptedException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean isConnected() {
    return natsConnection != null && natsConnection.getStatus() == Connection.Status.CONNECTED;
  }
}
