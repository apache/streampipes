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

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.net.ServerSocket;
import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfSystemProperty(named = "streampipes.nats.integration", matches = "true")
public class NatsRecoveryIntegrationTest {

  @Test
  public void recoversInitialFailureBrokerRestartAndClosedConnections() throws Exception {
    int port;
    try (var socket = new ServerSocket(0)) {
      port = socket.getLocalPort();
    }
    try (var broker = new GenericContainer<>("nats:latest")
        .withExposedPorts(4222)
        // Docker may allocate a different ephemeral host port on start after stop.
        .withCreateContainerCmdModifier(command -> command.getHostConfig().withPortBindings(
            new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(4222))))
        .waitingFor(Wait.forLogMessage(".*Server is ready.*", 1))) {
      broker.start();
      var protocol = new NatsTransportProtocol(broker.getHost(), broker.getMappedPort(4222), "recovery.events");
      var publisher = new NatsPublisher(protocol);
      var consumer = new NatsConsumer(protocol);
      var events = new LinkedBlockingQueue<byte[]>();
      var docker = broker.getDockerClient();
      try {
        docker.stopContainerCmd(broker.getContainerId()).withTimeout(0).exec();
        publisher.connect();
        consumer.connect(events::add);
        assertFalse(publisher.isConnected());
        assertFalse(consumer.isConnected());

        docker.startContainerCmd(broker.getContainerId()).exec();
        await(() -> publisher.isConnected() && consumer.isConnected());
        assertDelivery(publisher, consumer, events, new byte[] {1});

        // Exercise terminal closure separately from JNATS's ordinary reconnect path.
        var oldPublisher = publisher.natsConnection;
        var oldConsumer = consumer.natsConnection;
        oldPublisher.close();
        oldConsumer.close();
        await(() -> publisher.natsConnection != oldPublisher && consumer.natsConnection != oldConsumer
            && publisher.isConnected() && consumer.isConnected());
        assertDelivery(publisher, consumer, events, new byte[] {2});

        docker.stopContainerCmd(broker.getContainerId()).withTimeout(0).exec();
        await(() -> !publisher.isConnected() && !consumer.isConnected());
        docker.startContainerCmd(broker.getContainerId()).exec();
        await(() -> publisher.isConnected() && consumer.isConnected());
        assertDelivery(publisher, consumer, events, new byte[] {3});
      } finally {
        publisher.disconnect();
        consumer.disconnect();
      }
    }
  }

  private void assertDelivery(NatsPublisher publisher,
                              NatsConsumer consumer,
                              LinkedBlockingQueue<byte[]> events,
                              byte[] payload) throws Exception {
    // A connected socket alone does not establish that the subscription reached the server.
    consumer.natsConnection.flush(Duration.ofSeconds(5));
    publisher.publish(payload);
    assertArrayEquals(payload, events.poll(5, TimeUnit.SECONDS));
  }

  private void await(BooleanSupplier condition) throws InterruptedException {
    long deadline = System.nanoTime() + Duration.ofSeconds(20).toNanos();
    while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
      Thread.sleep(50);
    }
    assertTrue(condition.getAsBoolean(), "NATS connection did not reach the expected state");
  }
}
