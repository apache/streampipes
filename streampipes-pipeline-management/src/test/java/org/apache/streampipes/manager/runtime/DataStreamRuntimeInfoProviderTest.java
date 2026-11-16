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
package org.apache.streampipes.manager.runtime;

import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.messaging.SpProtocolDefinition;
import org.apache.streampipes.messaging.SpProtocolDefinitionFactory;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.TransportProtocol;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataStreamRuntimeInfoProviderTest {

  @Test
  void snapshotsRemainStableWhileBrokerThreadPublishes() throws Exception {
    var consumer = mock(EventConsumer.class);
    var protocol = new SnapshotProtocol();
    register(protocol, consumer);
    var provider = new DataStreamRuntimeInfoProvider(Map.of("adapter", stream(protocol)));
    provider.startConsuming();
    try (var executor = Executors.newSingleThreadExecutor()) {
      ArgumentCaptor<InternalEventProcessor<byte[]>> processor = ArgumentCaptor.captor();
      verify(consumer).connect(processor.capture());
      var emptySnapshot = provider.getLatestEvents();
      executor.submit(() -> {
        processor.getValue().onEvent("{\"value\":1}".getBytes(StandardCharsets.UTF_8));
        return null;
      }).get();
      var firstSnapshot = provider.getLatestEvents();
      executor.submit(() -> {
        processor.getValue().onEvent("{\"value\":2}".getBytes(StandardCharsets.UTF_8));
        return null;
      }).get();

      assertEquals(Map.of(), emptySnapshot);
      assertEquals(Map.of("adapter", Map.of("value", 1)), firstSnapshot);
      assertEquals(Map.of("adapter", Map.of("value", 2)), provider.getLatestEvents());
      assertThrows(UnsupportedOperationException.class, () -> firstSnapshot.clear());
    } finally {
      provider.close();
    }
    verify(consumer).disconnect();
  }

  @Test
  void partialStartupFailureClosesBothConsumersEvenIfOneCloseFails() {
    var first = mock(EventConsumer.class);
    var second = mock(EventConsumer.class);
    var protocol = new FailureProtocol();
    register(protocol, first, second);
    doThrow(new IllegalStateException("Connect failed")).when(second).connect(any());
    doThrow(new IllegalStateException("Disconnect failed")).when(first).disconnect();
    var streams = new LinkedHashMap<String, SpDataStream>();
    streams.put("first", stream(protocol));
    streams.put("second", stream(protocol));
    var provider = new DataStreamRuntimeInfoProvider(streams);
    var cleanup = mock(ClientDisconnectCallback.class);

    assertThrows(IllegalStateException.class,
        () -> new RateLimitedRuntimeInfoProvider(provider, cleanup).streamOutput(new ByteArrayOutputStream()));

    verify(first).disconnect();
    verify(second).disconnect();
    verify(cleanup).onClientDisconnect();
  }

  private SpDataStream stream(TransportProtocol protocol) {
    var stream = new SpDataStream();
    var grounding = new EventGrounding();
    grounding.setTransportProtocol(protocol);
    stream.setEventGrounding(grounding);
    return stream;
  }

  @SuppressWarnings("unchecked")
  private void register(TransportProtocol protocol, EventConsumer first, EventConsumer... remaining) {
    SpProtocolDefinition<TransportProtocol> definition = mock(SpProtocolDefinition.class);
    when(definition.getConsumer(any())).thenReturn(first, remaining);
    SpProtocolManager.INSTANCE.register(new SpProtocolDefinitionFactory<TransportProtocol>() {
      @Override
      public TransportProtocol getTransportProtocol() {
        return protocol;
      }

      @Override
      public String getTransportProtocolClass() {
        return protocol.getClass().getCanonicalName();
      }

      @Override
      public SpProtocolDefinition<TransportProtocol> createInstance() {
        return definition;
      }
    });
  }

  private static class SnapshotProtocol extends TransportProtocol {
    @Override
    public String protocolId() {
      return "snapshot-test";
    }
  }

  private static class FailureProtocol extends TransportProtocol {
    @Override
    public String protocolId() {
      return "failure-test";
    }
  }
}
