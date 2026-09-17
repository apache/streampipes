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

package org.apache.streampipes.service.extensions;

import org.apache.streampipes.model.extensions.transport.ExtensionServiceTransportMode;
import org.apache.streampipes.model.grounding.BrokerConfiguration;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestReceiver;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NatsRequestTransportTest {
  @Test
  void receiverUsesResolvedRegistrationEndpointAndToken() throws Exception {
    var broker = new BrokerConfiguration();
    broker.setProtocolId("nats");
    broker.setUrl("nats://edge.example:4422");
    broker.setToken("edge-token");
    var connection = mock(Connection.class);
    var dispatcher = mock(Dispatcher.class);
    when(connection.createDispatcher(any())).thenReturn(dispatcher);
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenAnswer(invocation -> {
        Options options = invocation.getArgument(0);
        assertEquals("nats://edge.example:4422", options.getServers().get(0).toString());
        assertEquals("edge-token", new String(options.getToken()));
        return connection;
      });
      var receiver = new ExtensionBrokerRequestReceiver(List.of());
      try {
        assertTrue(receiver.start("edge", ExtensionServiceTransportMode.NATS, "requests", null, broker));
        verify(dispatcher).subscribe(any(String.class));
      } finally {
        receiver.stop();
      }
      verify(connection).close();
    }
  }

  @Test
  void natsOnlyStartupFailsWhenReceiverCannotConnect() {
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenThrow(new IOException("unavailable"));
      var receiver = new ExtensionBrokerRequestReceiver(List.of());
      assertThrows(IllegalStateException.class,
          () -> receiver.start("edge", ExtensionServiceTransportMode.NATS, "requests"));
    }
  }

  @Test
  void dualModeCanFallBackToHttpWhenReceiverCannotConnect() {
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenThrow(new IOException("unavailable"));
      var receiver = new ExtensionBrokerRequestReceiver(List.of());
      assertFalse(receiver.start("edge", ExtensionServiceTransportMode.DUAL, "requests"));
    }
  }
}
