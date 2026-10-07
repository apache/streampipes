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
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.nats.NatsConfig;

import io.nats.client.AuthenticationException;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLHandshakeException;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public abstract class AbstractNatsConnector {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractNatsConnector.class);

  protected volatile Connection natsConnection;
  protected String subject;
  private ScheduledExecutorService recoveryExecutor;
  private NatsConnectionLog connectionLog;

  protected void makeBrokerConnection(NatsTransportProtocol protocol) {
    makeBrokerConnection(protocol, connection -> { });
  }

  protected void makeBrokerConnection(NatsTransportProtocol protocol, Consumer<Connection> initializeConnection) {
    var config = makeNatsConfig(protocol);
    var builder = new Options.Builder(NatsUtils.makeNatsOptions(config));
    if (protocol.getToken() != null && !protocol.getToken().isBlank()) {
      builder.token(protocol.getToken());
    }
    makeBrokerConnection(builder.build(), config.getSubject(), initializeConnection);
  }

  protected void makeBrokerConnection(NatsConfig config, Consumer<Connection> initializeConnection) {
    makeBrokerConnection(NatsUtils.makeNatsOptions(config), config.getSubject(), initializeConnection);
  }

  private synchronized void makeBrokerConnection(Options options, String subject,
                                                 Consumer<Connection> initializeConnection) {
    if (recoveryExecutor != null) {
      return;
    }
    this.subject = subject;
    var diagnostics = new NatsConnectionLog(getClass().getSimpleName() + " subject " + subject);
    connectionLog = diagnostics;
    var previousListener = options.getConnectionListener();
    var monitoredOptions = new Options.Builder(options)
        .errorListener(diagnostics)
        .connectionListener((connection, event) -> {
          diagnostics.connectionEvent(connection, event);
          if (previousListener != null) {
            previousListener.connectionEvent(connection, event);
          }
        }).build();
    var executor = createRecoveryExecutor();
    recoveryExecutor = executor;
    // Only replace absent or terminally closed connections. JNATS owns ordinary reconnects,
    // including its bounded reconnect buffer and restoration of existing subscriptions.
    Runnable recover = () -> recoverConnection(executor, monitoredOptions, initializeConnection);
    recover.run();
    long delay = Math.max(1, options.getReconnectWait().toMillis())
        + ThreadLocalRandom.current().nextLong(Math.max(1, options.getReconnectJitter().toMillis()));
    executor.scheduleWithFixedDelay(recover, delay, delay, TimeUnit.MILLISECONDS);
  }

  private synchronized void recoverConnection(ScheduledExecutorService executor,
                                               Options options,
                                               Consumer<Connection> initializeConnection) {
    if (recoveryExecutor != executor
        || (natsConnection != null && natsConnection.getStatus() != Connection.Status.CLOSED)) {
      return;
    }
    Connection candidate = null;
    try {
      candidate = openConnection(options);
      initializeConnection.accept(candidate);
      natsConnection = candidate;
      LOG.debug("NATS transport connection established for subject {}", subject);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      disconnect();
      throw new SpRuntimeException("Interrupted while connecting to NATS", e);
    } catch (AuthenticationException | SSLHandshakeException e) {
      throw stopRecoveryAfterFailure(e);
    } catch (IOException e) {
      connectionLog.unavailable(e.getClass().getSimpleName());
    } catch (RuntimeException e) {
      if (e instanceof IllegalStateException && candidate != null
          && candidate.getStatus() == Connection.Status.CLOSED) {
        connectionLog.unavailable("Connection closed during subscription setup");
      } else {
        throw stopRecoveryAfterFailure(e);
      }
    } finally {
      if (candidate != null && candidate != natsConnection) {
        closeConnection(candidate);
      }
    }
  }

  private SpRuntimeException stopRecoveryAfterFailure(Exception cause) {
    // This may run on the recovery worker itself. Do not interrupt candidate cleanup.
    if (recoveryExecutor != null) {
      recoveryExecutor.shutdown();
      recoveryExecutor = null;
    }
    disconnect();
    LOG.error("Stopping NATS recovery after a permanent setup failure for subject {}", subject, cause);
    return new SpRuntimeException("Could not initialize NATS transport for subject " + subject, cause);
  }

  protected Connection openConnection(Options options) throws IOException, InterruptedException {
    return Nats.connect(options);
  }

  protected ScheduledExecutorService createRecoveryExecutor() {
    return Executors.newSingleThreadScheduledExecutor(
        Thread.ofVirtual().name("streampipes-nats-recovery").factory());
  }

  protected synchronized void publishEvent(byte[] event) {
    if (recoveryExecutor == null || natsConnection == null) {
      throw new SpRuntimeException("NATS transport is not available for subject " + subject);
    }
    // Do not retry this publish: an ambiguous failure must not duplicate an event.
    natsConnection.publish(subject, event);
  }

  protected NatsConfig makeNatsConfig(NatsTransportProtocol protocol) {
    var config = new NatsConfig();
    config.setNatsUrls("nats://" + protocol.getBrokerHostname() + ":" + protocol.getPort());
    config.setSubject(protocol.getTopicDefinition().getActualTopicName());
    return config;
  }

  protected void disconnect() {
    Connection connection;
    synchronized (this) {
      if (connectionLog != null) {
        connectionLog.stop();
      }
      if (recoveryExecutor != null) {
        recoveryExecutor.shutdownNow();
        recoveryExecutor = null;
      }
      connection = natsConnection;
      natsConnection = null;
    }
    if (connection != null) {
      try {
        if (connection.getStatus() == Connection.Status.CONNECTED) {
          connection.flush(Duration.ofSeconds(1));
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (TimeoutException | RuntimeException e) {
        LOG.debug("Could not flush NATS connection during shutdown", e);
      } finally {
        closeConnection(connection);
      }
    }
  }

  private void closeConnection(Connection connection) {
    try {
      connection.close();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOG.warn("Interrupted while closing NATS connection", e);
    } catch (RuntimeException e) {
      LOG.warn("Could not close NATS connection", e);
    }
  }

  public boolean isConnected() {
    var connection = natsConnection;
    return connection != null && connection.getStatus() == Connection.Status.CONNECTED;
  }
}
