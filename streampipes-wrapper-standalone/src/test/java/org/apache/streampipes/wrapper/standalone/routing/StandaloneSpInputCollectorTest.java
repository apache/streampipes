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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.environment.variable.BooleanEnvironmentVariable;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.limiter.SpRateLimiter;
import org.apache.streampipes.extensions.api.memorymanager.SpMemoryManager;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.SpProtocolDefinition;
import org.apache.streampipes.messaging.SpProtocolDefinitionFactory;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class StandaloneSpInputCollectorTest {

  private long initialMemory;

  @BeforeAll
  static void registerProtocol() {
    SpProtocolManager.INSTANCE.register(new SpProtocolDefinitionFactory<MqttTransportProtocol>() {
      @Override
      public MqttTransportProtocol getTransportProtocol() {
        return new MqttTransportProtocol();
      }

      @Override
      public String getTransportProtocolClass() {
        return MqttTransportProtocol.class.getCanonicalName();
      }

      @Override
      public SpProtocolDefinition<MqttTransportProtocol> createInstance() {
        return new SpProtocolDefinition<>() {
          @Override
          public EventConsumer getConsumer(MqttTransportProtocol protocol) {
            return mock(EventConsumer.class);
          }

          @Override
          public EventProducer getProducer(MqttTransportProtocol protocol) {
            throw new UnsupportedOperationException();
          }
        };
      }
    });
  }

  @BeforeEach
  void setup() {
    SpRateLimiter.INSTANCE.reset();
    SpRateLimiter.INSTANCE.createRateLimiter(1.0e15, 0, TimeUnit.SECONDS);
    initialMemory = SpMemoryManager.INSTANCE.getFreeMemory();
  }

  @AfterEach
  void checkReservations() {
    SpRateLimiter.INSTANCE.reset();
    assertEquals(initialMemory, SpMemoryManager.INSTANCE.getFreeMemory());
  }

  @AfterAll
  static void shutdownScheduler() {
    SpMemoryManager.shutdown();
  }

  @Test
  void decodesOnceAndIsolatesNestedMutationsForEveryConsumer() throws InterruptedException {
    var collector = makeCollector(false, true);
    byte[] payload = new JsonDataFormatDefinition().fromMap(
        Map.of("nested", Map.of("value", 42), "list", List.of(Map.of("value", 42))));
    var delivered = new AtomicInteger();
    for (int i = 0; i < 4; i++) {
      collector.registerConsumer("consumer" + i, (event, size, topic) -> {
        assertEquals(payload.length, size);
        assertEquals("test", topic);
        assertEquals(initialMemory - payload.length, SpMemoryManager.INSTANCE.getFreeMemory());
        assertEquals(42, ((Map<?, ?>) event.get("nested")).get("value"));
        var list = (List<?>) event.get("list");
        assertEquals(42, ((Map<?, ?>) list.get(0)).get("value"));
        ((Map<?, ?>) event.get("nested")).clear();
        ((Map<?, ?>) list.get(0)).clear();
        list.clear();
        event.clear();
        delivered.incrementAndGet();
      });
    }
    collector.onEvent(payload);
    assertEquals(1, collector.decodes);
    assertEquals(4, delivered.get());
  }

  @Test
  void releasesReservationWhenDecodingFails() {
    var collector = makeCollector(false, true);
    collector.registerConsumer("consumer", (event, size, topic) -> {
      throw new AssertionError("Malformed input must not reach consumers");
    });
    assertThrows(SpRuntimeException.class, () -> collector.onEvent("invalid".getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  void releasesReservationWhenConsumerFails() {
    var collector = makeCollector(false, true);
    collector.registerConsumer("consumer", (event, size, topic) -> {
      throw new IllegalStateException("consumer failed");
    });
    assertThrows(IllegalStateException.class, () -> collector.onEvent("{}".getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  void singletonDispatchesToOneConsumerOnly() throws InterruptedException {
    var collector = makeCollector(true, true);
    var delivered = new AtomicInteger();
    for (int i = 0; i < 2; i++) {
      collector.registerConsumer("consumer" + i, (event, size, topic) -> delivered.incrementAndGet());
    }
    collector.onEvent("{}".getBytes(StandardCharsets.UTF_8));
    assertEquals(1, delivered.get());
    assertEquals(1, collector.decodes);
  }

  @Test
  void doesNothingWhenTheLastConsumerHasUnregistered() throws InterruptedException {
    var collector = makeCollector(true, true);
    collector.onEvent("invalid".getBytes(StandardCharsets.UTF_8));
    assertEquals(0, collector.decodes);
  }

  @Test
  void interruptedAdmissionDoesNotDecodeOrDispatch() {
    var collector = makeCollector(false, true);
    collector.registerConsumer("consumer", (event, size, topic) -> {
      throw new AssertionError("Interrupted event must not be dispatched");
    });
    Thread.currentThread().interrupt();
    try {
      assertThrows(InterruptedException.class, () -> collector.onEvent("{}".getBytes(StandardCharsets.UTF_8)));
      assertEquals(0, collector.decodes);
    } finally {
      Thread.interrupted();
    }
  }

  @Test
  void doesNotDispatchToAConsumerDetachedWhileWaitingForMemory() throws Exception {
    var collector = makeCollector(false, true);
    collector.registerConsumer("consumer", (event, size, topic) -> {
      throw new AssertionError("Detached consumer must not receive an event");
    });
    var executor = Executors.newSingleThreadExecutor();
    var held = SpMemoryManager.INSTANCE.reserve(initialMemory);
    try {
      var pending = executor.submit(() -> {
        collector.onEvent("{}".getBytes(StandardCharsets.UTF_8));
        return true;
      });
      assertThrows(java.util.concurrent.TimeoutException.class, () -> pending.get(50, TimeUnit.MILLISECONDS));
      collector.unregisterConsumer("consumer");
      held.close();
      assertTrue(pending.get(2, TimeUnit.SECONDS));
      assertEquals(0, collector.decodes);
    } finally {
      held.close();
      executor.shutdownNow();
      assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
    }
  }

  @Test
  void disabledLoadManagementDispatchesWithoutRateLimiterOrAvailableMemory() throws Exception {
    SpRateLimiter.INSTANCE.reset();
    var collector = makeCollector(false, false);
    var delivered = new AtomicInteger();
    for (int i = 0; i < 2; i++) {
      collector.registerConsumer("consumer" + i, (event, size, topic) -> {
        assertEquals(Map.of("value", 42), event);
        assertEquals(0, SpMemoryManager.INSTANCE.getFreeMemory());
        event.clear();
        delivered.incrementAndGet();
      });
    }
    var executor = Executors.newSingleThreadExecutor();
    try (var held = SpMemoryManager.INSTANCE.reserve(initialMemory)) {
      var pending = executor.submit(() -> {
        collector.onEvent(new JsonDataFormatDefinition().fromMap(Map.of("value", 42)));
        return true;
      });
      assertTrue(pending.get(2, TimeUnit.SECONDS));
      assertEquals(2, delivered.get());
      assertEquals(1, collector.decodes);
      assertEquals(0, SpMemoryManager.INSTANCE.getFreeMemory());
    } finally {
      executor.shutdownNow();
      assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
    }
  }

  @Test
  void skipsConsumersRemovedOrReplacedWhileAnEarlierCallbackIsRunning() throws Exception {
    for (boolean enabled : List.of(false, true)) {
      for (boolean replace : List.of(false, true)) {
        var collector = makeCollector(false, enabled);
        var staleCalls = new AtomicInteger();
        var replacementCalls = new AtomicInteger();
        collector.registerConsumer("a", (event, size, topic) -> staleCalls.incrementAndGet());
        collector.registerConsumer("b", (event, size, topic) -> staleCalls.incrementAndGet());
        var ids = collector.consumerIds();
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        collector.registerConsumer(ids.get(0), (event, size, topic) -> {
          entered.countDown();
          try {
            assertTrue(release.await(5, TimeUnit.SECONDS));
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
          }
        });
        var executor = Executors.newSingleThreadExecutor();
        byte[] payload = "{}".getBytes(StandardCharsets.UTF_8);
        try {
          var pending = executor.submit(() -> {
            collector.onEvent(payload);
            return true;
          });
          assertTrue(entered.await(5, TimeUnit.SECONDS));
          collector.unregisterConsumer(ids.get(1));
          if (replace) {
            collector.registerConsumer(ids.get(1), (event, size, topic) -> replacementCalls.incrementAndGet());
          }
          release.countDown();
          assertTrue(pending.get(5, TimeUnit.SECONDS));
          assertEquals(0, staleCalls.get());
          assertEquals(0, replacementCalls.get());
          collector.unregisterConsumer(ids.get(0));
          collector.onEvent(payload);
          assertEquals(replace ? 1 : 0, replacementCalls.get());
        } finally {
          release.countDown();
          executor.shutdownNow();
          assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
      }
    }
  }

  private TestCollector makeCollector(boolean singleton, boolean loadManagementEnabled) {
    var environment = mock(Environment.class);
    var enabled = mock(BooleanEnvironmentVariable.class);
    when(environment.getLoadManagerEnable()).thenReturn(enabled);
    when(enabled.getValueOrDefault()).thenReturn(loadManagementEnabled);
    // The flag is captured at construction, not read or allocated for each event.
    try (var environments = mockStatic(Environments.class)) {
      environments.when(Environments::getEnvironment).thenReturn(environment);
      return new TestCollector(singleton);
    }
  }

  private static class TestCollector extends StandaloneSpInputCollector<MqttTransportProtocol> {
    private int decodes;

    List<String> consumerIds() {
      return List.copyOf(consumers.keySet());
    }

    TestCollector(boolean singleton) {
      super(new MqttTransportProtocol("test.invalid", 1883, "test"), singleton);
      dataFormatDefinition = new JsonDataFormatDefinition() {
        @Override
        public Map<String, Object> toMap(byte[] event) {
          decodes++;
          return super.toMap(event);
        }
      };
    }
  }
}
