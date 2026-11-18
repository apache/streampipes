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
package org.apache.streampipes.extensions.connectors.mqtt.shared;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Timeout(5)
class MqttConsumerTest {

  private final Mqtt3AsyncClient client = mock(Mqtt3AsyncClient.class);
  private final CompletableFuture<Mqtt3ConnAck> connect = new CompletableFuture<>();
  private final CompletableFuture<Mqtt3SubAck> subscribe = new CompletableFuture<>();
  private BooleanSupplier reconnectAllowed;
  private MqttConsumer consumer;

  @BeforeEach
  void setUp() {
    when(client.connect(any())).thenReturn(connect);
    when(client.subscribe(any(), any(java.util.function.Consumer.class))).thenReturn(subscribe);
    when(client.disconnect()).thenReturn(CompletableFuture.completedFuture(null));
    consumer = new MqttConsumer(new MqttConfig("tcp://localhost:1883", "test"), event -> {}, Duration.ofMillis(20)) {
      @Override
      protected Mqtt3AsyncClient setupMqttClient(BooleanSupplier allowed) {
        reconnectAllowed = allowed;
        assertFalse(allowed.getAsBoolean());
        return client;
      }
    };
  }

  @Test
  void connectionTimeoutCancelsOriginalFutureAndCleansUp() {
    var error = assertThrows(AdapterException.class, consumer::start);
    assertInstanceOf(TimeoutException.class, error.getCause());
    assertTrue(connect.isCancelled());
    assertFalse(reconnectAllowed.getAsBoolean());
    verify(client).disconnect();
  }

  @Test
  void subscriptionTimeoutCancelsOriginalFutureAndCleansUp() {
    connect.complete(mock(Mqtt3ConnAck.class));
    var error = assertThrows(AdapterException.class, consumer::start);
    assertInstanceOf(TimeoutException.class, error.getCause());
    assertTrue(subscribe.isCancelled());
    verify(client).disconnect();
    assertFalse(reconnectAllowed.getAsBoolean());
  }

  @Test
  void connectionFailurePropagatesAndCleansUp() {
    var failure = new IllegalStateException("TLS setup failed");
    connect.completeExceptionally(failure);
    assertSame(failure, assertThrows(AdapterException.class, consumer::start).getCause());
    verify(client).disconnect();
    assertFalse(reconnectAllowed.getAsBoolean());
  }

  @Test
  void subscriptionFailurePropagatesAndCleansUp() {
    connect.complete(mock(Mqtt3ConnAck.class));
    var failure = new IllegalStateException("Subscription rejected");
    subscribe.completeExceptionally(failure);
    assertSame(failure, assertThrows(AdapterException.class, consumer::start).getCause());
    verify(client).disconnect();
  }

  @Test
  void interruptionCancelsConnectionAndPreservesFlag() {
    Thread.currentThread().interrupt();
    try {
      assertThrows(AdapterException.class, consumer::start);
      assertTrue(Thread.currentThread().isInterrupted());
      assertTrue(connect.isCancelled());
      verify(client).disconnect();
    } finally {
      Thread.interrupted();
    }
  }

  @Test
  void successfulStartupAllowsReconnectUntilStopped() throws Exception {
    connect.complete(mock(Mqtt3ConnAck.class));
    subscribe.complete(mock(Mqtt3SubAck.class));
    consumer.start();
    assertTrue(reconnectAllowed.getAsBoolean());

    consumer.stop();
    consumer.stop();

    assertFalse(reconnectAllowed.getAsBoolean());
    verify(client, times(1)).disconnect();
  }
}
