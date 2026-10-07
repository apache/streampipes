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

package org.apache.streampipes.wrapper.standalone.routing;

import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.SpProtocolDefinition;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.wrapper.standalone.manager.PManager;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StandaloneCollectorShutdownTest {

  private final NatsTransportProtocol protocol = new NatsTransportProtocol("localhost", 4222, "events");
  private EventConsumer consumer;
  private EventProducer producer;
  private MockedStatic<PManager> definitions;
  private MockedStatic<ProtocolManager> collectors;

  @BeforeEach
  public void setUp() {
    consumer = mock(EventConsumer.class);
    producer = mock(EventProducer.class);
    definitions = mockStatic(PManager.class);
    collectors = mockStatic(ProtocolManager.class);
    SpProtocolDefinition<NatsTransportProtocol> definition = new SpProtocolDefinition<>() {
      @Override
      public EventConsumer getConsumer(NatsTransportProtocol protocol) {
        return consumer;
      }

      @Override
      public EventProducer getProducer(NatsTransportProtocol protocol) {
        return producer;
      }
    };
    definitions.when(() -> PManager.getProtocolDefinition(protocol)).thenReturn(Optional.of(definition));
  }

  @AfterEach
  public void tearDown() {
    collectors.close();
    definitions.close();
  }

  @Test
  public void repeatedShutdownPreservesReplacementForNatsAndKafka() {
    collectors.close();
    collectors = mockStatic(ProtocolManager.class, org.mockito.Mockito.CALLS_REAL_METHODS);
    for (TransportProtocol transport : List.of(protocol,
        new KafkaTransportProtocol("localhost", 9092, "events"))) {
      SpProtocolDefinition<TransportProtocol> definition = new SpProtocolDefinition<>() {
        @Override
        public EventConsumer getConsumer(TransportProtocol ignored) {
          return consumer;
        }

        @Override
        public EventProducer getProducer(TransportProtocol ignored) {
          return producer;
        }
      };
      definitions.when(() -> PManager.getProtocolDefinition(any())).thenReturn(Optional.of(definition));
      var input = new StandaloneSpInputCollector<>(transport, false);
      var output = new StandaloneSpOutputCollector<>(transport, "resource");
      var replacementInput = new StandaloneSpInputCollector<>(transport, false);
      var replacementOutput = new StandaloneSpOutputCollector<>(transport, "resource");
      try {
        ProtocolManager.consumers.put("events", input);
        ProtocolManager.producers.put("events", output);
        input.disconnect();
        output.disconnect();
        ProtocolManager.consumers.put("events", replacementInput);
        ProtocolManager.producers.put("events", replacementOutput);
        input.disconnect();
        output.disconnect();
        assertSame(replacementInput, ProtocolManager.consumers.get("events"));
        assertSame(replacementOutput, ProtocolManager.producers.get("events"));
      } finally {
        ProtocolManager.consumers.clear();
        ProtocolManager.producers.clear();
      }
    }
  }

  @Test
  public void inputShutdownCancelsPendingInitialConnection() {
    var collector = new StandaloneSpInputCollector<>(protocol, false);
    collector.connect();
    collector.disconnect();
    collector.disconnect();
    verify(consumer).disconnect();
    collectors.verify(() -> ProtocolManager.removeInputCollector(protocol, collector), org.mockito.Mockito.times(2));
  }

  @Test
  public void outputShutdownCancelsPendingInitialConnection() {
    var collector = new StandaloneSpOutputCollector<>(protocol, "resource");
    collector.connect();
    collector.disconnect();
    collector.disconnect();
    verify(producer).disconnect();
    collectors.verify(() -> ProtocolManager.removeOutputCollector(protocol, collector), org.mockito.Mockito.times(2));
  }

  @Test
  public void inputShutdownDuringOutageClosesPreviouslyConnectedConsumer() {
    var collector = new StandaloneSpInputCollector<>(protocol, false);
    collector.connect();
    when(consumer.isConnected()).thenReturn(true);
    collector.connect();
    when(consumer.isConnected()).thenReturn(false);
    collector.disconnect();
    verify(consumer).disconnect();
  }

  @Test
  public void outputShutdownDuringOutageClosesPreviouslyConnectedProducer() {
    var collector = new StandaloneSpOutputCollector<>(protocol, "resource");
    collector.connect();
    when(producer.isConnected()).thenReturn(true);
    collector.connect();
    when(producer.isConnected()).thenReturn(false);
    collector.disconnect();
    verify(producer).disconnect();
  }

  @Test
  public void sharedInputIsClosedOnlyAfterLastConsumerDetaches() {
    var collector = new StandaloneSpInputCollector<>(protocol, false);
    collector.connect();
    collector.registerConsumer("route", (event, size, topic) -> { });
    collector.disconnect();
    verify(consumer, never()).disconnect();
    collectors.verify(() -> ProtocolManager.removeInputCollector(protocol, collector), never());
    collector.unregisterConsumer("route");
    collector.disconnect();
    verify(consumer).disconnect();
    collectors.verify(() -> ProtocolManager.removeInputCollector(protocol, collector));
  }

  @Test
  public void neverStartedCollectorsDoNotDisconnectUninitializedTransports() {
    new StandaloneSpInputCollector<>(protocol, false).disconnect();
    new StandaloneSpOutputCollector<>(protocol, "resource").disconnect();
    verify(consumer, never()).disconnect();
    verify(producer, never()).disconnect();
  }
}
