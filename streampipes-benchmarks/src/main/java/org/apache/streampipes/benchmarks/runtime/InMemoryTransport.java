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


package org.apache.streampipes.benchmarks.runtime;

import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.messaging.SpProtocolDefinition;
import org.apache.streampipes.messaging.SpProtocolDefinitionFactory;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;

import java.util.HashMap;
import java.util.Map;

final class InMemoryTransport extends SpProtocolDefinitionFactory<NatsTransportProtocol> {

  private static final Map<String, InternalEventProcessor<byte[]>> RECIPIENTS = new HashMap<>();
  private static boolean registered;
  private static int producers;

  static void register() {
    if (!registered) {
      if (SpProtocolManager.INSTANCE.findDefinition(new NatsTransportProtocol()).isPresent()) {
        throw new IllegalStateException("Benchmark requires an isolated JVM without a registered NATS transport");
      }
      SpProtocolManager.INSTANCE.register(new InMemoryTransport());
      registered = true;
    }
    assertDisconnected();
  }

  static void deliver(String topic, byte[] event) {
    var recipient = RECIPIENTS.get(topic);
    if (recipient == null) {
      throw new AssertionError("No recipient for " + topic);
    }
    try {
      recipient.onEvent(event);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new AssertionError("Benchmark delivery interrupted", e);
    }
  }

  static void assertDisconnected() {
    if (!RECIPIENTS.isEmpty() || producers != 0) {
      throw new AssertionError("Runtime left transport connections open");
    }
  }

  @Override
  public NatsTransportProtocol getTransportProtocol() {
    return new NatsTransportProtocol();
  }

  @Override
  public String getTransportProtocolClass() {
    return NatsTransportProtocol.class.getCanonicalName();
  }

  @Override
  public SpProtocolDefinition<NatsTransportProtocol> createInstance() {
    return new SpProtocolDefinition<>() {
      @Override
      public EventConsumer getConsumer(NatsTransportProtocol protocol) {
        String topic = protocol.getTopicDefinition().getActualTopicName();
        return new EventConsumer() {
          private boolean connected;

          @Override
          public void connect(InternalEventProcessor<byte[]> processor) {
            if (RECIPIENTS.putIfAbsent(topic, processor) != null) {
              throw new AssertionError("Duplicate recipient for " + topic);
            }
            connected = true;
          }

          @Override
          public void disconnect() {
            RECIPIENTS.remove(topic);
            connected = false;
          }

          @Override
          public boolean isConnected() {
            return connected;
          }
        };
      }

      @Override
      public EventProducer getProducer(NatsTransportProtocol protocol) {
        String topic = protocol.getTopicDefinition().getActualTopicName();
        return new EventProducer() {
          private boolean connected;

          @Override
          public void connect() {
            connected = true;
            producers++;
          }

          @Override
          public void publish(byte[] event) {
            if (!connected) {
              throw new AssertionError("Producer is disconnected");
            }
            deliver(topic, event);
          }

          @Override
          public void disconnect() {
            connected = false;
            producers--;
          }

          @Override
          public boolean isConnected() {
            return connected;
          }
        };
      }
    };
  }
}
