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
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.nats.NatsConfig;

import io.nats.client.AuthenticationException;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NatsRecoveryTest {

  @Test
  public void protocolTokenIsPreservedForPublisherAndConsumer() throws Exception {
    var protocol = new NatsTransportProtocol("localhost", 4222, "events");
    protocol.setToken(" token,with:delimiters ");
    var connection = connection();
    when(connection.createDispatcher(any(MessageHandler.class))).thenReturn(mock(Dispatcher.class));
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenAnswer(invocation -> {
        Options options = invocation.getArgument(0);
        assertEquals(protocol.getToken(), new String(options.getToken()));
        assertEquals(-1, options.getMaxReconnect());
        return connection;
      });
      var publisher = new NatsPublisher(protocol);
      var consumer = new NatsConsumer(protocol);
      try {
        publisher.connect();
        consumer.connect(mock(InternalEventProcessor.class));
        nats.verify(() -> Nats.connect(any(Options.class)), times(2));
      } finally {
        publisher.disconnect();
        consumer.disconnect();
      }
    }
  }

  @Test
  public void publisherRecoversAfterInitialFailureAndTerminalClosure() throws Exception {
    var executor = mock(ScheduledExecutorService.class);
    var publisher = publisher(executor);
    var first = connection();
    var replacement = connection();
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class)))
          .thenThrow(new IOException("broker unavailable"))
          .thenReturn(first)
          .thenThrow(new IOException("broker still unavailable"))
          .thenReturn(replacement);

      publisher.connect();
      assertFalse(publisher.isConnected());
      assertThrows(SpRuntimeException.class, () -> publisher.publish(new byte[0]));
      var recover = recoveryTask(executor);
      recover.run();
      assertTrue(publisher.isConnected());
      publisher.publish(new byte[] {1});
      verify(first).publish(eq("events"), any(byte[].class));

      when(first.getStatus()).thenReturn(Connection.Status.CLOSED);
      recover.run();
      assertFalse(publisher.isConnected());
      recover.run();
      publisher.publish(new byte[] {2});
      verify(replacement).publish(eq("events"), any(byte[].class));
      assertTrue(publisher.isConnected());
      publisher.disconnect();
    }
  }

  @Test
  public void ordinaryReconnectIsLeftToJnatsAndStopPreventsRecovery() throws Exception {
    var executor = mock(ScheduledExecutorService.class);
    var publisher = publisher(executor);
    var connection = connection();
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenReturn(connection);
      publisher.connect();
      publisher.connect();
      var recover = recoveryTask(executor);
      when(connection.getStatus()).thenReturn(Connection.Status.RECONNECTING);
      recover.run();
      publisher.publish(new byte[0]);
      verify(connection).publish(eq("events"), any(byte[].class));
      publisher.disconnect();
      publisher.disconnect();
      when(connection.getStatus()).thenReturn(Connection.Status.CLOSED);
      recover.run();
      nats.verify(() -> Nats.connect(any(Options.class)), times(1));
      verify(connection).close();
      verify(executor).shutdownNow();
      assertFalse(publisher.isConnected());
      assertThrows(SpRuntimeException.class, () -> publisher.publish(new byte[0]));
    }
  }

  @Test
  public void consumerRestoresSubscriptionAndCallbackOnReplacement() throws Exception {
    var executor = mock(ScheduledExecutorService.class);
    var consumer = new NatsConsumer(new NatsTransportProtocol("localhost", 4222, "events")) {
      @Override
      protected ScheduledExecutorService createRecoveryExecutor() {
        return executor;
      }
    };
    var first = connection();
    var replacement = connection();
    var firstDispatcher = mock(Dispatcher.class);
    var replacementDispatcher = mock(Dispatcher.class);
    when(first.createDispatcher(any(MessageHandler.class))).thenReturn(firstDispatcher);
    when(replacement.createDispatcher(any(MessageHandler.class))).thenReturn(replacementDispatcher);
    InternalEventProcessor<byte[]> processor = mock(InternalEventProcessor.class);
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenReturn(first, replacement);
      consumer.connect(processor);
      verify(firstDispatcher).subscribe("events");
      when(first.getStatus()).thenReturn(Connection.Status.CLOSED);
      recoveryTask(executor).run();
      verify(replacementDispatcher).subscribe("events");
      var handler = ArgumentCaptor.forClass(MessageHandler.class);
      verify(replacement).createDispatcher(handler.capture());
      var message = mock(Message.class);
      var payload = new byte[] {3};
      when(message.getData()).thenReturn(payload);
      handler.getValue().onMessage(message);
      verify(processor).onEvent(payload);
      consumer.disconnect();
      verify(replacement).close();
    }
  }

  @Test
  public void shutdownClosesConnectionEvenIfFlushTimesOut() throws Exception {
    var publisher = publisher(mock(ScheduledExecutorService.class));
    var connection = connection();
    doThrow(new TimeoutException()).when(connection).flush(any());
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenReturn(connection);
      publisher.connect();
      publisher.disconnect();
      verify(connection).close();
    }
  }

  @Test
  public void interruptedStartupDoesNotLeaveRecoveryRunning() throws Exception {
    var executor = mock(ScheduledExecutorService.class);
    var publisher = publisher(executor);
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenThrow(new InterruptedException());
      assertThrows(SpRuntimeException.class, publisher::connect);
      assertTrue(Thread.currentThread().isInterrupted());
      verify(executor).shutdownNow();
      verify(executor, never()).scheduleWithFixedDelay(any(), anyLong(), anyLong(), any());
    } finally {
      Thread.interrupted();
    }
  }

  @Test
  public void staleRecoveryTaskCannotReplaceAConnectionAfterExplicitRestart() throws Exception {
    var oldExecutor = mock(ScheduledExecutorService.class);
    var newExecutor = mock(ScheduledExecutorService.class);
    var executors = new ArrayDeque<>(List.of(oldExecutor, newExecutor));
    var publisher = new NatsPublisher(new NatsTransportProtocol("localhost", 4222, "events")) {
      @Override
      protected ScheduledExecutorService createRecoveryExecutor() {
        return executors.remove();
      }
    };
    var first = connection();
    var second = connection();
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenReturn(first, second);
      publisher.connect();
      var oldRecovery = recoveryTask(oldExecutor);
      publisher.disconnect();
      publisher.connect();
      when(second.getStatus()).thenReturn(Connection.Status.CLOSED);
      oldRecovery.run();
      nats.verify(() -> Nats.connect(any(Options.class)), times(2));
      publisher.disconnect();
    }
  }

  @Test
  public void failedSubscriptionClosesCandidateAndRetries() throws Exception {
    var executor = mock(ScheduledExecutorService.class);
    var consumer = new NatsConsumer(new NatsTransportProtocol("localhost", 4222, "events")) {
      @Override
      protected ScheduledExecutorService createRecoveryExecutor() {
        return executor;
      }
    };
    var first = connection();
    var second = connection();
    when(first.getStatus()).thenReturn(Connection.Status.CLOSED);
    when(first.createDispatcher(any(MessageHandler.class))).thenThrow(new IllegalStateException("closed"));
    var dispatcher = mock(Dispatcher.class);
    when(second.createDispatcher(any(MessageHandler.class))).thenReturn(dispatcher);
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenReturn(first, second);
      consumer.connect(event -> { });
      verify(first).close();
      assertFalse(consumer.isConnected());
      recoveryTask(executor).run();
      verify(dispatcher).subscribe("events");
      assertTrue(consumer.isConnected());
      consumer.disconnect();
    }
  }

  @Test
  public void authenticationFailureRejectsStartupAndCancelsRecovery() throws Exception {
    var executor = mock(ScheduledExecutorService.class);
    var publisher = publisher(executor);
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenThrow(new AuthenticationException("invalid token"));
      assertThrows(SpRuntimeException.class, publisher::connect);
      verify(executor).shutdown();
      verify(executor, never()).scheduleWithFixedDelay(any(), anyLong(), anyLong(), any());
      assertFalse(publisher.isConnected());
    }
  }

  @Test
  public void invalidSubscriptionRejectsStartupAndClosesCandidate() throws Exception {
    var executor = mock(ScheduledExecutorService.class);
    var consumer = new NatsConsumer(new NatsTransportProtocol("localhost", 4222, "invalid subject")) {
      @Override
      protected ScheduledExecutorService createRecoveryExecutor() {
        return executor;
      }
    };
    var connection = connection();
    var dispatcher = mock(Dispatcher.class);
    when(connection.createDispatcher(any(MessageHandler.class))).thenReturn(dispatcher);
    when(dispatcher.subscribe("invalid subject")).thenThrow(new IllegalArgumentException("invalid subject"));
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class))).thenReturn(connection);
      assertThrows(SpRuntimeException.class, () -> consumer.connect(event -> { }));
      verify(connection).close();
      verify(executor).shutdown();
      verify(executor, never()).scheduleWithFixedDelay(any(), anyLong(), anyLong(), any());
      assertFalse(consumer.isConnected());
    }
  }

  @Test
  public void permanentFailureDuringRecoveryCancelsFurtherAttempts() throws Exception {
    var executor = mock(ScheduledExecutorService.class);
    var publisher = publisher(executor);
    try (var nats = mockStatic(Nats.class)) {
      nats.when(() -> Nats.connect(any(Options.class)))
          .thenThrow(new IOException("offline"))
          .thenThrow(new AuthenticationException("invalid token"));
      publisher.connect();
      var recover = recoveryTask(executor);
      assertThrows(SpRuntimeException.class, recover::run);
      recover.run();
      nats.verify(() -> Nats.connect(any(Options.class)), times(2));
      verify(executor).shutdown();
    }
  }

  @Test
  public void optionsDefaultToUnlimitedReconnectsAndPreserveOverrides() {
    var config = new NatsConfig();
    config.setNatsUrls("nats://localhost:4222");
    assertEquals(-1, NatsUtils.makeNatsOptions(config).getMaxReconnect());
    config.setProperties(Options.PROP_MAX_RECONNECT + ":7," + Options.PROP_RECONNECT_WAIT + ":5000");
    var options = NatsUtils.makeNatsOptions(config);
    assertEquals(7, options.getMaxReconnect());
    assertEquals(5000, options.getReconnectWait().toMillis());
  }

  private NatsPublisher publisher(ScheduledExecutorService executor) {
    return new NatsPublisher(new NatsTransportProtocol("localhost", 4222, "events")) {
      @Override
      protected ScheduledExecutorService createRecoveryExecutor() {
        return executor;
      }
    };
  }

  private Connection connection() {
    var connection = mock(Connection.class);
    when(connection.getStatus()).thenReturn(Connection.Status.CONNECTED);
    return connection;
  }

  private Runnable recoveryTask(ScheduledExecutorService executor) {
    var task = ArgumentCaptor.forClass(Runnable.class);
    verify(executor).scheduleWithFixedDelay(task.capture(), anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS));
    return task.getValue();
  }
}
